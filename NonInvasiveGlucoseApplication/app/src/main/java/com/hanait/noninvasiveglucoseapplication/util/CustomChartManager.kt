package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.home.MyValueFormatter
import java.util.*
import kotlin.math.roundToInt

class CustomChartManager(context: Context) {
    var context: Context = context

    companion object {
        @SuppressLint("StaticFieldLeak")
        var INSTANCE: CustomChartManager? = null
        fun getInstance(context: Context): CustomChartManager {
            if (INSTANCE == null) {
                INSTANCE = CustomChartManager(context)
            }
            return INSTANCE as CustomChartManager
        }
    }

    //실수 난수 생성
    fun makeThermometerDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 0 until 20) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "심박수")

    }

    //실수 난수 생성
    fun makeHeartDataSet() : LineDataSet {
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
    fun makeBarDataSet() : BarDataSet {
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
    //심박수 막대 데이터 생성성
    fun setThermometerDashboardLineData() : LineDataSet {
        val lineDataSet = makeThermometerDataSet()
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2F
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_blue_100))
        lineDataSet.lineWidth = 2F //선 굵기
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.valueFormatter = MyValueFormatter()
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawValues(true)
        lineDataSet.valueTextSize = 0F
        lineDataSet.fillAlpha = 50
        lineDataSet.highLightColor = Color.RED
        lineDataSet.highlightLineWidth = 1.0F
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }

    //체온 라인데이터 생성
    fun setHeartLineData() : LineDataSet {
        val lineDataSet = makeHeartDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
        lineDataSet.cubicIntensity = 0.2F
        lineDataSet.setDrawFilled(false)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_red_100))
        lineDataSet.lineWidth = 2F
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawValues(true)
        lineDataSet.valueTextSize = 0F
        lineDataSet.fillAlpha = 50
        lineDataSet.valueFormatter = MyValueFormatter()
        lineDataSet.highLightColor = Color.RED
        lineDataSet.highlightLineWidth = 1.0F
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }

    //체온 막대 데이터 생성
    fun setSugarBloodBarData() : BarDataSet {
        val barDataSet = makeBarDataSet()
        barDataSet.valueFormatter = MyValueFormatter()  //소수 첫째 자리까지 표시
        barDataSet.setColor(ContextCompat.getColor(context, R.color.graph_orange_100))
        barDataSet.valueTextSize = 0F
        return barDataSet
    }

//==================================================================================================
    //심박수 막대 데이터 생성성
    fun setThermometerDayLineData() : LineDataSet {
        val lineDataSet = makeThermometerDataSet()
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_blue_100))
        lineDataSet.lineWidth = 3F //선 굵기
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.valueFormatter = MyValueFormatter()
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawValues(true)
        lineDataSet.valueTextSize = 0F
        lineDataSet.fillAlpha = 50
        lineDataSet.highLightColor = Color.RED
        lineDataSet.highlightLineWidth = 1.0F
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }
}