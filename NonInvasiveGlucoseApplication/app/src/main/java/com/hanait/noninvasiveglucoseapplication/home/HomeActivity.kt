package com.hanait.noninvasiveglucoseapplication.home

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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeBinding
import com.hanait.noninvasiveglucoseapplication.model.BodyData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.*
import com.hanait.noninvasiveglucoseapplication.util.Constants._bluetoothResultDevice
import com.hanait.noninvasiveglucoseapplication.util.Constants._bodyDataArrayList
import com.hanait.noninvasiveglucoseapplication.util.Constants._checkBluetoothTimer
import com.hanait.noninvasiveglucoseapplication.util.Constants._entryIndex
import org.json.JSONArray
import java.io.File
import java.lang.Compiler.enable
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess


class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private var waitTime = 0L

    private val customProgressDialog by lazy { CustomDialogManager(R.layout.common_progress_dialog, null) }
    var getRealTimeThread: Thread? = null

    //가트 연결 변수
    var bluetoothGatt: BluetoothGatt? = null

    //Gatt 연결 상태 확인 변수
    var bluetoothGattConnected = false

    //데이터 가져오는 시간
    val REFRESH_TIME = 1000L

    //실시간 데이터 받기 타이머
    private val timer : Timer by lazy { Timer() }

    companion object {
        lateinit var thermometerLineData : LineData
        lateinit var heartLineData : LineData
        lateinit var glucoseLineData : LineData
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

//       Home 네비만 활성화 되도록 설정
        binding.homeBottomNav.menu[0].isChecked = true

        //사용자 정보 가져오기
        retrofitInfoLoginedUser()

        //실시간 데이터 표시
        refreshRealTimeData()
    }

    @SuppressLint("MissingPermission")
    private fun init() {
        //글라이드로 모든 이미지 가져오기
        setImageViewWithGlide()

        //라인 데이터 초기화 하기
        thermometerLineData = LineData(makeThermometerSet())
        heartLineData = LineData(makeHeartSet())
        glucoseLineData = LineData(makeGlucoseSet())

        //데이터 차트 모양 설정
        setThermometerLineChart()
        setHeartLineChart()
        setGlucoseLineChart()

        //블루투스 GATT 연결하기 -> 실시간 데이터 받기
        if(!bluetoothGattConnected)
            bluetoothGatt = _bluetoothResultDevice.connectGatt(this, false, gattCallback)

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
            binding.homeTextViewThermometerDetail, binding.homeTextViewHeartDetail, binding.homeTextViewGlucoseDetail -> {
                val intent = Intent(this, HomeThermometerActivity::class.java)
                startActivity(intent)
            }
            binding.homeBtnThermometer, binding.homeBtnHeart, binding.homeBtnGlucose -> {
                val intent = Intent(this, HomeFullChartActivity::class.java)
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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshRealTimeData() {
        getRealTimeThread?.interrupt()
        val runnable = Runnable {
            binding.homeThermometerChart.notifyDataSetChanged()
//            binding.homeThermometerChart.setVisibleXRangeMaximum(6f)
//            binding.homeThermometerChart.moveViewToX((thermometerLineData.entryCount).toFloat())
            binding.homeThermometerChart.invalidate()
            binding.homeHeartChart.notifyDataSetChanged()
//            binding.homeHeartChart.setVisibleXRangeMaximum(6f)
//            binding.homeHeartChart.moveViewToX((heartLineData.entryCount).toFloat())
            binding.homeHeartChart.invalidate()
            binding.homeGlucoseChart.notifyDataSetChanged()
//            binding.homeGlucoseChart.setVisibleXRangeMaximum(6f)
//            binding.homeGlucoseChart.moveViewToX((glucoseLineData.entryCount).toFloat())
            binding.homeGlucoseChart.invalidate()

            //시간 업데이트
            val now = LocalDateTime.now()
            binding.homeTextViewTime.text = now.format(DateTimeFormatter.ofPattern("a hh:mm"))
        }

        getRealTimeThread = Thread {
            while (true) {
                this.runOnUiThread(runnable)
                try {
                    Thread.sleep(REFRESH_TIME)
                } catch (ie: InterruptedException) {
                    ie.printStackTrace()
                }
            }
        }
        getRealTimeThread!!.start()
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(this)
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
            when(newState) {
                BluetoothGatt.STATE_CONNECTED -> {


                    //가트 연결 상태 확인 전역 변수
                    bluetoothGattConnected = true

                    //서비스디스커버 호출하기
                    Log.d("로그", "CustomBluetoothManager - onConnectionStateChange : 가트 서버 연결됨 : ${bluetoothGatt!!.discoverServices()}")

                    //5초마다 타이머로 값을 받을지 체크
                    timer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            Constants._checkBluetoothTimer = true
                        }
                    }, 0, REFRESH_TIME)
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    bluetoothGattConnected = false
                    Log.d("로그", "CustomBluetoothManager - onConnectionStateChange : 가트 서버에서 연결 해제됨")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            when(status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    //연결 상태 변경

                    Log.d("로그", "CustomBluetoothManager - onServicesDiscovered : 연결 성공함")
                    val str = gatt!!.getService(Constants.UART_UUID).getCharacteristic(Constants.TX_CHAR_UUID)
                    Log.d("로그", "CustomBluetoothManager - onServicesDiscovered : $str")
                    if (bluetoothGatt!!.setCharacteristicNotification(str, true)) {
                        val descriptor = str.getDescriptor(Constants.CCCD_UUID)
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        bluetoothGatt!!.writeDescriptor(descriptor)
                        broadcastUpdate("커넥티드 한아아이티 기계")
                    }
                }
                BluetoothGatt.GATT_FAILURE -> {
                    broadcastUpdate("커넥티드 실패")
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
            if(data == "W" || data == "F") return
            //5초마다 타이머 체크 후 값 전달 받기
            if(_checkBluetoothTimer) {
                Log.d("로그", "UserSetConnectDeviceFragment - onCharacteristicChanged : $data")
                //인덱스 + 1
                _entryIndex++
                _checkBluetoothTimer = false
                val heart = data.split('!')[0].split('@')[1].toFloat()
                val thermometer = data.split('/')[0].split('!')[1].toFloat()
                val glucose = 0f

                //요약 데이터 업데이트 하기
                updateSummaryValue(String.format("%.1f", thermometer), String.format("%.1f", heart), String.format("%.1f", glucose))

                Log.d("로그", "HomeFragment - onCharacteristicChanged : ${thermometerLineData.entryCount}  ${heartLineData.entryCount}  ${glucoseLineData.entryCount}")

                //전역 배열 리스트에 각각 추가하기
                thermometerLineData.addEntry(Entry(Constants._entryIndex, thermometer), 0)
                thermometerLineData.notifyDataChanged()
                heartLineData.addEntry(Entry(Constants._entryIndex, heart), 0)
                heartLineData.notifyDataChanged()
                glucoseLineData.addEntry(Entry(Constants._entryIndex, glucose), 0)
                glucoseLineData.notifyDataChanged()
                //서버로 보낼 통합 리스트
                _bodyDataArrayList.add(BodyData(thermometer, heart, glucose))
            }

        }

        private  fun broadcastUpdate(str:String) {
            val mHandler : Handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
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
            lineWidth = 2F //선 굵기
            circleRadius = 3F
            circleHoleRadius = 1F
            setDrawCircles(true)   //동그란거 없애기
            setDrawValues(true)
            setCircleColor(ContextCompat.getColor(applicationContext, R.color.toss_black_500))
            valueTextSize = 0F
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
            lineWidth = 2F //선 굵기
            circleRadius = 3F
            circleHoleRadius = 1F
            setDrawCircles(true)   //동그란거 없애기
            setDrawValues(true)
            setCircleColor(ContextCompat.getColor(applicationContext, R.color.text_red_200))
            valueTextSize = 0F
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
            lineWidth = 2F //선 굵기
            circleRadius = 2.1F
            circleHoleRadius = 0.1F
            setDrawCircles(true)   //동그란거 없애기
            setDrawValues(true)
            setCircleColor(ContextCompat.getColor(applicationContext, R.color.text_blue_200))
            valueTextSize = 0F
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
        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)
        lineThermometerDay.run {
            data = thermometerLineData
            setScaleEnabled(false) //핀치 줌 안되도록
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
            isScaleXEnabled = false //가로 확대 없애기

            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            marker = markerView
//            setVisibleXRangeMaximum(6f)
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
            axisRight.run { //오른쪽 y축축
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
            data = heartLineData
            setScaleEnabled(false) //핀치 줌 안되도록
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
            isScaleXEnabled = false //가로 확대 없애기
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))

//            setVisibleXRangeMaximum(6f)
            marker = markerView
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
                axisMinimum = 40F   //최소값
                axisMaximum = 130F   //최대값
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
            data = glucoseLineData
            setScaleEnabled(false) //핀치 줌 안되도록
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
            isScaleXEnabled = false //가로 확대 없애기

//            setVisibleXRangeMaximum(6f)
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            marker = markerView
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
                            val nickname = jsonObjectUser.getString("nickname")
                            binding.homeTextViewNickname.text = nickname
                            binding.homeRlv.titleText = nickname[0].toString()
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