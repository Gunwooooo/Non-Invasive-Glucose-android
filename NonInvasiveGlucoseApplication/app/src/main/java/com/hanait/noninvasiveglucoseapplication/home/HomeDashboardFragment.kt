package com.hanait.noninvasiveglucoseapplication.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeDashboardBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomChartManager
import com.hanait.noninvasiveglucoseapplication.util.CustomMarkerView
import java.text.DecimalFormat


class HomeDashboardFragment : BaseFragment<FragmentHomeDashboardBinding>(FragmentHomeDashboardBinding::inflate), View.OnClickListener {
    lateinit var customChartManager: CustomChartManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        customChartManager = CustomChartManager.getInstance(requireContext())

        setThermometerLineChart()
        setHeartLineChart()
        setSugarBloodBarChart()

        binding.homeDashboardCardViewThermometer.setOnClickListener(this)
        binding.homeDashboardCardViewHeart.setOnClickListener(this)
        binding.homeDashboardCardViewSugarBlood.setOnClickListener(this)


    }


    override fun onClick(v: View?) {
        when (v) {
            binding.homeDashboardCardViewThermometer -> {
                val intent = Intent(context, HomeAnalysisActivity::class.java)
                intent.putExtra("TabNumber", 0)
                startActivity(intent)
            }
            binding.homeDashboardCardViewHeart -> {
                val intent = Intent(context, HomeAnalysisActivity::class.java)
                intent.putExtra("TabNumber", 1)
                startActivity(intent)
            }
            binding.homeDashboardCardViewSugarBlood -> {
                val intent = Intent(context, HomeAnalysisActivity::class.java)
                intent.putExtra("TabNumber", 2)
                startActivity(intent)
            }
        }
    }


    //=================================================================================================

    //체온 차트 설정
    private fun setThermometerLineChart() {
        val thermometerLineData =  customChartManager.setThermometerDashboardLineData()
        val lineData = LineData(thermometerLineData)
        val lineThermometerDay = binding.homeDashboardLineChartThermometer
        //마커 뷰 설정
        val markerView = CustomMarkerView(context, R.layout.custom_marker_view)
        lineThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = lineData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
//            isDragEnabled = true
            isScaleXEnabled = false //가로 확대 없애기
//            enableScroll()
//            setVisibleXRangeMaximum(7f) //

            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
//                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 32F   //최소값
                axisMaximum = 55F   //최대값
                isEnabled = true
                animateX(2000)
                animateY(2000)
                textSize = 12f
                gridColor =
                    ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
                isEnabled = false  //오른쪽 y축 없애기
            }
            legend.run {
                isEnabled = true //레전드 아이콘 표시
                form = Legend.LegendForm.CIRCLE
                textSize = 16f
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }

    //심박수 차트 설정
    private fun setHeartLineChart() {
        val heartLineData = customChartManager.setHeartLineData()
        val lineData = LineData(heartLineData)
        val lineChartHeart = binding.homeDashboardLineChartHeart
        lineChartHeart.data = lineData
        lineChartHeart.setPinchZoom(false)
        lineChartHeart.description.isEnabled = false
        lineChartHeart.legend.isEnabled = false
        lineChartHeart.setTouchEnabled(false)
        lineChartHeart.xAxis.position = XAxis.XAxisPosition.BOTTOM //x값 아래로 이동
//        lineChartHeart.axisLeft.setDrawGridLines(false)
//        lineChartHeart.setDrawGridBackground(false)
        lineChartHeart.axisRight.isEnabled = false
//        lineChartHeart.axisLeft.isEnabled = false
        lineChartHeart.xAxis.setDrawGridLines(false)
        lineChartHeart.animateXY(1000, 1000)
    }
    //혈당 차트 설정
    private fun setSugarBloodBarChart() {
        val sugarBloodData = customChartManager.setSugarBloodBarData()
        val barData = BarData(sugarBloodData)
        val barChartSugarBlood = binding.homeDashboardBarChartSugarBlood
        barChartSugarBlood.description.isEnabled = false //오른쪽 하단 설명 글 없애기
        barChartSugarBlood.legend.isEnabled = false //하단에 레전드 없애기
        barChartSugarBlood.setTouchEnabled(false)
        barChartSugarBlood.data = barData
        barChartSugarBlood.xAxis.setDrawGridLines(false) //세로줄 없애기
        barChartSugarBlood.xAxis.position = XAxis.XAxisPosition.BOTTOM //x값 아래로 이동
        barChartSugarBlood.axisRight.isEnabled = false //오른쪽 y축 없애기
    }
}

// 소숫점 한 자리까지 보이기 위한 Formatter
class MyValueFormatter : ValueFormatter(),
    IValueFormatter {
    private val mFormat: DecimalFormat = DecimalFormat("###,###,##0.0")
    fun getFormattedValue(value: Float, entry: Map.Entry<*, *>?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?
    ): String {
        // write your logic here
        return mFormat.format(value).toString() + " $" // e.g. append a dollar-sign
    }
}