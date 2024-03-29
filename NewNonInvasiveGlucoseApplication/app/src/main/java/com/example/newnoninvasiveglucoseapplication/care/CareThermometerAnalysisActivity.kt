package com.example.newnoninvasiveglucoseapplication.care

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
import com.example.newnoninvasiveglucoseapplication.care.CareProtectingMenuActivity.Companion._protectingPhoneNumber
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityCareThermometerAnalysisBinding
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.CandleScatterDataSet
import com.example.newnoninvasiveglucoseapplication.util.CustomChartManager
import com.gigamole.navigationtabstrip.NavigationTabStrip
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.LimitLine
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
import kotlin.math.roundToInt

class CareThermometerAnalysisActivity : AppCompatActivity(), View.OnClickListener {
    private val customChartManager by lazy { CustomChartManager.getInstance(applicationContext)}
    private val binding by lazy { ActivityCareThermometerAnalysisBinding.inflate(layoutInflater) }
    private val daysArray = intArrayOf(7, 30, 90)

    //평균 체온 인덱스 순서 저장
    private val averageThermometerIndexList = ArrayList<Int>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {

        binding.careThermometerAnalysisNtsAverageChart.setTabIndex(0, true)
        binding.careThermometerAnalysisNtsAbnormalChart.setTabIndex(0, true)
        binding.careThermometerAnalysisNtsNormalChart.setTabIndex(0, true)

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

        binding.careThermometerAnalysisBtnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.careThermometerAnalysisBtnBack -> {
                finish()
            }
        }
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(applicationContext)
        glide.load(R.drawable.background_image_detail).into(binding.careThermometerAnalysisImageViewDetailBackground)
        glide.load(R.drawable.ic_baseline_arrow_back_24).into(binding.careThermometerAnalysisImageViewBack)
    }


    /////////////////////////////////////////////   평균 체온 캔들라인 차트   ///////////////////////////////////////
    //평균 체온 차트 설정
    private fun makeThermometerCandleScatterData(candleScatterDataSet : CandleScatterDataSet) : CandleScatterDataSet {
        val candleDataSet = candleScatterDataSet.candleDataSet
        candleDataSet.apply {
            shadowColor = ContextCompat.getColor(applicationContext, R.color.toss_black_600)             //심지 부분
            shadowWidth = 3f
//            color = ContextCompat.getColor(applicationContext, R.color.toss_black_150)             //레전드 색깔
            neutralColor = ContextCompat.getColor(applicationContext, R.color.transparent)
            setDrawValues(false)
            isHighlightEnabled = false  //터치시 붉은 선 보이기
        }
        //평균 데이터
        val scatterDataSet = candleScatterDataSet.scatterDataSet
        scatterDataSet.apply {
            setDrawHorizontalHighlightIndicator(true)  //클릭 시 선 보이게 하기
            setDrawVerticalHighlightIndicator(true)  //클릭 시 선 보이게 하기
            color = ContextCompat.getColor(applicationContext, R.color.teal_200)
            valueFormatter = CustomChartManager.CustomDecimalYAxisFormatter() //데이터 소수점 표시
            setScatterShape(ScatterChart.ScatterShape.CIRCLE)   //원으로 표시
            setDrawValues(false)    //값 안보이도록
            isHighlightEnabled = true   //클릭시 선 보이게 설정
            highLightColor = ContextCompat.getColor(applicationContext, R.color.circle_red_100) //클릭시 보이는 선 색깔
            scatterShapeSize = 18f
        }
        return candleScatterDataSet
    }

    //평균 체온 차트 설정
    private fun setThermometerCombineChart() {
        val homeThermometerAnalysisAverageChart = binding.careThermometerAnalysisAverageChart

        //마커 뷰 설정
//        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)

        //클릭 리스너 설정
        homeThermometerAnalysisAverageChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            //그래프 터치시 값 변경 리스너
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val index = averageThermometerIndexList.indexOf((e!!.x / 3).toInt())
                val candleData = binding.careThermometerAnalysisAverageChart.data.candleData.dataSets[0].getEntryForIndex(index)
                val scatterData = binding.careThermometerAnalysisAverageChart.data.scatterData.dataSets[0].getEntryForIndex(index)

                val minVal = min(candleData.high, candleData.low)
                val maxVal = max(candleData.high, candleData.low)
                val averageVal = scatterData.y

                binding.careThermometerAnalysisTextViewMinValue.text = "$minVal"
                binding.careThermometerAnalysisTextViewMaxValue.text = "$maxVal"
                binding.careThermometerAnalysisTextViewAverageValue.text = "$averageVal"
                binding.careThermometerAnalysisTextViewMaxUnit.visibility = View.VISIBLE
                binding.careThermometerAnalysisTextViewMinUnit.visibility = View.VISIBLE
                binding.careThermometerAnalysisTextViewAverageUnit.visibility = View.VISIBLE

                //로티 시작하기
                binding.careThermometerAnalysisLottie.playAnimation()
            }
            override fun onNothingSelected() {
            }
        })
        //레전드 각각 설정
        val legend1 = LegendEntry("평균", Legend.LegendForm.CIRCLE, 10f, 2f, null, ContextCompat.getColor(applicationContext, R.color.teal_200))
        val legend2 = LegendEntry("범위", Legend.LegendForm.SQUARE, 10f, 2f, null, ContextCompat.getColor(applicationContext, R.color.toss_black_600))

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
                axisMinimum = 20F   //최소값
                axisMaximum = 45F   //최대값
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(applicationContext, R.color.toss_black_700)
                gridColor = ContextCompat.getColor(applicationContext, R.color.toss_black_150)    //y그리드 색깔 변경


                //배경 색 추가하기
                val increment = 0.01F
                var metricLine = 35F
                for (i in 0..250) {
                    val llRange = LimitLine(metricLine, "")
                    llRange.lineColor = ContextCompat.getColor(applicationContext, R.color.iphone_green_heart_700)
                    llRange.lineWidth = 1f
                    this.addLimitLine(llRange)
                    metricLine += increment
                }
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
        val barDataSet = BarDataSet(values, "비정상 빈도수")
        barDataSet.run {
            color = ContextCompat.getColor(applicationContext, R.color.text_red_200)
            valueFormatter = CustomChartManager.CustomIntegerYAxisFormatter() //데이터 소수점 표시
            valueTextColor = ContextCompat.getColor(applicationContext, R.color.toss_black_500)
            setDrawValues(true)
            valueTextSize = 14F
            isHighlightEnabled = false   //클릭시 마크 보이게
        }
        return barDataSet
    }

    //체온 차트 설정
    private fun setThermometerAbnormalChart() {
        val barThermometerDay = binding.careThermometerAnalysisAbnormalChart
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

    /////////////////////////////////////////////   정상 바 차트   ///////////////////////////////////////
    private fun setThermometerNormalData(values : ArrayList<BarEntry>) : BarDataSet {
        val barDataSet = BarDataSet(values, "정상 빈도수")
        barDataSet.run {
            color = ContextCompat.getColor(applicationContext, R.color.text_blue_200)
            valueFormatter = CustomChartManager.CustomIntegerYAxisFormatter() //데이터 소수점 표시
            valueTextColor = ContextCompat.getColor(applicationContext, R.color.toss_black_500)
            setDrawValues(true)
            valueTextSize = 14F
            isHighlightEnabled = false   //클릭시 마크 보이게
        }
        return barDataSet
    }

    private fun setThermometerNormalChart() {
        val barThermometerDay = binding.careThermometerAnalysisNormalChart

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
        when(flag) {
            //평균
            0 -> {
                binding.careThermometerAnalysisTextViewMinValue.text = "-"
                binding.careThermometerAnalysisTextViewMaxValue.text = "-"
                binding.careThermometerAnalysisTextViewAverageValue.text = "-"
                binding.careThermometerAnalysisTextViewMaxUnit.visibility = View.GONE
                binding.careThermometerAnalysisTextViewMinUnit.visibility = View.GONE
                binding.careThermometerAnalysisTextViewAverageUnit.visibility = View.GONE
                binding.careThermometerAnalysisTextViewPeriodAverage.text = "$start  ~  $end"
            }
            //정상 빈도수
            1 -> binding.careThermometerAnalysisTextViewPeriodNormal.text = "$start  ~  $end"
            //비정상 빈도수
            2 -> binding.careThermometerAnalysisTextViewPeriodAbnormal.text = "$start  ~  $end"

        }
    }
    
    //평균 체온 차트 일수 변경 리스너
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTabStripSelectedIndexListener() {
        binding.careThermometerAnalysisNtsAverageChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {

            override fun onStartTabSelected(title: String?, index: Int) {
                //7, 30, 90 데이터 가져오기
                retrofitGetAnalysisThermometerAverage(daysArray[index])
                setPeriodTextView(daysArray[index].toLong(), 0)
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }
        binding.careThermometerAnalysisNtsAbnormalChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {
            override fun onStartTabSelected(title: String?, index: Int) {
                retrofitGetAnalysisThermometerNormal(daysArray[index])
                binding.careThermometerAnalysisTextViewPeriodNormalDays.text = daysArray[index].toString()

                setPeriodTextView(daysArray[index].toLong(), 1)
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }
        binding.careThermometerAnalysisNtsNormalChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {
            override fun onStartTabSelected(title: String?, index: Int) {
                retrofitGetAnalysisThermometerAbnormal(daysArray[index])
                binding.careThermometerAnalysisTextViewPeriodAbnormalDays.text = daysArray[index].toString()

                setPeriodTextView(daysArray[index].toLong(), 2)
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }


    }
    


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //평균, 최대, 최소 일수별 체온 가져오기
    private fun retrofitGetAnalysisThermometerAverage(day : Int) {
        RetrofitManager.instance.getAnalysisThermometerAverage(_protectingPhoneNumber!!, day, completion = {
            completionResponse, response -> 
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            if(jsonArray.length() == 0) {
                                binding.careThermometerAnalysisAverageChart.clear()
                                binding.careThermometerAnalysisAverageChart.invalidate()
                                return@getAnalysisThermometerAverage
                            }
                            val listCandleData: ArrayList<CandleEntry> = ArrayList()
                            val listScatterData: MutableList<Entry> = ArrayList()
                            averageThermometerIndexList.clear()

                            for(i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val avg = jsonObject.getDouble("avgData").toFloat()
                                val min = jsonObject.getDouble("minData").toFloat()
                                val max = jsonObject.getDouble("maxData").toFloat()
                                val timeSlot = jsonObject.getDouble("timeSlot")
                                //인덱스 순서 저장
                                averageThermometerIndexList.add(timeSlot.toInt())
                                listCandleData.add(CandleEntry((timeSlot * 3 + 1.5).toFloat(), min,  max, avg, avg))
                                listScatterData.add(Entry((timeSlot * 3 + 1.5).toFloat(), (avg  * 10).roundToInt() / 10.0F))
                            }
                            //체온 컴바인 데이터 만들기
                            val candleScatterDataSet = makeThermometerCandleScatterData(CandleScatterDataSet(CandleDataSet(listCandleData, "범위"), ScatterDataSet(listScatterData, "평균")))
                            val combinedData = CombinedData()
                            combinedData.setData(CandleData(candleScatterDataSet.candleDataSet))
                            combinedData.setData(ScatterData(candleScatterDataSet.scatterDataSet))

                            //차트 다시 그리기
                            binding.careThermometerAnalysisAverageChart.clear()
                            binding.careThermometerAnalysisAverageChart.data = combinedData
                            binding.careThermometerAnalysisAverageChart.invalidate()
                            binding.careThermometerAnalysisAverageChart.animateXY(1000, 1000)
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
        RetrofitManager.instance.getAnalysisThermometerNormal(_protectingPhoneNumber!!, day, completion = {
                completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
//                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            if(jsonArray.length() == 0) {
                                binding.careThermometerAnalysisNormalChart.clear()
                                binding.careThermometerAnalysisNormalChart.invalidate()
                                return@getAnalysisThermometerNormal
                            }

                            val listBarData: ArrayList<BarEntry> = ArrayList()
                            //총 개수 카운트
                            var sum = 0
                            for(i in 0 until jsonArray.length()) {
                                val value = jsonArray.getInt(i)
                                sum += value
                                listBarData.add(BarEntry(i.toFloat(), value.toFloat()))
                            }
                            val barDataSet = BarData(setThermometerNormalData(listBarData))
                            binding.careThermometerAnalysisNormalChart.data = barDataSet
                            binding.careThermometerAnalysisNormalChart.invalidate()
                            binding.careThermometerAnalysisNormalChart.animateY( 1000)


                            //총 개수 카운트
                            binding.careThermometerAnalysisTextViewPeriodNormalSum.text = sum.toString()
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
        RetrofitManager.instance.getAnalysisThermometerAbnormal(_protectingPhoneNumber!!, day, completion = {
                completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
//                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            if(jsonArray.length() == 0) {
                                binding.careThermometerAnalysisAbnormalChart.clear()
                                binding.careThermometerAnalysisAbnormalChart.invalidate()
                                return@getAnalysisThermometerAbnormal
                            }

                            val listBarData: ArrayList<BarEntry> = ArrayList()
                            var sum = 0
                            for(i in 0 until jsonArray.length()) {
                                val value = jsonArray.getInt(i)
                                sum += value
                                listBarData.add(BarEntry(i.toFloat(), value.toFloat()))
                            }
                            val barDataSet = BarData(setThermometerAbnormalData(listBarData))
                            binding.careThermometerAnalysisAbnormalChart.data = barDataSet
                            binding.careThermometerAnalysisAbnormalChart.invalidate()
                            binding.careThermometerAnalysisAbnormalChart.animateY( 1000)

                            binding.careThermometerAnalysisTextViewPeriodAbnormalSum.text = sum.toString()
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