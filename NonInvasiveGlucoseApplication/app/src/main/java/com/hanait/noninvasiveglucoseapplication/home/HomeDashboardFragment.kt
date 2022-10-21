package com.hanait.noninvasiveglucoseapplication.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeDashboardBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomChartManager
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
                startActivity(intent)
            }
            binding.homeDashboardCardViewHeart -> {

            }
            binding.homeDashboardCardViewSugarBlood -> {

            }
        }
    }


    //=================================================================================================

    //체온 차트 설정
    private fun setThermometerLineChart() {
        val thermometerLineData =  customChartManager.setThermometerDashboardLineData()
        val lineData = LineData(thermometerLineData)
        val lineThermometerHeart = binding.homeDashboardLineChartThermometer
        lineThermometerHeart.data = lineData
        lineThermometerHeart.setPinchZoom(false)
        lineThermometerHeart.description.isEnabled = false
        lineThermometerHeart.legend.isEnabled = false
        lineThermometerHeart.setTouchEnabled(false)
        lineThermometerHeart.setDrawGridBackground(false)
        lineThermometerHeart.xAxis.setDrawGridLines(false)
        lineThermometerHeart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        //        lineThermometerHeart.axisLeft.setDrawGridLines(false)
        lineThermometerHeart.axisRight.isEnabled = false
//        lineThermometerHeart.axisLeft.isEnabled = false
        lineThermometerHeart.animateXY(1000, 1000)
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