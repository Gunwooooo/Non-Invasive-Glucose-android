package com.hanait.noninvasiveglucoseapplication.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeDashboardBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt


class HomeDashboardFragment : BaseFragment<FragmentHomeDashboardBinding>(FragmentHomeDashboardBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHeartLineChart()
        setThermometerLineChart()
        setSugarBloodBarChart()
    }


    //실수 난수 생성
    private fun makeThermometerDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 0 until 30) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "심박수")

    }

    //실수 난수 생성
    private fun makeHeartDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 0 until 50) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "심박수")

    }

    //실수 난수 생성
    private fun makeBarDataSet() : BarDataSet {
        val entry: MutableList<BarEntry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 0 until 30) {
            entry.add(BarEntry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return BarDataSet(entry, "혈당")
    }

//=================================================================================================


    //당뇨 라인데이터 생성
    private fun setThermometerLineData() : LineDataSet {
        val lineDataSet = makeThermometerDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
        lineDataSet.cubicIntensity = 0.2F
        lineDataSet.setDrawFilled(false)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_red)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.text_red_200))
//        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
//        lineDataSet.color = ColorTemplate.getHoloBlue()
        lineDataSet.lineWidth = 2F
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawValues(true)
        lineDataSet.valueTextSize = 0F
        lineDataSet.fillAlpha = 50
        lineDataSet.valueFormatter = MyValueFormatter()
//        lineDataSet.fillColor = Color.LTGRAY
//        lineDataSet.fillColor = ColorTemplate.getHoloBlue()
        lineDataSet.highLightColor = Color.RED
        lineDataSet.highlightLineWidth = 1.0F
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }

    //심박수 막대 데이터 생성성
   private fun setHeartLineData() : LineDataSet {
        val lineDataSet = makeHeartDataSet()
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2F
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_red)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
//        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.setColor(Color.BLACK)
        lineDataSet.setCircleColor(Color.BLACK)
        lineDataSet.lineWidth = 2F //선 굵기
        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.valueFormatter = MyValueFormatter()
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawValues(true)
        lineDataSet.valueTextSize = 0F
        lineDataSet.fillAlpha = 50
//        lineDataSet.fillColor = Color.LTGRAY
//        lineDataSet.fillColor = ColorTemplate.getHoloBlue()
        lineDataSet.highLightColor = Color.RED
        lineDataSet.highlightLineWidth = 1.0F
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }

    //체온 막대 데이터 생성
    private fun setSugarBloodBarData() : BarDataSet {
        val barDataSet = makeBarDataSet()
        barDataSet.valueFormatter = MyValueFormatter()  //소수 첫째 자리까지 표시
        barDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.toss_black_600))
        barDataSet.valueTextSize = 0F
        return barDataSet
    }
    //=================================================================================================
    private fun setThermometerLineChart() {
        val heartLineData = setThermometerLineData()
        val lineData = LineData(heartLineData)
        val lineChartHeart = binding.homeDashboardLineChartThermometer
        lineChartHeart.data = lineData
        lineChartHeart.setDrawGridBackground(false)
//        lineChartHeart.setPinchZoom(false)
        lineChartHeart.description.isEnabled = false
        lineChartHeart.axisLeft.setDrawGridLines(false)
        lineChartHeart.axisRight.isEnabled = false
        lineChartHeart.axisLeft.isEnabled = false
        lineChartHeart.setTouchEnabled(false)
        lineChartHeart.xAxis.setDrawGridLines(false)
        lineChartHeart.legend.isEnabled = false
        lineChartHeart.animateXY(1000, 1000)
    }

    //심박수 차트 설정
    private fun setHeartLineChart() {
        val heartLineData = setHeartLineData()
        val lineData = LineData(heartLineData)
        val lineChartHeart = binding.homeDashboardLineChartHeart
        lineChartHeart.data = lineData
        lineChartHeart.setDrawGridBackground(false)
//        lineChartHeart.setPinchZoom(false)
        lineChartHeart.description.isEnabled = false
        lineChartHeart.axisLeft.setDrawGridLines(false)
        lineChartHeart.axisRight.isEnabled = false
        lineChartHeart.axisLeft.isEnabled = false
        lineChartHeart.setTouchEnabled(false)
        lineChartHeart.xAxis.setDrawGridLines(false)
        lineChartHeart.legend.isEnabled = false
        lineChartHeart.animateXY(1000, 1000)
    }



    //혈당 차트 설정
    private fun setSugarBloodBarChart() {
        val sugarBloodData = setSugarBloodBarData()
        val barData = BarData(sugarBloodData)
        val barChartSugarBlood = binding.homeDashboardBarChartSugarBlood
        barChartSugarBlood.description.isEnabled = false //오른쪽 하단 설명 글 없애기
        barChartSugarBlood.legend.isEnabled = false //하단에 레전드 없애기
        barChartSugarBlood.setTouchEnabled(false)

        barChartSugarBlood.data = barData
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