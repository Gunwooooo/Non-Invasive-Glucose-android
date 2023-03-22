package com.hanait.noninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.google.android.material.datepicker.MaterialDatePicker
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeThermometerFullChartBinding
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.*
import org.json.JSONArray
import java.lang.Math.round
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class HomeThermometerFullChartActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeThermometerFullChartBinding.inflate(layoutInflater) }

    private val customProgressDialog by lazy { CustomDialogManager(R.layout.common_progress_dialog, null) }

    private lateinit var thermometerScatterData : ScatterData

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        setThermometerScatterChart()

        //캘린더 이미지 넣기
        Glide.with(this).load(R.drawable.ic_baseline_calendar_month_24).into(binding.homeFullChartImageViewCalendar)

        //오늘 날짜 설정
        setTodayDate()

        //현재 시간
        val now = LocalDateTime.now()
        //날짜에 해당하는 데이터 가져오기
        retrofitGetBodyDataAsDate(now.year, now.monthValue, now.dayOfMonth)

        binding.homeFullChartImageViewCalendar.setOnClickListener(this)
        binding.homeFullChartBtnBack.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when(v) {
            binding.homeFullChartBtnBack -> {
                finish()
            }
            binding.homeFullChartImageViewCalendar -> {
                CustomDatePickerDialogManager(this).makeDatePickerDialog(setDatePickerDialogListener()).show()
            }
        }
    }

    //초기 오늘 날짜 표시 되도록 설정
    @SuppressLint("SetTextI18n")
    private fun setTodayDate() {
        val gregorianCalendar = GregorianCalendar()
        val year = gregorianCalendar.get(Calendar.YEAR)
        val month = gregorianCalendar.get(Calendar.MONTH)
        val dayOfMonth = gregorianCalendar.get(Calendar.DAY_OF_MONTH)
        binding.homeFullChartTextViewDate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
    }

    //데이터피커 리스너 설정
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun setDatePickerDialogListener() : DatePickerDialog.OnDateSetListener {
        val datePickerDialogListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            binding.homeFullChartTextViewDate.text = "${year}년 ${month+1}월 ${dayOfMonth}일"
            retrofitGetBodyDataAsDate(year, month+1, dayOfMonth)
        }
        return datePickerDialogListener
    }

    //심박수 라인 데이터 생성성
    private fun makeThermometerSet(values : ArrayList<Entry>) : ScatterDataSet {
        val thermometerLineDataSet = ScatterDataSet(values, "체온")
        return thermometerLineDataSet.apply {
//            mode = LineDataSet.Mode.LINEAR
//            cubicIntensity = 0.2F //베지어 곡선 휘는 정도
            setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
            color = ContextCompat.getColor(applicationContext, R.color.toss_black_500)
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
            scatterShapeSize = 15f
        }
    }

    //체온 차트 설정
    private fun setThermometerScatterChart() {
        val lineThermometerDay = binding.homeFullChartScatterChart
        //마커 뷰 설정
        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)
        lineThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = false
            isScaleXEnabled = false //가로 확대 없애기
//            enableScroll()
            setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.android_blue_100))
//            marker = markerView

            notifyDataSetChanged()  //차트 값 변동을 감지함
//            moveViewToX((thermometerLineData.entryCount).toFloat())
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                axisMinimum = 0f
                axisMaximum = 86400f
                spaceMin = 10800f
                spaceMax = 10800f
                setDrawGridLines(true)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
//                labelCount = 5
//                granularity = f  //X축 간격
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 10F   //최소값
                axisMaximum = 40F   //최대값
                isEnabled = true
                animateX(500)
                animateY(1000)
                textSize = 15f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(applicationContext, R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
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
        RetrofitManager.instance.getBodyDataAsDate(year, month, day, completion = {
                completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            Log.d("로그", "HomeFullChartActivity - retrofitGetBodyDataAsDate : 제이슨어레이 갯수 :  ${jsonArray.length()}")
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
                            //리스트 가져와서 차트 새로 그리기
                            thermometerScatterData = ScatterData(makeThermometerSet(list))
                            binding.homeFullChartScatterChart.data = thermometerScatterData
//                            binding.homeFullChartScatterChart.setVisibleXRangeMaximum(28800f)
                            binding.homeFullChartScatterChart.invalidate()
                            //데이터가 없으면 종료
                            if(list.size == 0) {
                                binding.homeThermometerFullChartTextViewAverage.text = " - "
                                return@getBodyDataAsDate
                            }
                            //평균값 표시
                            average /= list.size
                            binding.homeThermometerFullChartTextViewAverage.text = ((average * 10).roundToInt() / 10F).toString()
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