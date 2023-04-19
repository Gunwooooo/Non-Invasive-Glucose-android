package com.example.newnoninvasiveglucoseapplication.care

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.care.CareProtectingMenuActivity.Companion._protectingPhoneNumber
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityCareThermometerFullChartBinding
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityHomeThermometerFullChartBinding
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.*
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
class CareThermometerFullChartActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityCareThermometerFullChartBinding.inflate(layoutInflater) }

    private val customProgressDialog by lazy { CustomDialogManager(applicationContext, R.layout.common_progress_dialog, null) }

    //현재 선택된 날짜 가지고 있기
    private val now by lazy { Calendar.getInstance() }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()

        Log.d("로그", "CareThermometerFullChartActivity - onCreate : 보호대상자 폰번호 $_protectingPhoneNumber")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        setThermometerScatterChart()

        //캘린더 이미지 넣기
        Glide.with(this).load(R.drawable.ic_baseline_calendar_month_24).into(binding.careThermometerFullChartImageViewCalendar)

        //오늘 날짜 설정
        setTodayDate()
        
        //날짜에 해당하는 데이터 가져오기
        retrofitGetBodyDataAsDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))

        //스와이프 리스너 설정
        setLayoutSwipeListener()
        
        binding.careThermometerFullChartImageViewCalendar.setOnClickListener(this)
        binding.careThermometerFullChartBtnBack.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when(v) {
            binding.careThermometerFullChartBtnBack -> {
                finish()
            }
            binding.careThermometerFullChartImageViewCalendar -> {
                CustomDatePickerDialogManager(this).makeDatePickerDialog(setDatePickerDialogListener()).show()
            }
        }
    }

    //초기 오늘 날짜 표시 되도록 설정
    @SuppressLint("SetTextI18n")
    private fun setTodayDate() {
        val now = now
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val dayOfMonth = now.get(Calendar.DAY_OF_MONTH)
        binding.careThermometerFullChartTextViewDate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
    }

    //데이터피커 리스너 설정
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun setDatePickerDialogListener() : DatePickerDialog.OnDateSetListener {
        val datePickerDialogListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            binding.careThermometerFullChartTextViewDate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            retrofitGetBodyDataAsDate(year, month, dayOfMonth)
        }
        return datePickerDialogListener
    }
    
    //스와이프 리스너 설정
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun setLayoutSwipeListener() {
        binding.careThermometerFullChartLayout.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {
            override fun onSwipeLeft() {
                Log.d("로그", "HomeThermometerFullChartActivity - onChartFling : 다음날짜 호출")
                now.add(Calendar.DAY_OF_MONTH, 1)
                binding.careThermometerFullChartTextViewDate.text = "${now.get(Calendar.YEAR)}년 ${now.get(Calendar.MONTH) + 1}월 ${now.get(Calendar.DAY_OF_MONTH)}일"
                retrofitGetBodyDataAsDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }

            override fun onSwipeRight() {
                Log.d("로그", "HomeThermometerFullChartActivity - onChartFling : 이전날짜 호출")
                now.add(Calendar.DAY_OF_MONTH, -1)
                binding.careThermometerFullChartTextViewDate.text = "${now.get(Calendar.YEAR)}년 ${now.get(Calendar.MONTH) + 1}월 ${now.get(Calendar.DAY_OF_MONTH)}일"
                retrofitGetBodyDataAsDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }
        })
    }

    //심박수 라인 데이터 생성성
    private fun makeThermometerSet(values : ArrayList<Entry>) : ScatterDataSet {
        val thermometerLineDataSet = ScatterDataSet(values, "체온")
        return thermometerLineDataSet.apply {
//            mode = LineDataSet.Mode.LINEAR
//            cubicIntensity = 0.2F //베지어 곡선 휘는 정도
            setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
            color = ContextCompat.getColor(applicationContext, R.color.teal_700)
            valueFormatter = CustomChartManager.CustomDecimalYAxisFormatter() //데이터 소수점 표시
            setScatterShape(ScatterChart.ScatterShape.CIRCLE)
//            lineWidth = 2F //선 굵기
//            circleRadius = 3F
//            circleHoleRadius = 1F
//            setDrawCircles(true)   //동그란거 없애기
            setDrawValues(true)
//            setCircleColor(ContextCompat.getColor(applicationContext, R.color.toss_black_500))
            valueTextSize = 0F
            isHighlightEnabled = true   //클릭시 마크 보이게
            setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
            setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
//            setDrawCircleHole(true)
            scatterShapeSize = 11f
        }
    }

    //체온 차트 설정
    private fun setThermometerScatterChart() {
        val homeFullChartScatterChart = binding.careThermometerFullChartScatterChart
        //마커 뷰 설정
        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)
        homeFullChartScatterChart.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = false
//            isScaleXEnabled = false //가로 확대 없애기
//            enableScroll()
            setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.android_blue_100))
//            marker = markerView

            //스와이프 제스처 이벤트 설정
            onChartGestureListener = object : OnChartGestureListener {
                override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {}
                override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {}
                override fun onChartLongPressed(me: MotionEvent?) {}
                override fun onChartDoubleTapped(me: MotionEvent?) {}
                override fun onChartSingleTapped(me: MotionEvent?) {}
                //스와이프 이벤트 설정
                @SuppressLint("SetTextI18n")
                override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {
                    val x1 = me1!!.x
                    val x2 = me2!!.x
                    Log.d("로그", "HomeThermometerFullChartActivity - onChartFling : $velocityX     $velocityY")
                    //오른쪽으로 스와이프 -> 이전 날짜 호출
                    if(x1 < x2) {
                        Log.d("로그", "HomeThermometerFullChartActivity - onChartFling : 이전날짜 호출")
                        now.add(Calendar.DAY_OF_MONTH, -1)
                        binding.careThermometerFullChartTextViewDate.text = "${now.get(Calendar.YEAR)}년 ${now.get(Calendar.MONTH) + 1}월 ${now.get(Calendar.DAY_OF_MONTH)}일"
                        retrofitGetBodyDataAsDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                    } else if(x1 > x2) {
                        Log.d("로그", "HomeThermometerFullChartActivity - onChartFling : 다음날짜 호출")
                        now.add(Calendar.DAY_OF_MONTH, 1)
                        binding.careThermometerFullChartTextViewDate.text = "${now.get(Calendar.YEAR)}년 ${now.get(Calendar.MONTH) + 1}월 ${now.get(Calendar.DAY_OF_MONTH)}일"
                        retrofitGetBodyDataAsDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                    }
                }
                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
            }
            animateX(1000)
            notifyDataSetChanged()  //차트 값 변동을 감지함
//            moveViewToX((thermometerLineData.entryCount).toFloat())
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                axisMinimum = 0f
                axisMaximum = 85399f
//                spaceMin = 10800f
//                spaceMax = 10800f
                setDrawGridLines(true)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
//                labelCount = 12
                granularity = 10800f  //X축 간격
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
//                setDrawLabels(true)
//                setDrawAxisLine(false)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 20F   //최소값
                axisMaximum = 40F   //최대값
                isEnabled = true
                textSize = 15f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(applicationContext, R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
                setDrawAxisLine(true)  //좌측 선 없애기
                axisMinimum = 20F   //최소값
                axisMaximum = 40F   //최대값
                isEnabled = true
                textSize = 15f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(applicationContext, R.color.toss_black_150)    //y그리드 색깔 변경
                isEnabled = true  //오른쪽 y축 없애기
            }
            legend.run {
                isEnabled = true //레전드 아이콘 표시
                form = Legend.LegendForm.CIRCLE
                textSize = 16f
                setExtraOffsets(15f, 15f, 15f, 15f)
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //해당 날짜의 건강 데이터 조회
    @RequiresApi(Build.VERSION_CODES.O)
    private fun retrofitGetBodyDataAsDate(year: Int, month: Int, day: Int) {
        customProgressDialog.show(supportFragmentManager, "common_progress_dialog")
        Log.d("로그", "HomeFullChartActivity - retrofitGetBodyDataAsDate : date : $year-$month-$day")
        RetrofitManager.instance.getBodyDataAsDate(_protectingPhoneNumber!!, year, month + 1, day, completion = {
                completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
                            //스와이프 애니메이션 표시
                            binding.careThermometerFullChartLottieSwipe.playAnimation()

                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            val list = ArrayList<Entry>()
                            var average = 0F
                            for(i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val createDate = jsonObject.getString("createdDate")
                                //시간만 second로 변경
                                val index = LocalDateTime.parse(createDate, DateTimeFormatter.ISO_DATE_TIME).toLocalTime().toSecondOfDay().toFloat()
                                val thermometer = jsonObject.getDouble("thermometer").toFloat()
                                Log.d("로그", "HomeFullChartActivity - retrofitGetBodyDataAsDate : ${index} - $thermometer")
                                list.add(Entry(index, thermometer))

                                //평균값 계산을 위해 더하기
                                average += thermometer
                            }
                            //데이터 한번 X 오름차순으로 정렬
                            list.sortBy { it.x }

                            //리스트 가져와서 차트 새로 그리기
                            val thermometerScatterData = ScatterData(makeThermometerSet(list))
    
                            //데이터가 없으면 종료
                            if(list.size == 0) {
                                binding.careThermometerFullChartScatterChart.visibility = View.GONE
                                binding.careThermometerFullChartLottie.visibility = View.VISIBLE
                                binding.careThermometerFullChartLottie.playAnimation()
                                binding.careThermometerFullChartTextViewAverage.text = "데이터 없음"
                                binding.careThermometerFullChartTextViewUnit.visibility = View.GONE
                                return@getBodyDataAsDate
                            }
                            //평균값 표시
                            average /= list.size

                            //데이터가 있을 경우
                            binding.careThermometerFullChartTextViewUnit.visibility = View.VISIBLE
                            binding.careThermometerFullChartTextViewAverage.visibility = View.VISIBLE
                            binding.careThermometerFullChartTextViewAverage.text = ((average * 10).roundToInt() / 10F).toString()
                            binding.careThermometerFullChartScatterChart.visibility = View.VISIBLE
                            binding.careThermometerFullChartLottie.visibility = View.GONE
                            binding.careThermometerFullChartTextViewUnit.text = "℃"

                            binding.careThermometerFullChartScatterChart.data = thermometerScatterData
                            binding.careThermometerFullChartScatterChart.invalidate()
                            binding.careThermometerFullChartScatterChart.animateX(2000)
                        }
                        else -> Toast.makeText(applicationContext, "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeFullChartActivity - retrofitGetBodyDataAsDate : 통신 실패")
                }
            }
        })
    }

}