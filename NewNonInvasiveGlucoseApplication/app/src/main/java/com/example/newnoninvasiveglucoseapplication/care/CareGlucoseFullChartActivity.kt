package com.example.newnoninvasiveglucoseapplication.care

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.care.CareProtectingMenuActivity.Companion._protectingPhoneNumber
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityCareGlucoseFullChartBinding
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.*
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.prolificinteractive.materialcalendarview.CalendarDay
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class CareGlucoseFullChartActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityCareGlucoseFullChartBinding.inflate(layoutInflater) }

    private val customProgressDialog by lazy { CustomDialogManager(applicationContext, R.layout.common_progress_dialog, null, null) }
    private val customCalendarDialog by lazy { CustomDialogManager(applicationContext, R.layout.common_calendar_dialog, null, _protectingPhoneNumber) }

    private lateinit var glucoseScatterData : ScatterData

    //현재 선택된 날짜 가지고 있기
    private val now by lazy { GregorianCalendar.getInstance() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {

        setGlucoseScatterChart()

        //이미지 넣기
        setImageViewWithGlide()
        Glide.with(this).load(R.drawable.ic_baseline_calendar_month_24).into(binding.careGlucoseFullChartImageViewCalendar)
        Glide.with(this).load(R.drawable.ic_baseline_arrow_back_24).into(binding.careGlucoseFullChartImageViewBack)

        //오늘 날짜 설정
        setTodayDate()

        //날짜에 해당하는 데이터 가져오기
        retrofitGetBodyDataAsDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))

        //스와이프 리스너 설정
        setLayoutSwipeListener()

        binding.careGlucoseFullChartImageViewCalendar.setOnClickListener(this)
        binding.careGlucoseFullChartBtnBack.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when(v) {
            binding.careGlucoseFullChartBtnBack -> {
                finish()
            }
            binding.careGlucoseFullChartImageViewCalendar -> {
                showCommonCalendarDialog()
            }
        }
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(applicationContext)
        glide.load(R.drawable.ic_baseline_calendar_month_24).into(binding.careGlucoseFullChartImageViewCalendar)
        glide.load(R.drawable.ic_baseline_arrow_back_24).into(binding.careGlucoseFullChartImageViewBack)
    }

    //초기 오늘 날짜 표시 되도록 설정
    @SuppressLint("SetTextI18n")
    private fun setTodayDate() {
        val now = now
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val dayOfMonth = now.get(Calendar.DAY_OF_MONTH)
        binding.careGlucoseFullChartTextViewDate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
    }

    //스와이프 리스너 설정
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun setLayoutSwipeListener() {
        binding.careGlucoseFullChartLottieSwipe.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {
            override fun onSwipeLeft() {
                Log.d("로그", "HomeThermometerFullChartActivity - onChartFling : 다음날짜 호출")
                now.add(Calendar.DAY_OF_MONTH, 1)
                binding.careGlucoseFullChartTextViewTime.text = "평균 체온"
                binding.careGlucoseFullChartTextViewDate.text = "${now.get(Calendar.YEAR)}년 ${now.get(Calendar.MONTH) + 1}월 ${now.get(Calendar.DAY_OF_MONTH)}일"
                retrofitGetBodyDataAsDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }

            override fun onSwipeRight() {
                Log.d("로그", "HomeThermometerFullChartActivity - onChartFling : 이전날짜 호출")
                now.add(Calendar.DAY_OF_MONTH, -1)
                binding.careGlucoseFullChartTextViewTime.text = "평균 체온"
                binding.careGlucoseFullChartTextViewDate.text = "${now.get(Calendar.YEAR)}년 ${now.get(Calendar.MONTH) + 1}월 ${now.get(Calendar.DAY_OF_MONTH)}일"
                retrofitGetBodyDataAsDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            }
        })
    }

    //MaterialCalendarView 다이어로그 생성
    private fun showCommonCalendarDialog() {
        customCalendarDialog.setTwoButtonWithOneCalendarDataDialogListener(object : CustomDialogManager.TwoButtonWithOneCalendarDataDialogListener {
            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            //날짜 설정 후 데이터 가져오기
            override fun onPositiveClicked(calendarDay: CalendarDay) {
                customCalendarDialog.dismiss()

                Log.d("로그", "HomeThermometerFullChartActivity - onPositiveClicked : ${calendarDay.year} ${calendarDay.month} ${calendarDay.day}")
                val year = calendarDay.year
                val month = calendarDay.month - 1
                val dayOfMonth = calendarDay.day
                now.set(year, month, dayOfMonth)
                binding.careGlucoseFullChartTextViewDate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"

                retrofitGetBodyDataAsDate(year, month, dayOfMonth)
            }

            override fun onNegativeClicked() {
                customCalendarDialog.dismiss()
            }
        })
        //retrofit통신으로 데이터가 있는 날짜 가져오기
        customCalendarDialog.show(supportFragmentManager, "common_calendar_dialog")
    }


    //심박수 라인 데이터 생성성
    private fun makeGlucoseSet(values : ArrayList<Entry>) : ScatterDataSet {
        val glucoseLineDataSet = ScatterDataSet(values, "혈당")
        return glucoseLineDataSet.apply {
//            mode = LineDataSet.Mode.LINEAR
//            cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//            setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
            color = ContextCompat.getColor(applicationContext, R.color.text_blue_200)
            valueFormatter = CustomChartManager.CustomDecimalYAxisFormatter() //데이터 소수점 표시
            setScatterShape(ScatterChart.ScatterShape.CIRCLE)
//            lineWidth = 2F //선 굵기
//            circleRadius = 3F
//            circleHoleRadius = 1F
//            setDrawCircles(true)   //동그란거 없애기
            setDrawValues(false)
//            setCircleColor(ContextCompat.getColor(applicationContext, R.color.teal_700))
            valueTextSize = 0F
            isHighlightEnabled = true   //클릭시 마크 보이게
            setDrawHorizontalHighlightIndicator(true)  //가로 하이라이트 줄 없애기
            setDrawVerticalHighlightIndicator(true) //세로 하이라이트 줄 없애기
            highLightColor =
                ContextCompat.getColor(applicationContext, R.color.circle_red_100) //클릭시 보이는 선 색깔
//            setDrawCircleHole(true)
            scatterShapeSize = 11f
        }
    }

    //체온 차트 설정
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setGlucoseScatterChart() {
        val homeGlucoseFullChartScatterChart = binding.careGlucoseFullChartScatterChart
        //마커 뷰 설정
//        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)
        homeGlucoseFullChartScatterChart.run {
            setScaleEnabled(true) //핀치 줌 안되도록
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
//            isScaleXEnabled = false //가로 확대 없애기
//            enableScroll()
            setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.android_blue_100))
//            marker = markerView
            animateX(1000)
            //스와이프 제스처 이벤트 설정
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                //그래프 터치시 값 변경 리스너
                @SuppressLint("SetTextI18n")
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    Log.d("로그", "HomeThermometerFullChartActivity - onValueSelected : ${e!!.x}  ${e.y}")
//                    binding.homeThermometerFullChartTextViewTime
                    val hour = (e.x / 3600).toInt() % 12
                    var aa = "오전"
                    if(e.x / 3600 > 12)
                        aa = "오후"

                    val minute = String.format("%02d", (e.x % 3600 / 60).toInt())
                    binding.careGlucoseFullChartTextViewValue.text = e.y.toString()
                    binding.careGlucoseFullChartTextViewTime.text = "$aa ${hour}시 ${minute}분"
                }
                override fun onNothingSelected() {
                }
            })

            notifyDataSetChanged()  //차트 값 변동을 감지함
//            moveViewToX((glucoseLineData.entryCount).toFloat())
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                axisMinimum = 0f
                axisMaximum = 85399f
                setDrawGridLines(true)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
//                labelCount = 6
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(applicationContext, R.color.toss_black_700)    //x그리드 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 50F   //최소값
                axisMaximum = 140F   //최대값
                isEnabled = true
                textSize = 15f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(applicationContext, R.color.toss_black_700)    //y그리드 색깔 변경


                //배경 색 추가하기
                val increment = 0.1F
                var metricLine = 70F
                for (i in 0..300) {
                    val llRange = LimitLine(metricLine, "")
                    llRange.lineColor = ContextCompat.getColor(applicationContext, R.color.iphone_green_heart_700)
                    llRange.lineWidth = 1f
                    this.addLimitLine(llRange)
                    metricLine += increment
                }
            }
            axisRight.run { //오른쪽 y축
                setDrawAxisLine(true)  //좌측 선 없애기
                axisMinimum = 50F   //최소값
                axisMaximum = 140F   //최대값
                isEnabled = true
                textSize = 15f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(applicationContext, R.color.toss_black_700)    //y그리드 색깔 변경
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
        Log.d("로그", "HomeGlucoseFullChartActivity - retrofitGetBodyDataAsDate : date : $year-$month-$day")
        RetrofitManager.instance.getBodyDataAsDate(_protectingPhoneNumber!!, year, month + 1, day, completion = {
                completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
                            //스와이프 애니메이션 표시
                            binding.careGlucoseFullChartLottieSwipe.playAnimation()

                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            val list = ArrayList<Entry>()
                            var average = 0F
                            for(i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val createdDate = jsonObject.getString("createdDate")
                                //시간만 second 변경
                                val index = LocalDateTime.parse(createdDate, DateTimeFormatter.ISO_DATE_TIME).toLocalTime().toSecondOfDay().toFloat()
                                val glucose = jsonObject.getDouble("glucose").toFloat()
                                Log.d("로그", "HomeGlucoseFullChartActivity - retrofitGetBodyDataAsDate : ${index} - $glucose")
                                list.add(Entry(index, glucose))
                                //평균값 계산을 위해 더하기
                                average += glucose
                            }
                            //데이터 한번 X 오름차순으로 정렬
                            list.sortBy { it.x }

                            //리스트 가져와서 차트 새로 그리기
                            glucoseScatterData = ScatterData(makeGlucoseSet(list))

                            //데이터가 없으면 종료
                            if(list.size == 0) {
                                binding.careGlucoseFullChartScatterChart.visibility = View.GONE
                                binding.careGlucoseFullChartLottie.visibility = View.VISIBLE
                                binding.careGlucoseFullChartLottie.playAnimation()
                                binding.careGlucoseFullChartTextViewValue.text = "데이터 없음"
                                binding.careGlucoseFullChartTextViewUnit.visibility = View.GONE
                                return@getBodyDataAsDate
                            }

                            //평균값 표시
                            average /= list.size
                            
                            //데이터가 있을 경우
                            binding.careGlucoseFullChartTextViewUnit.visibility = View.VISIBLE
                            binding.careGlucoseFullChartTextViewValue.visibility = View.VISIBLE
                            binding.careGlucoseFullChartTextViewValue.text = ((average * 10).roundToInt() / 10F).toString()
                            binding.careGlucoseFullChartScatterChart.visibility = View.VISIBLE
                            binding.careGlucoseFullChartLottie.visibility = View.GONE
                            binding.careGlucoseFullChartTextViewUnit.text = "mg/dL"

                            //차트 새로 찍기
                            binding.careGlucoseFullChartScatterChart.clear()
                            binding.careGlucoseFullChartScatterChart.data = glucoseScatterData
                            binding.careGlucoseFullChartScatterChart.xAxis.granularity = 1800f  //X축 간격
//                            binding.homeThermometerFullChartScatterChart.setVisibleXRangeMaximum(18000f)
//                            var centerPoint = binding.homeThermometerFullChartScatterChart.data.xMax - 9000
//                            if(centerPoint < 0) centerPoint = 0F
//
//                            binding.homeThermometerFullChartScatterChart.moveViewToX(centerPoint)  //출력 값이 중앙에 오도록 표시
                            binding.careGlucoseFullChartScatterChart.invalidate()
                            binding.careGlucoseFullChartScatterChart.fitScreen()
                            binding.careGlucoseFullChartScatterChart.animateX(2000)
                        }
                        else -> Toast.makeText(applicationContext, "데이터를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }

                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeGlucoseFullChartActivity - retrofitGetBodyDataAsDate : 통신 실패")
                }
            }
        })
    }

}