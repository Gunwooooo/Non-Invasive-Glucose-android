package com.example.newnoninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityHomeThermometerAnalysisBinding
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.CustomChartManager
import com.example.newnoninvasiveglucoseapplication.util.CustomMarkerViewManager
import com.gigamole.navigationtabstrip.NavigationTabStrip
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import kotlin.math.max
import kotlin.math.min


class HomeThermometerAnalysisActivity : AppCompatActivity(), View.OnClickListener, OnChartValueSelectedListener {
    private val customChartManager by lazy { CustomChartManager.getInstance(applicationContext)}
    private val binding by lazy { ActivityHomeThermometerAnalysisBinding.inflate(layoutInflater) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        binding.homeThermometerAnalysisNtsAverageChart.setTabIndex(0, true)
        binding.homeThermometerAnalysisNtsAbnormalChart.setTabIndex(0, true)
        binding.homeThermometerAnalysisNtsNormalChart.setTabIndex(0, true)

        //초기 글라이드로 이미지 불러오기
        setImageViewWithGlide()

        //차트 보여주기 설정하기
        setThermometerAverageChart()
        setThermometerAbnormalChart()
        setThermometerNormalChart()

        //일수 선택 리스너 설정
        setTabStripSelectedIndexListener()

        //평균, 최대, 최소 체온 데이터 가져오기
        retrofitGetAnalysisThermometerAverage(7)

        //초기 기간(7일)로 텍스트뷰 범위 세팅
        setPeriodAverageTextView(7)
        setPeriodNormalTextView(7)
        setPeriodAbnormalTextView(7)

        binding.homeThermometerAnalysisBtnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeThermometerAnalysisBtnBack -> {
                finish()
            }
        }
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(applicationContext)
        glide.load(R.drawable.background_image_detail).into(binding.homeThermometerAnalysisImageViewDetailBackground)
        glide.load(R.drawable.ic_baseline_arrow_back_24).into(binding.homeThermometerAnalysisImageViewBack)
    }


    /////////////////////////////////////////////   평균 체온 캔들라인 차트   ///////////////////////////////////////
    //평균 체온 차트 설정
    private fun setThermometerAverageChart() {
        val homeThermometerAnalysisAverageChart = binding.homeThermometerAnalysisAverageChart

        //마커 뷰 설정
        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)

        //클릭 리스너 설정
        homeThermometerAnalysisAverageChart.setOnChartValueSelectedListener(this)

        //레전드 각각 설정
        val legend1 = LegendEntry("평균", Legend.LegendForm.DEFAULT, 10f, 2f, null, ContextCompat.getColor(applicationContext, R.color.toss_black_700))
        val legend2 = LegendEntry("범위", Legend.LegendForm.DEFAULT, 10f, 2f, null, ContextCompat.getColor(applicationContext, R.color.toss_black_150))

        homeThermometerAnalysisAverageChart.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = false    //드래그 가능
            isScaleXEnabled = false //가로 확대 없애기
//            enableScroll()
//            setVisibleXRangeMaximum(7f) //
//            setVisibleXRangeMinimum(7f)
            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                axisMinimum = 0f
                axisMaximum = 86400f
                setDrawGridLines(false)   //배경 그리드 추가
//                labelCount = 7
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(applicationContext, R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 20F   //최소값
                axisMaximum = 40F   //최대값
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor = ContextCompat.getColor(applicationContext, R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
                isEnabled = false  //오른쪽 y축 없애기
            }
            legend.run {
                setCustom(arrayOf(legend1, legend2))
                isEnabled = true //레전드 아이콘 표시
                form = Legend.LegendForm.CIRCLE
                textSize = 16f
                setExtraOffsets(5f, 5f, 5f, 15f)
//                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }

    /////////////////////////////////////////////   비정상 바 차트   ///////////////////////////////////////
    private fun setThermometerAbnormalData() : BarDataSet {
        val barDataSet = CustomChartManager(applicationContext).makeThermometerAnalysis2BarDataSet()
//        barDataSet.mode = LineDataSet.Mode.LINEAR
//        barDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        barDataSet.setColor(ContextCompat.getColor(applicationContext, R.color.text_red_200))
//        barDataSet.lineWidth = 3F //선 굵기
//        barDataSet.circleRadius = 7F
//        barDataSet.circleHoleRadius = 4F
//        barDataSet.setDrawCircles(true)   //동그란거 없애기
        barDataSet.valueTextColor = ContextCompat.getColor(applicationContext, R.color.toss_black_500)
        barDataSet.setDrawValues(true)
//        barDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_thermometer_100))
        barDataSet.valueTextSize = 12F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        barDataSet.isHighlightEnabled = true   //클릭시 마크 보이게
//        barDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
//        barDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
//        barDataSet.setDrawCircleHole(true)
        return barDataSet
    }

    //체온 차트 설정
    private fun setThermometerAbnormalChart() {
        val thermometerBarData =  setThermometerAbnormalData()
        val barData = BarData(thermometerBarData)
        val barThermometerDay = binding.homeThermometerAnalysisAbnormalChart

        barThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = barData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isScaleXEnabled = false //가로 확대 없애기
//            isDragEnabled = true    //드래그 가능
//            enableScroll()
//            setVisibleXRangeMaximum(7f)
//            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(applicationContext, R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 0F   //최소값
                axisMaximum = 20F   //최대값
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor = ContextCompat.getColor(applicationContext, R.color.toss_black_150)    //y그리드 색깔 변경
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

    /////////////////////////////////////////////   정상 바 차트   ///////////////////////////////////////
    private fun setThermometerNormalChart() {
        val thermometerBarData =  customChartManager.setThermometerNormalData()
        val barData = BarData(thermometerBarData)
        val barThermometerDay = binding.homeThermometerAnalysisNormalChart
//        val combinedData = LineData()
//        lineThermometerDay.setData(lineData)

        barThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = barData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isScaleXEnabled = false //가로 확대 없애기
//            isDragEnabled = true    //드래그 가능
//            enableScroll()
//            setVisibleXRangeMaximum(7f)
//            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(applicationContext, R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 0F   //최소값
                axisMaximum = 20F   //최대값
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor = ContextCompat.getColor(applicationContext, R.color.toss_black_150)    //y그리드 색깔 변경
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

    //평균 기간 텍스트 뷰 셋팅
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setPeriodAverageTextView(period : Long) {
        val now = LocalDateTime.now()
        val end = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        val start = now.minusDays(period - 1).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        Log.d("로그", "HomeThermometerAnalysisActivity - setPeriodAverageTextView : $start   $end")
        binding.homeThermometerAnalysisTextViewPeriodAverage.text = "$start ~ $end"
    }
    //정상 바차트 기간 텍스트 뷰 셋팅
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setPeriodNormalTextView(period : Long) {
        val now = LocalDateTime.now()
        val end = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        val start = now.minusDays(period - 1).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        Log.d("로그", "HomeThermometerAnalysisActivity - setPeriodAverageTextView : $start   $end")
        binding.homeThermometerAnalysisTextViewPeriodNormal.text = "$start ~ $end"
    }
    //비정상 바차트 기간 텍스트 뷰 셋팅
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setPeriodAbnormalTextView(period : Long) {
        val now = LocalDateTime.now()
        val end = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        val start = now.minusDays(period - 1).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
        Log.d("로그", "HomeThermometerAnalysisActivity - setPeriodAverageTextView : $start   $end")
        binding.homeThermometerAnalysisTextViewPeriodAbnormal.text = "$start ~ $end"
    }

    
    //평균 체온 차트 일수 변경 리스너
    private fun setTabStripSelectedIndexListener() {
        binding.homeThermometerAnalysisNtsAverageChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {
            override fun onStartTabSelected(title: String?, index: Int) {
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }
        binding.homeThermometerAnalysisNtsAbnormalChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {
            override fun onStartTabSelected(title: String?, index: Int) {
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }
        binding.homeThermometerAnalysisNtsNormalChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {
            override fun onStartTabSelected(title: String?, index: Int) {
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }
    }
    

    //그래프 터치시 값 변경 리스너
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e is CandleEntry) {
            val minVal = min(e.low, e.high)
            val maxVal = max(e.low, e.high)

            binding.homeThermometerAnalysisTextViewMinValue.text = "$minVal"
            binding.homeThermometerAnalysisTextViewMaxValue.text = "$maxVal"

//            binding.homeAnalysisThermometerTextViewMinValue.setTextColor(ContextCompat.getColor(R.color.))
        }
    }

    override fun onNothingSelected() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //평균, 최대, 최소 일수별 체온 가져오기
    private fun retrofitGetAnalysisThermometerAverage(day : Int) {
        RetrofitManager.instance.getAnalysisThermometerAverage(day, completion = {
            completionResponse, response -> 
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            Log.d("로그", "HomeFullChartActivity - retrofitGetBodyDataAsDate : 제이슨어레이 갯수 :  ${jsonArray.length()}")
                            val listAvg = ArrayList<Entry>()
                            val listMin = ArrayList<Entry>()
                            val listMax = ArrayList<Entry>()
                            for(i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val avg = jsonObject.getDouble("avgThermometer").toFloat()
                                val min = jsonObject.getDouble("minThermometer").toFloat()
                                val max = jsonObject.getDouble("maxThermometer").toFloat()
                                val index = (i + 1) * 3 * 3600 - 5400
                                Log.d("로그", "HomeFullChartActivity - retrofitGetBodyDataAsDate : ${index} - avg : $avg  min : $min  max : $max")
                                listAvg.add(Entry(index.toFloat(), avg))
                                listMin.add(Entry(index.toFloat(), min))
                                listMax.add(Entry(index.toFloat(), max))
                            }

                            //체온 라인 데이터 만들기
                            val dataSets = ArrayList<ILineDataSet>()
                            dataSets.add(makeThermometerAverageSet(listAvg))
                            dataSets.add(makeThermometerMaxSet(listMax))
                            dataSets.add(makeThermometerMinSet(listMin))

                            binding.homeThermometerAnalysisAverageChart.data = LineData(dataSets)
                            binding.homeThermometerAnalysisAverageChart.invalidate()
                        }
                    }

                }
                CompletionResponse.FAIL -> {
                    Log.d(
                        "로그",
                        "HomeThermometerAnalysisActivity - retrofitGetAnalysisThermometerAverage : 통신 실패"
                    )
                }
            }
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //심박수 라인 데이터 생성성
    private fun makeThermometerAverageSet(values : ArrayList<Entry>) : LineDataSet {
        val thermometerLineDataSet = LineDataSet(values, "체온")
        return thermometerLineDataSet.apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2F //베지어 곡선 휘는 정도
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

    private fun makeThermometerMinSet(values : ArrayList<Entry>) : LineDataSet {
        val thermometerLineDataSet = LineDataSet(values, "체온")
        return thermometerLineDataSet.apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2F //베지어 곡선 휘는 정도
            setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
            fillColor = ContextCompat.getColor(applicationContext, R.color.android_blue_100)
            valueFormatter = CustomChartManager.CustomDecimalYAxisFormatter() //데이터 소수점 표시
            lineWidth = 0F //선 굵기
            color = ContextCompat.getColor(applicationContext, R.color.android_blue_100)
            setDrawCircles(false)   //동그란거 없애기
            setDrawValues(false)
            setDrawFilled(true)
            valueTextSize = 0F
            isHighlightEnabled = true   //클릭시 마크 보이게
            setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
            setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
            setDrawCircleHole(false)
        }
    }

    private fun makeThermometerMaxSet(values : ArrayList<Entry>) : LineDataSet {
        val thermometerLineDataSet = LineDataSet(values, "체온")
        return thermometerLineDataSet.apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2F //베지어 곡선 휘는 정도
            setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
            fillColor = ContextCompat.getColor(applicationContext, R.color.black)
            valueFormatter = CustomChartManager.CustomDecimalYAxisFormatter() //데이터 소수점 표시
            lineWidth = 0F //선 굵기
            setDrawCircles(false)   //동그란거 없애기
            setDrawValues(false)
            valueTextSize = 0F
            color = ContextCompat.getColor(applicationContext, R.color.black)
            setDrawFilled(true)
            isHighlightEnabled = true   //클릭시 마크 보이게
            setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
            setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
            setDrawCircleHole(false)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}