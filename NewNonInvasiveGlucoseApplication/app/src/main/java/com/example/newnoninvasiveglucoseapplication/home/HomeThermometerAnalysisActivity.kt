package com.example.newnoninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityHomeThermometerAnalysisBinding
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.CandleScatterDataSet
import com.example.newnoninvasiveglucoseapplication.util.CustomChartManager
import com.gigamole.navigationtabstrip.NavigationTabStrip
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.max
import kotlin.math.min

class HomeThermometerAnalysisActivity : AppCompatActivity(), View.OnClickListener {
    private val customChartManager by lazy { CustomChartManager.getInstance(applicationContext)}
    private val binding by lazy { ActivityHomeThermometerAnalysisBinding.inflate(layoutInflater) }
    private val daysArray = intArrayOf(7, 30, 90)
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
        setThermometerCombineChart()
        setThermometerAbnormalChart()
        setThermometerNormalChart()

        //일수 선택 리스너 설정
        setTabStripSelectedIndexListener()

        //평균, 최대, 최소 체온 데이터 가져오기
        retrofitGetAnalysisThermometerAverage(7)
        retrofitGetAnalysisThermometerNormal(7)
        retrofitGetAnalysisThermometerAbnormal(7)

        //초기 기간(7일)로 텍스트뷰 범위 세팅
        setPeriodTextView(7, 0)
        setPeriodTextView(7, 1)
        setPeriodTextView(7, 2)

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
    private fun makeThermometerCandleScatterData(candleScatterDataSet : CandleScatterDataSet) : CandleScatterDataSet {
        val candleDataSet = candleScatterDataSet.candleDataSet
        //범위 데이터
        candleDataSet.apply {
            //심지 부분
            shadowColor = ContextCompat.getColor(applicationContext, R.color.toss_black_150)
            shadowWidth = 4f
            //레전드 색깔
            color = ContextCompat.getColor(applicationContext, R.color.toss_black_150)

//            //음봉
//            decreasingColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//            decreasingPaintStyle = Paint.Style.FILL
//
//            //양봉
//            increasingColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//            increasingPaintStyle = Paint.Style.FILL

            neutralColor = ContextCompat.getColor(applicationContext, R.color.transparent)

            setDrawValues(false)

            //터치시 붉은 선 보이기
            isHighlightEnabled = false
        }
        //평균 데이터
        val scatterDataSet = candleScatterDataSet.scatterDataSet
        scatterDataSet.apply {
//            mode = LineDataSet.Mode.LINEAR
//            cubicIntensity = 0.2F //베지어 곡선 휘는 정도
            setDrawHorizontalHighlightIndicator(true)  //클릭 시 선 보이게 하기
            setDrawVerticalHighlightIndicator(true)  //클릭 시 선 보이게 하기
            color = ContextCompat.getColor(applicationContext, R.color.toss_black_500)
            valueFormatter = CustomChartManager.CustomDecimalYAxisFormatter() //데이터 소수점 표시
            setScatterShape(ScatterChart.ScatterShape.CIRCLE)
            setDrawValues(true)
            valueTextSize = 0F
            isHighlightEnabled = true   //클릭시 마크 보이게
            highLightColor = ContextCompat.getColor(applicationContext, R.color.circle_red_100)
//            setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
//            setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
//            setDrawCircleHole(true)
            scatterShapeSize = 16f
        }
        return candleScatterDataSet
    }

    //평균 체온 차트 설정
    private fun setThermometerCombineChart() {
        val homeThermometerAnalysisAverageChart = binding.homeThermometerAnalysisAverageChart

        //마커 뷰 설정
//        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)

        //클릭 리스너 설정
        homeThermometerAnalysisAverageChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            //그래프 터치시 값 변경 리스너
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val index = (e!!.x / 3).toInt()
                val candleData = binding.homeThermometerAnalysisAverageChart.data.candleData.dataSets[0].getEntryForIndex(index)
                Log.d("로그", "HomeThermometerAnalysisActivity - onValueSelected : $index   =-   $candleData")
                val minVal = min(candleData.high, candleData.low)
                val maxVal = max(candleData.high, candleData.low)

                binding.homeThermometerAnalysisTextViewMinValue.text = "$minVal"
                binding.homeThermometerAnalysisTextViewMaxValue.text = "$maxVal"
            }
            override fun onNothingSelected() {
            }
        })
        //레전드 각각 설정
        val legend1 = LegendEntry("평균", Legend.LegendForm.CIRCLE, 10f, 2f, null, ContextCompat.getColor(applicationContext, R.color.toss_black_700))
        val legend2 = LegendEntry("범위", Legend.LegendForm.SQUARE, 10f, 2f, null, ContextCompat.getColor(applicationContext, R.color.toss_black_150))

        homeThermometerAnalysisAverageChart.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = false    //드래그 가능
            isScaleXEnabled = false //가로 확대 없애기
//            enableScroll()
//            setVisibleXRangeMaximum(7f) //
//            setVisibleXRangeMinimum(7f)
//            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                axisMinimum = 0f
                axisMaximum = 24f
                labelCount = 8
                setDrawGridLines(true)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = CustomChartManager.CustomIntegerYAxisFormatter()
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(applicationContext, R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 25F   //최소값
                axisMaximum = 45F   //최대값
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
    private fun setThermometerAbnormalData(values : ArrayList<BarEntry>) : BarDataSet {
        val barDataSet = BarDataSet(values, "비정상 체온 빈도수")
        barDataSet.run {
            color = ContextCompat.getColor(applicationContext, R.color.text_red_200)
            valueFormatter = CustomChartManager.CustomIntegerYAxisFormatter() //데이터 소수점 표시
            valueTextColor = ContextCompat.getColor(applicationContext, R.color.toss_black_500)
            setDrawValues(true)
            valueTextSize = 12F
            isHighlightEnabled = true   //클릭시 마크 보이게
        }
        return barDataSet
    }

    //체온 차트 설정
    private fun setThermometerAbnormalChart() {
        val barThermometerDay = binding.homeThermometerAnalysisAbnormalChart
        barThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
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
                textSize = 10f
                setDrawGridLines(false)   //배경 그리드 추가
                valueFormatter = CustomChartManager.CustomIntegerYAxisFormatter()
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(applicationContext, R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 0F   //최소값
                axisMaximum = 300F   //최대값
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
    private fun setThermometerNormalData(values : ArrayList<BarEntry>) : BarDataSet {
        val barDataSet = BarDataSet(values, "정상 체온 빈도수")
        barDataSet.run {
            color = ContextCompat.getColor(applicationContext, R.color.text_blue_200)
            valueFormatter = CustomChartManager.CustomIntegerYAxisFormatter() //데이터 소수점 표시
            valueTextColor = ContextCompat.getColor(applicationContext, R.color.toss_black_500)
            setDrawValues(true)
            valueTextSize = 12F
            isHighlightEnabled = true   //클릭시 마크 보이게
        }
        return barDataSet
    }

    private fun setThermometerNormalChart() {
        val barThermometerDay = binding.homeThermometerAnalysisNormalChart

        barThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
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
                textSize = 10f
                valueFormatter = CustomChartManager.CustomBarChartXAxisFormatter()
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(applicationContext, R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 0F   //최소값
                axisMaximum = 300F   //최대값
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

    //기간 텍스트 뷰 셋팅
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setPeriodTextView(period : Long, flag : Int) {
        val now = LocalDateTime.now()
        val end = now.format(DateTimeFormatter.ofPattern("yyyy.M.d"))
        val start = now.minusDays(period - 1).format(DateTimeFormatter.ofPattern("yyyy.M.d"))
        Log.d("로그", "HomeThermometerAnalysisActivity - setPeriodTextView : @@@@@@@@@@@@@@@@@     $flag")
        when(flag) {
            0 -> binding.homeThermometerAnalysisTextViewPeriodAverage.text = "$start  ~  $end"
            1 -> binding.homeThermometerAnalysisTextViewPeriodNormal.text = "$start  ~  $end"
            2 -> binding.homeThermometerAnalysisTextViewPeriodAbnormal.text = "$start  ~  $end"

        }
    }
    
    //평균 체온 차트 일수 변경 리스너
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTabStripSelectedIndexListener() {
        binding.homeThermometerAnalysisNtsAverageChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {

            override fun onStartTabSelected(title: String?, index: Int) {
                //7, 30, 90 데이터 가져오기
                retrofitGetAnalysisThermometerAverage(daysArray[index])
                setPeriodTextView(daysArray[index].toLong(), 0)
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }
        binding.homeThermometerAnalysisNtsAbnormalChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {
            override fun onStartTabSelected(title: String?, index: Int) {
                retrofitGetAnalysisThermometerNormal(daysArray[index])
                binding.homeThermometerAnalysisTextViewPeriodNormalDays.text = daysArray[index].toString()

                setPeriodTextView(daysArray[index].toLong(), 1)
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }
        binding.homeThermometerAnalysisNtsNormalChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {
            override fun onStartTabSelected(title: String?, index: Int) {
                retrofitGetAnalysisThermometerAbnormal(daysArray[index])
                binding.homeThermometerAnalysisTextViewPeriodAbnormalDays.text = daysArray[index].toString()

                setPeriodTextView(daysArray[index].toLong(), 2)
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }


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
                            val listCandleData: ArrayList<CandleEntry> = ArrayList()
                            val listScatterData: MutableList<Entry> = ArrayList()
                            for(i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val avg = jsonObject.getDouble("avgData").toFloat()
                                val min = jsonObject.getDouble("minData").toFloat()
                                val max = jsonObject.getDouble("maxData").toFloat()
                                val timeSlot = jsonObject.getDouble("timeSlot").toFloat()
                                listCandleData.add(CandleEntry((timeSlot * 3 + 1.5).toFloat(), min,  max, avg, avg))
                                listScatterData.add(Entry((timeSlot * 3 + 1.5).toFloat(), avg))
                            }
                            //체온 컴바인 데이터 만들기
                            val candleScatterDataSet = makeThermometerCandleScatterData(CandleScatterDataSet(CandleDataSet(listCandleData, "범위"), ScatterDataSet(listScatterData, "평균")))
                            val combinedData = CombinedData()
                            combinedData.setData(CandleData(candleScatterDataSet.candleDataSet))
                            combinedData.setData(ScatterData(candleScatterDataSet.scatterDataSet))

                            Log.d(
                                "로그",
                                "HomeThermometerAnalysisActivity - retrofitGetAnalysisThermometerAverage : ${combinedData.candleData.dataSets}"
                            )
                            Log.d(
                                "로그",
                                "HomeThermometerAnalysisActivity - retrofitGetAnalysisThermometerAverage : ${combinedData.scatterData.dataSets}"
                            )
                            binding.homeThermometerAnalysisAverageChart.data = combinedData
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

    //정상 범위 체온 빈도수 가져오기
    private fun retrofitGetAnalysisThermometerNormal(day : Int) {
        RetrofitManager.instance.getAnalysisThermometerNormal(day, completion = {
                completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
//                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            val listBarData: ArrayList<BarEntry> = ArrayList()
                            //총 개수 카운트
                            var sum = 0
                            for(i in 0 until jsonArray.length()) {
                                val value = jsonArray.getInt(i)
                                sum += value
                                listBarData.add(BarEntry(i.toFloat(), value.toFloat()))
                            }
                            val barDataSet = BarData(setThermometerNormalData(listBarData))
                            binding.homeThermometerAnalysisNormalChart.data = barDataSet
                            binding.homeThermometerAnalysisNormalChart.invalidate()

                            binding.homeThermometerAnalysisTextViewPeriodNormalSum.text = sum.toString()
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeThermometerAnalysisActivity - retrofitGetAnalysisThermometerAverage : 통신 실패")
                }
            }
        })
    }

    //정상 범위 체온 빈도수 가져오기
    private fun retrofitGetAnalysisThermometerAbnormal(day : Int) {
        RetrofitManager.instance.getAnalysisThermometerAbnormal(day, completion = {
                completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
//                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            val listBarData: ArrayList<BarEntry> = ArrayList()
                            var sum = 0
                            for(i in 0 until jsonArray.length()) {
                                val value = jsonArray.getInt(i)
                                sum += value
                                listBarData.add(BarEntry(i.toFloat(), value.toFloat()))
                            }
                            val barDataSet = BarData(setThermometerAbnormalData(listBarData))
                            binding.homeThermometerAnalysisAbnormalChart.data = barDataSet
                            binding.homeThermometerAnalysisAbnormalChart.invalidate()

                            binding.homeThermometerAnalysisTextViewPeriodAbnormalSum.text = sum.toString()
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeThermometerAnalysisActivity - retrofitGetAnalysisThermometerAbnormal : 통신 실패")
                }
            }
        })
    }
}