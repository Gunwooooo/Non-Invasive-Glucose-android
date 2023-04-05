package com.example.newnoninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityHomeBinding
import com.example.newnoninvasiveglucoseapplication.model.BodyData
import com.example.newnoninvasiveglucoseapplication.retrofit.API
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.*
import com.example.newnoninvasiveglucoseapplication.util.Constants.PROFILE_IMAGE_NAME
import com.example.newnoninvasiveglucoseapplication.util.Constants._bluetoothResultDevice
import com.example.newnoninvasiveglucoseapplication.util.Constants._checkBluetoothTimer
import org.json.JSONArray
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess


class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private var waitTime = 0L

    private val customProgressDialog by lazy { CustomDialogManager(applicationContext, R.layout.common_progress_dialog, null) }
    private val glide by lazy { Glide.with(this) }

    var getRealTimeThread: Thread? = null

    //가트 연결 변수
    var bluetoothGatt: BluetoothGatt? = null

    //Gatt 연결 상태 확인 변수
    var bluetoothGattConnected = false

    //데이터 가져오는 시간
    val REFRESH_TIME = 3000L

    //데이터 받아오는 횟수
    var dataCount = 0

    //스레드 종료 시점 구현
    var tryConnectCount = 0
    var stopFlag = false
    
    //타이머 태스크 정의 (timer 새로운 객체로 생성되는 것을 방지)
    var timerTask: TimerTask = object : TimerTask() {
        override fun run() {
            //데이터 받을 수 있도록 플래그 변경
            _checkBluetoothTimer = true
        }
    }

    //라인 데이터 전역 변수
    private val thermometerLineData by lazy { LineData(makeThermometerSet()) }
    private val heartLineData by lazy { LineData(makeHeartSet()) }
    private val glucoseLineData by lazy { LineData(makeGlucoseSet()) }


    private var bodyDataArrayList = ArrayList<BodyData>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        init()
    }
    
    override fun onResume() {
        super.onResume()

//       Home 네비만 활성화 되도록 설정
        binding.homeBottomNav.menu[0].isChecked = true

        //사용자 정보 가져오기
        retrofitInfoLoginedUser()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun init() {
        //데이터 받아오는 횟수 초기화
        dataCount = 0

        //글라이드로 모든 이미지 가져오기
        setImageViewWithGlide()

        //데이터 차트 모양 설정
        setThermometerLineChart()
        setHeartLineChart()
        setGlucoseLineChart()


        //실시간 데이터 표시
        refreshRealTimeData()

        binding.homeBtnThermometer.setOnClickListener(this)
        binding.homeBtnHeart.setOnClickListener(this)
        binding.homeBtnGlucose.setOnClickListener(this)
        binding.homeBtnAccount.setOnClickListener(this)
        binding.homeTextViewThermometerDetail.setOnClickListener(this)
        binding.homeTextViewHeartDetail.setOnClickListener(this)
        binding.homeTextViewGlucoseDetail.setOnClickListener(this)
        binding.homeImageViewSetting.setOnClickListener(this)

        //바텀 네비게이션 리스너
        binding.homeBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottomNav_dashboard -> {
                    true
                }
                R.id.bottomNav_protector -> {
                    val intent = Intent(this, HomeProtectorActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            //화면 이동 클릭 리스너
            binding.homeTextViewThermometerDetail -> {
                val intent = Intent(this, HomeThermometerAnalysisActivity::class.java)
                startActivity(intent)
            }
            binding.homeTextViewHeartDetail -> {
                val intent = Intent(this, HomeHeartAnalysisActivity::class.java)
                startActivity(intent)
            }
            binding.homeTextViewGlucoseDetail -> {
                val intent = Intent(this, HomeGlucoseAnalysisActivity::class.java)
                startActivity(intent)
            }
            binding.homeBtnThermometer -> {
                val intent = Intent(this, HomeThermometerFullChartActivity::class.java)
                startActivity(intent)
            }
            binding.homeBtnHeart -> {
                val intent = Intent(this, HomeHeartFullChartActivity::class.java)
                startActivity(intent)
            }
            binding.homeBtnGlucose -> {
                val intent = Intent(this, HomeGlucoseFullChartActivity::class.java)
                startActivity(intent)
            }
            binding.homeBtnAccount -> {
                val intent = Intent(this, HomeAccountActivity::class.java)
                startActivity(intent)
            }
            binding.homeImageViewSetting -> {
                val intent = Intent(this, HomeDeviceActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        getRealTimeThread?.interrupt()
    }

    //실시간 라인 데이터 가져오기
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshRealTimeData() {
        getRealTimeThread?.interrupt()
        val runnable = Runnable {
            //가트 연결이 해제됐을 경우 자동으로 연결 체크
            if(!bluetoothGattConnected) {
                //연결 시도 횟수 카운트
                tryConnectCount++
                
                //120번 연결 시도 했으면 종료 시키고 토스트 출력
                if(tryConnectCount == 1000) {
                    Toast.makeText(applicationContext, "기기와 연결이 해제되었습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                    stopFlag = true
                }

//                Log.d("로그", "HomeActivity - refreshRealTimeData : ####ㄴㅇㄹ##  기기명 : ${_bluetoothResultDevice} - @@ 연결 시도   #######")
                //연결됨 뷰 변경
                binding.homeLinearLayoutConnected.visibility = View.GONE
                binding.homeLinearLayoutDisconnected.visibility = View.VISIBLE

                //재연결
                bluetoothGatt = _bluetoothResultDevice.connectGatt(applicationContext, false, gattCallback)
                return@Runnable
            }

            //연결됨 표시로 변경
            binding.homeLinearLayoutConnected.visibility = View.VISIBLE
            binding.homeLinearLayoutDisconnected.visibility = View.GONE


            //최근 데이터값 가져와서 요약에 표시하도록 설정
            val thermometerDataSet = thermometerLineData.dataSets[0]
            val heartDataSet = heartLineData.dataSets[0]
            val glucoseDataSet = glucoseLineData.dataSets[0]
            if(thermometerDataSet.entryCount != 0 && heartDataSet.entryCount != 0 && glucoseDataSet.entryCount != 0) {
                val thermometer = thermometerDataSet.getEntryForIndex(thermometerDataSet.entryCount - 1).y
                val heart = heartDataSet.getEntryForIndex(heartDataSet.entryCount - 1).y
                val glucose = glucoseDataSet.getEntryForIndex(glucoseDataSet.entryCount - 1).y

                //요약 데이터 업데이트 하기
                updateSummaryValue(thermometer.toString(), heart.toString(), glucose.toString())
            }

            //시간 업데이트
            val now = LocalDateTime.now()
            binding.homeTextViewTime.text = now.format(DateTimeFormatter.ofPattern("a hh:mm"))
        }

        getRealTimeThread = Thread {
            while (!stopFlag) {
                this.runOnUiThread(runnable)
                try {
                    Thread.sleep(100)
                } catch (ie: InterruptedException) {
                    ie.printStackTrace()
                }
            }
        }
        getRealTimeThread!!.start()
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        glide.load(R.drawable.background_image_dashboard).into(binding.homeImageViewDashBoardBackground)
        glide.load(R.drawable.ic_baseline_settings_24).into(binding.homeImageViewSetting)
        glide.load(R.drawable.ic_baseline_access_time_24).into(binding.homeImageViewClock)
    }
    //=================================================================================================
    ///////////////////////////////////////블루투스 GATT 연결////////////////////////////////////////////

    //가트 콜백 생성
    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val timer = Timer()
            when(newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    //가트 연결 상태 확인 전역 변수
                    bluetoothGattConnected = true

                    //서비스디스커버 호출하기
                    Log.d("로그", "CustomBluetoothManager - onConnectionStateChange : 가트 서버 연결됨 : ${bluetoothGatt!!.discoverServices()}")

                    //5초마다 타이머로 값을 받을지 체크
                    timer.scheduleAtFixedRate(timerTask, 0, REFRESH_TIME)

                    //연결 시도 횟수 초기화
                    tryConnectCount = 0
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    bluetoothGattConnected = false
//                    Log.d("로그", "CustomBluetoothManager - onConnectionStateChange : 가트 서버에서 연결 해제됨")
                    //가트 연결 해제
                    bluetoothGatt!!.disconnect()
                    bluetoothGatt!!.close()

                    //타이머 종료
                    timer.cancel()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            when(status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.d("로그", "CustomBluetoothManager - onServicesDiscovered : 연결 성공함")
                    val str = gatt!!.getService(Constants.UART_UUID).getCharacteristic(Constants.TX_CHAR_UUID)
                    if (bluetoothGatt!!.setCharacteristicNotification(str, true)) {
                        val descriptor = str.getDescriptor(Constants.CCCD_UUID)
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        bluetoothGatt!!.writeDescriptor(descriptor)
                        broadcastUpdate("데이터 기록을 시작합니다. 잠시만 기다려주세요.\n(기기 특성상 최대 1분 정도 소요될 수 있습니다)")
                    }

                }
                BluetoothGatt.GATT_FAILURE -> {
                    broadcastUpdate("블루투스 연결에 실패하였습니다.")
                }
            }
        }

        //기기에서 오는 데이터 처리
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            //바이트 데이터 스트링으로 변환
            val data = String(characteristic!!.value)
            //w나 f면 return
            if(data == "W" || data == "F") {
//                Log.d("로그", "HomeActivity - onCharacteristicChanged : $data")
                return
            }
            //5초마다 타이머 체크 후 값 전달 받기
            if(_checkBluetoothTimer) {
                //데이터 받아오는 횟수 카운트
                dataCount++

                //현재 시간
                val dateTime = LocalDateTime.now()
                //서버로 보낼 데이터 형식
                val dateTimeString = dateTime.format(DateTimeFormatter.ISO_DATE_TIME).substring(0, 19)
                //인덱스로 사용할 데이터 형식
                val index = dateTime.toLocalTime().toSecondOfDay().toFloat()
                Log.d("로그", "HomeActivity - onCharacteristicChanged : 날짜...${index}   ${dateTimeString} - $data")

                _checkBluetoothTimer = false
                val heart = (data.split('!')[0].split('@')[1].toFloat() * 10).roundToInt() / 10.0F
                val thermometer = (data.split('/')[0].split('!')[1].toFloat() * 10).roundToInt() / 10.0F
                val glucose = 0f

                //챠트 실시간으로 업데이트
                thermometerLineData.addEntry(Entry(index, thermometer), 0)
                thermometerLineData.notifyDataChanged()
                binding.homeThermometerChart.notifyDataSetChanged()
                binding.homeThermometerChart.setVisibleXRangeMaximum(10f)
                binding.homeThermometerChart.moveViewToX(binding.homeThermometerChart.data.xMax)

                heartLineData.addEntry(Entry(index, heart), 0)
                heartLineData.notifyDataChanged()
                binding.homeHeartChart.notifyDataSetChanged()
                binding.homeHeartChart.setVisibleXRangeMaximum(10f)
                binding.homeHeartChart.moveViewToX(binding.homeHeartChart.data.xMax)

                glucoseLineData.addEntry(Entry(index, glucose), 0)
                glucoseLineData.notifyDataChanged()
                binding.homeGlucoseChart.notifyDataSetChanged()
                binding.homeGlucoseChart.setVisibleXRangeMaximum(10f)
                binding.homeGlucoseChart.moveViewToX(binding.homeGlucoseChart.data.xMax)

                //서버로 보낼 통합 리스트
                bodyDataArrayList.add(BodyData(thermometer, heart, glucose, dateTimeString))

//                바디 데이터가 10개 쌓일 때마다 서버에 보내기
                if(dataCount % 10 == 0) retrofitAddBodyData()
            }

        }

        private  fun broadcastUpdate(str:String) {
            val mHandler : Handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    Toast.makeText(applicationContext, str, Toast.LENGTH_LONG).show()
                }
            }
            mHandler.obtainMessage().sendToTarget()
        }
    }

    //요약 데이터 업데이트
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateSummaryValue(thermometer : String, heart : String, glucose : String) {
        binding.homeTextViewThermometerSummary.text = thermometer
        binding.homeTextViewThermometerValue.text = thermometer
        binding.homeTextViewHeartSummary.text = heart
        binding.homeTextViewHeartValue.text = heart
        binding.homeTextViewGlucoseSummary.text = glucose
        binding.homeTextViewGlucoseValue.text = glucose
    }

    //=================================================================================================

    //심박수 라인 데이터 생성성
    private fun makeThermometerSet() : LineDataSet {
        val thermometerLineDataSet = LineDataSet(null, "체온")
        return thermometerLineDataSet.apply {
            mode = LineDataSet.Mode.LINEAR
//            cubicIntensity = 0.2F //베지어 곡선 휘는 정도
            setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
            color = ContextCompat.getColor(applicationContext, R.color.toss_black_500)
            valueFormatter = CustomChartManager.CustomDecimalYAxisFormatter() //데이터 소수점 표시
            lineWidth = 2F //선 굵기
            circleRadius = 3F
            circleHoleRadius = 1F
            setDrawCircles(true)   //동그란거 없애기
            setDrawValues(true)
            setCircleColor(ContextCompat.getColor(applicationContext, R.color.toss_black_500))
            valueTextSize = 12F
            isHighlightEnabled = true   //클릭시 마크 보이게
            setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
            setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
            setDrawCircleHole(true)
        }
    }

    //심박수 라인 데이터 생성성
    private fun makeHeartSet() : LineDataSet {
        val heartLineDataSet = LineDataSet(null, "체온")
        return heartLineDataSet.apply {
            mode = LineDataSet.Mode.LINEAR
//            cubicIntensity = 0.2F //베지어 곡선 휘는 정도
            setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
            color = ContextCompat.getColor(applicationContext, R.color.text_red_200)
            valueFormatter = CustomChartManager.CustomDecimalYAxisFormatter()
            lineWidth = 2F //선 굵기
            circleRadius = 3F
            circleHoleRadius = 1F
            setDrawCircles(true)   //동그란거 없애기
            setDrawValues(true)
            setCircleColor(ContextCompat.getColor(applicationContext, R.color.text_red_200))
            valueTextSize = 12F
            isHighlightEnabled = true   //클릭시 마크 보이게
            setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
            setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
            setDrawCircleHole(true)
        }
    }

    //혈당 라인 데이터 생성
    private fun makeGlucoseSet() : LineDataSet {
        val glucoseLineDataSet = LineDataSet(null, "혈당")
        return glucoseLineDataSet.apply {
            mode = LineDataSet.Mode.LINEAR
//            cubicIntensity = 0.2F //베지어 곡선 휘는 정도
            setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
            color = ContextCompat.getColor(applicationContext, R.color.text_blue_200)
            valueFormatter = CustomChartManager.CustomDecimalYAxisFormatter()
            lineWidth = 2F //선 굵기
            circleRadius = 2.1F
            circleHoleRadius = 0.1F
            setDrawCircles(true)   //동그란거 없애기
            setDrawValues(true)
            setCircleColor(ContextCompat.getColor(applicationContext, R.color.text_blue_200))
            valueTextSize = 12F
            isHighlightEnabled = true   //클릭시 마크 보이게
            setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
            setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
            setDrawCircleHole(true)
        }
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    //차트 설정
    private fun setThermometerLineChart() {
        val lineThermometerDay = binding.homeThermometerChart
        //마커 뷰 설정
//        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)
        lineThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = thermometerLineData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
            isScaleXEnabled = false //가로 확대 없애기
//            setVisibleXRangeMaximum(10f)
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
//            marker = markerView

            notifyDataSetChanged()  //차트 값 변동을 감지함
//            moveViewToX((thermometerLineData.entryCount).toFloat())
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
//                labelCount = 8
//                granularity = 3f  //X축 간격
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(this, R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 10F   //최소값
                axisMaximum = 40F   //최대값
                isEnabled = true
                animateX(500)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(applicationContext, R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축
                isEnabled = false  //오른쪽 y축 없애기
            }
            legend.run {
                isEnabled = true //레전드 아이콘 표시
                form = Legend.LegendForm.CIRCLE
                textSize = 16f
                setExtraOffsets(5f, 5f, 5f, 15f)
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }

    //심박수 차트 설정
    private fun setHeartLineChart() {
        val lineChartHeart = binding.homeHeartChart
        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)
        lineChartHeart.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = heartLineData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
            isScaleXEnabled = false //가로 확대 없애기
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))

//            setVisibleXRangeMaximum(10f)
//            marker = markerView
            notifyDataSetChanged()  //차트 값 변동을 감지함
//            moveViewToX((heartLineData.entryCount).toFloat())
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
//                labelCount = 8
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(this, R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 0F   //최소값
                axisMaximum = 150F   //최대값
                isEnabled = true
                animateX(500)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(applicationContext, R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
                isEnabled = false  //오른쪽 y축 없애기
            }
            legend.run {
                isEnabled = true //레전드 아이콘 표시
                form = Legend.LegendForm.CIRCLE
                textSize = 16f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                setExtraOffsets(5f, 5f, 5f, 15f)
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }
    //혈당 차트 설정
    private fun setGlucoseLineChart() {
        val lineGlucoseBlood = binding.homeGlucoseChart
        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)
        lineGlucoseBlood.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = glucoseLineData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
            isScaleXEnabled = false //가로 확대 없애기

//            setVisibleXRangeMaximum(10f)
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
//            marker = markerView
            notifyDataSetChanged()  //차트 값 변동을 감지함
//            moveViewToX((glucoseLineData.entryCount).toFloat())
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
//                labelCount = 8
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(this, R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 0F   //최소값
                axisMaximum = 42F   //최대값
                isEnabled = true
                animateX(500)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(applicationContext, R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
                isEnabled = false  //오른쪽 y축 없애기
            }
            legend.run {
                isEnabled = true //레전드 아이콘 표시
                form = Legend.LegendForm.CIRCLE
                textSize = 16f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                setExtraOffsets(5f, 5f, 5f, 15f)
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }

    //생년월일로 나이 계산
    private fun changeBirthDayToAge(birthDay: String?) : String {
        if(birthDay?.length!! <= 5 ) return ""
        val userYear = birthDay.substring(0, 4).toInt()
        val curYear = GregorianCalendar().get(Calendar.YEAR)
        return "${curYear - userYear}세"
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //로그인 된 회원 정보 가져오기
    @SuppressLint("SetTextI18n")
    private fun retrofitInfoLoginedUser() {
        customProgressDialog.show(supportFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.infoLoginedUser(completion = { completionResponse, response ->
            customProgressDialog.dismiss()
            when (completionResponse) {
                CompletionResponse.OK -> {
                    when (response?.code()) {
                        200 -> {
                            //로그인 된 유저 데이터 제이슨으로 파싱하기
                            val jsonArray = JSONArray(response.body()?.string())
                            //유저 토큰 만료 시간 저장
                            LoginedUserClient.exp = jsonArray.getJSONObject(0).getLong("exp")
                            //유저 개인 정보 담기
                            val jsonObjectUser = jsonArray.getJSONObject(1)
                            LoginedUserClient.phoneNumber = jsonObjectUser!!.getString("phoneNumber")
                            //프로필 이미지 가져오기
                            val sb = StringBuilder()
                            sb.append(API.PHR_PROFILE_BASE_URL).append(LoginedUserClient.phoneNumber).append(PROFILE_IMAGE_NAME)
                            Log.d("로그", "HomeActivity - setImageViewWithGlide : $sb")
                            glide.load(sb.toString()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                                .placeholder(R.drawable.icon_color_profile).circleCrop().into(binding.homeImageViewProfile)
                            val nickname = jsonObjectUser.getString("nickname")
                            binding.homeTextViewNickname.text = nickname
                            if (jsonObjectUser.getString("sex").equals("T")) {
                                binding.homeTextViewSex.text = "남성"
                            } else
                                binding.homeTextViewSex.text = "여성"
                            binding.homeTextViewAge.text =
                                changeBirthDayToAge(jsonObjectUser.getString("birthDay"))
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitInfoLogineduser : 통신 실패")
                }
            }
        })
    }

    //바디 데이터 특정 개수마다 서버로 보내기
    private fun retrofitAddBodyData() {
        RetrofitManager.instance.addBodyData(bodyDataArrayList, completion = { completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        201 -> {
                            //바디 데이터 리스트 초기화 하기
                            bodyDataArrayList.clear()
                            val str = response.body()!!.string()
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeActivity - retrofitAddBodyData : 통신 실패")
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()

        //데이터 받아온 횟수 초기화
        dataCount = 0

        //가트 연결 해제
        bluetoothGattConnected = false
        bluetoothGatt!!.disconnect()
        bluetoothGatt!!.close()
    }

    //뒤로가기 키 눌렸을 때 종료
    @Override
    override fun onBackPressed() {
        if(System.currentTimeMillis() - waitTime >= 1500 ) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            //캐쉬 메모리 삭제
            for(cacheFile : File in cacheDir.listFiles()!!) {
                if(!cacheFile.isFile) continue
                cacheFile.delete()
            }

            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAffinity()
            exitProcess(0)
        }
    }
}