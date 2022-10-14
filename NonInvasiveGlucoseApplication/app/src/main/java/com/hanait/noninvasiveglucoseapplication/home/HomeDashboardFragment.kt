package com.hanait.noninvasiveglucoseapplication.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeDashboardBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import java.util.*


class HomeDashboardFragment : BaseFragment<FragmentHomeDashboardBinding>(FragmentHomeDashboardBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lineDataSet = makeDataSet()
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2F
        lineDataSet.setDrawFilled(true)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
//        lineDataSet.color = ColorTemplate.getHoloBlue()
        lineDataSet.lineWidth = 2F
        lineDataSet.setDrawCircles(false)
//        lineDataSet.setDrawValues(true)
        lineDataSet.valueTextSize = 0F
        lineDataSet.fillAlpha = 50
//        lineDataSet.fillColor = Color.LTGRAY
//        lineDataSet.fillColor = ColorTemplate.getHoloBlue()
        lineDataSet.setGradientColor(R.color.border_yellow_200, R.color.white)
        lineDataSet.highLightColor = Color.RED
        lineDataSet.highlightLineWidth = 1.0F
        lineDataSet.setDrawCircleHole(false)

        val lineData = LineData(lineDataSet)
        binding.homeDashboardLineChartThermometer.data = lineData
        binding.homeDashboardLineChartThermometer.setDrawGridBackground(false)
        binding.homeDashboardLineChartThermometer.setPinchZoom(false)
        binding.homeDashboardLineChartThermometer.description.isEnabled = false
        binding.homeDashboardLineChartThermometer.axisLeft.setDrawGridLines(false)
        binding.homeDashboardLineChartThermometer.axisRight.isEnabled = false
        binding.homeDashboardLineChartThermometer.axisLeft.isEnabled = false
        binding.homeDashboardLineChartThermometer.setTouchEnabled(false)
        binding.homeDashboardLineChartThermometer.xAxis.setDrawGridLines(false)
        binding.homeDashboardLineChartThermometer.legend.isEnabled = false
        binding.homeDashboardLineChartThermometer.animateXY(1000, 1000)
    }


    //실수 난수 생성
    private fun makeDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 0 until 30) {
            entry.add(Entry(i.toFloat(), (min + random.nextDouble() * (max - min)).toFloat()))
        }
        return LineDataSet(entry, "심박수")
    }
}