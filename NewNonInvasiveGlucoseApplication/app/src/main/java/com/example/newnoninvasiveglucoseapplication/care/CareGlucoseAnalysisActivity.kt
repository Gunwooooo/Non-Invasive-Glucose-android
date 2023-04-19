package com.example.newnoninvasiveglucoseapplication.care

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityHomeGlucoseAnalysisBinding
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.CandleScatterDataSet
import com.example.newnoninvasiveglucoseapplication.util.CustomChartManager
import com.example.newnoninvasiveglucoseapplication.util.LoginedUserClient
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
import java.util.ArrayList
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class CareGlucoseAnalysisActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeGlucoseAnalysisBinding.inflate(layoutInflater) }
    private val daysArray = intArrayOf(7, 30, 90)

    //평균 혈당 인덱스 순서 저장
    private val averageGlucoseIndexList = ArrayList<Int>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        init()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        binding.homeGlucoseAnalysisNtsAverageChart.setTabIndex(0, true)
        binding.homeGlucoseAnalysisNtsAbnormalChart.setTabIndex(0, true)
        binding.homeGlucoseAnalysisNtsNormalChart.setTabIndex(0, true)

        //초기 글라이드로 이미지 불러오기
        setImageViewWithGlide()

        //차트 보여주기 설정하기
        setGlucoseCombineChart()
        setGlucoseAbnormalChart()
        setGlucoseNormalChart()

        //일수 선택 리스너 설정
        setTabStripSelectedIndexListener()

        //평균, 최대, 최소 혈당 데이터 가져오기
        retrofitGetAnalysisGlucoseAverage(7)
        retrofitGetAnalysisGlucoseNormal(7)
        retrofitGetAnalysisGlucoseAbnormal(7)

        //초기 기간(7일)로 텍스트뷰 범위 세팅 플래그 별로 각각 평균, 정상, 비정상
        setPeriodTextView(7, 0)
        setPeriodTextView(7, 1)
        setPeriodTextView(7, 2)

        binding.homeGlucoseAnalysisBtnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeGlucoseAnalysisBtnBack -> {
                finish()
            }
        }
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(applicationContext)
        glide.load(R.drawable.background_image_detail).into(binding.homeGlucoseAnalysisImageViewDetailBackground)
        glide.load(R.drawable.ic_baseline_arrow_back_24).into(binding.homeGlucoseAnalysisImageViewBack)
    }


    /////////////////////////////////////////////   평균 혈당 캔들라인 차트   ///////////////////////////////////////
    //평균 혈당 차트 설정
    private fun makeGlucoseCandleScatterData(candleScatterDataSet : CandleScatterDataSet) : CandleScatterDataSet {
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

    //평균 혈당 차트 설정
    private fun setGlucoseCombineChart() {
        val homeGlucoseAnalysisAverageChart = binding.homeGlucoseAnalysisAverageChart

        //마커 뷰 설정
//        val markerView = CustomMarkerViewManager(applicationContext, R.layout.custom_marker_view)

        //클릭 리스너 설정
        homeGlucoseAnalysisAverageChart.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            //그래프 터치시 값 변경 리스너
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val index = averageGlucoseIndexList.indexOf((e!!.x / 3).toInt())
                val candleData = binding.homeGlucoseAnalysisAverageChart.data.candleData.dataSets[0].getEntryForIndex(index)
                val scatterData = binding.homeGlucoseAnalysisAverageChart.data.scatterData.dataSets[0].getEntryForIndex(index)

                val minVal = min(candleData.high, candleData.low)
                val maxVal = max(candleData.high, candleData.low)
                val averageVal = scatterData.y

                binding.homeGlucoseAnalysisTextViewMinValue.text = "$minVal"
                binding.homeGlucoseAnalysisTextViewMaxValue.text = "$maxVal"
                binding.homeGlucoseAnalysisTextViewAverageValue.text = "$averageVal"
                binding.homeGlucoseAnalysisTextViewMaxUnit.visibility = View.VISIBLE
                binding.homeGlucoseAnalysisTextViewMinUnit.visibility = View.VISIBLE
                binding.homeGlucoseAnalysisTextViewAverageUnit.visibility = View.VISIBLE

                //로티 시작하기
                binding.homeGlucoseAnalysisLottie.playAnimation()
            }
            override fun onNothingSelected() {
            }
        })
        //레전드 각각 설정
        val legend1 = LegendEntry("평균", Legend.LegendForm.CIRCLE, 10f, 2f, null, ContextCompat.getColor(applicationContext, R.color.teal_200))
        val legend2 = LegendEntry("범위", Legend.LegendForm.SQUARE, 10f, 2f, null, ContextCompat.getColor(applicationContext, R.color.toss_black_600))

        homeGlucoseAnalysisAverageChart.run {
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
                axisMinimum = 0F   //최소값
                axisMaximum = 150F   //최대값
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
    private fun setGlucoseAbnormalData(values : ArrayList<BarEntry>) : BarDataSet {
        val barDataSet = BarDataSet(values, "비정상 혈당 빈도수")
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

    //혈당 차트 설정
    private fun setGlucoseAbnormalChart() {
        val barGlucoseDay = binding.homeGlucoseAnalysisAbnormalChart
        barGlucoseDay.run {
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
    private fun setGlucoseNormalData(values : ArrayList<BarEntry>) : BarDataSet {
        val barDataSet = BarDataSet(values, "정상 혈당 빈도수")
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

    private fun setGlucoseNormalChart() {
        val barGlucoseDay = binding.homeGlucoseAnalysisNormalChart

        barGlucoseDay.run {
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
            0 -> {
                binding.homeGlucoseAnalysisTextViewMinValue.text = "-"
                binding.homeGlucoseAnalysisTextViewMaxValue.text = "-"
                binding.homeGlucoseAnalysisTextViewAverageValue.text = "-"
                binding.homeGlucoseAnalysisTextViewMaxUnit.visibility = View.GONE
                binding.homeGlucoseAnalysisTextViewMinUnit.visibility = View.GONE
                binding.homeGlucoseAnalysisTextViewAverageUnit.visibility = View.GONE
                binding.homeGlucoseAnalysisTextViewPeriodAverage.text = "$start  ~  $end"
            }
            1 -> binding.homeGlucoseAnalysisTextViewPeriodNormal.text = "$start  ~  $end"
            2 -> binding.homeGlucoseAnalysisTextViewPeriodAbnormal.text = "$start  ~  $end"

        }
    }

    //평균 혈당 차트 일수 변경 리스너
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setTabStripSelectedIndexListener() {
        binding.homeGlucoseAnalysisNtsAverageChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {

            override fun onStartTabSelected(title: String?, index: Int) {
                //7, 30, 90 데이터 가져오기
                retrofitGetAnalysisGlucoseAverage(daysArray[index])
                setPeriodTextView(daysArray[index].toLong(), 0)
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }
        binding.homeGlucoseAnalysisNtsAbnormalChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {
            override fun onStartTabSelected(title: String?, index: Int) {
                retrofitGetAnalysisGlucoseNormal(daysArray[index])
                binding.homeGlucoseAnalysisTextViewPeriodNormalDays.text = daysArray[index].toString()

                setPeriodTextView(daysArray[index].toLong(), 1)
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }
        binding.homeGlucoseAnalysisNtsNormalChart.onTabStripSelectedIndexListener = object : NavigationTabStrip.OnTabStripSelectedIndexListener {
            override fun onStartTabSelected(title: String?, index: Int) {
                retrofitGetAnalysisGlucoseAbnormal(daysArray[index])
                binding.homeGlucoseAnalysisTextViewPeriodAbnormalDays.text = daysArray[index].toString()

                setPeriodTextView(daysArray[index].toLong(), 2)
            }
            override fun onEndTabSelected(title: String?, index: Int) {
            }
        }


    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //평균, 최대, 최소 일수별 혈당 가져오기
    private fun retrofitGetAnalysisGlucoseAverage(day : Int) {
        RetrofitManager.instance.getAnalysisGlucoseAverage(LoginedUserClient.phoneNumber!!, day, completion = {
                completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            if(jsonArray.length() == 0) return@getAnalysisGlucoseAverage

                            val listCandleData: ArrayList<CandleEntry> = ArrayList()
                            val listScatterData: MutableList<Entry> = ArrayList()
                            averageGlucoseIndexList.clear()

                            for(i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val avg = jsonObject.getDouble("avgData").toFloat()
                                val min = jsonObject.getDouble("minData").toFloat()
                                val max = jsonObject.getDouble("maxData").toFloat()
                                val timeSlot = jsonObject.getDouble("timeSlot")
                                //인덱스 순서 저장
                                averageGlucoseIndexList.add(timeSlot.toInt())
                                listCandleData.add(CandleEntry((timeSlot * 3 + 1.5).toFloat(), min,  max, avg, avg))
                                listScatterData.add(Entry((timeSlot * 3 + 1.5).toFloat(), (avg  * 10).roundToInt() / 10.0F))
                            }
                            //혈당 컴바인 데이터 만들기
                            val candleScatterDataSet = makeGlucoseCandleScatterData(
                                CandleScatterDataSet(CandleDataSet(listCandleData, "범위"), ScatterDataSet(listScatterData, "평균"))
                            )
                            val combinedData = CombinedData()
                            combinedData.setData(CandleData(candleScatterDataSet.candleDataSet))
                            combinedData.setData(ScatterData(candleScatterDataSet.scatterDataSet))

                            //차트 다시 그리기
                            binding.homeGlucoseAnalysisAverageChart.clear()
                            binding.homeGlucoseAnalysisAverageChart.data = combinedData
                            binding.homeGlucoseAnalysisAverageChart.invalidate()
                            binding.homeGlucoseAnalysisAverageChart.animateXY(1000, 1000)
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d(
                        "로그",
                        "HomeGlucoseAnalysisActivity - retrofitGetAnalysisGlucoseAverage : 통신 실패"
                    )
                }
            }
        })
    }

    //정상 범위 혈당 빈도수 가져오기
    private fun retrofitGetAnalysisGlucoseNormal(day : Int) {
        RetrofitManager.instance.getAnalysisGlucoseNormal(LoginedUserClient.phoneNumber!!, day, completion = {
                completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
//                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            if(jsonArray.length() == 0) return@getAnalysisGlucoseNormal

                            val listBarData: ArrayList<BarEntry> = ArrayList()
                            //총 개수 카운트
                            var sum = 0
                            for(i in 0 until jsonArray.length()) {
                                val value = jsonArray.getInt(i)
                                sum += value
                                listBarData.add(BarEntry(i.toFloat(), value.toFloat()))
                            }
                            val barDataSet = BarData(setGlucoseNormalData(listBarData))
                            binding.homeGlucoseAnalysisNormalChart.data = barDataSet
                            binding.homeGlucoseAnalysisNormalChart.invalidate()
                            binding.homeGlucoseAnalysisNormalChart.animateY( 1000)

                            binding.homeGlucoseAnalysisTextViewPeriodNormalSum.text = sum.toString()
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeGlucoseAnalysisActivity - retrofitGetAnalysisGlucoseAverage : 통신 실패")
                }
            }
        })
    }

    //정상 범위 혈당 빈도수 가져오기
    private fun retrofitGetAnalysisGlucoseAbnormal(day : Int) {
        RetrofitManager.instance.getAnalysisGlucoseAbnormal(LoginedUserClient.phoneNumber!!, day, completion = {
                completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response!!.code()) {
                        200 -> {
//                            //서버에서 건강 데이터 리스트 받아오기
                            val jsonArray = JSONArray(response.body()!!.string())
                            if(jsonArray.length() == 0) return@getAnalysisGlucoseAbnormal

                            val listBarData: ArrayList<BarEntry> = ArrayList()
                            var sum = 0
                            for(i in 0 until jsonArray.length()) {
                                val value = jsonArray.getInt(i)
                                sum += value
                                listBarData.add(BarEntry(i.toFloat(), value.toFloat()))
                            }
                            val barDataSet = BarData(setGlucoseAbnormalData(listBarData))
                            binding.homeGlucoseAnalysisAbnormalChart.data = barDataSet
                            binding.homeGlucoseAnalysisAbnormalChart.invalidate()
                            binding.homeGlucoseAnalysisAbnormalChart.animateY( 1000)

                            binding.homeGlucoseAnalysisTextViewPeriodAbnormalSum.text = sum.toString()
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeGlucoseAnalysisActivity - retrofitGetAnalysisGlucoseAbnormal : 통신 실패")
                }
            }
        })
    }
}