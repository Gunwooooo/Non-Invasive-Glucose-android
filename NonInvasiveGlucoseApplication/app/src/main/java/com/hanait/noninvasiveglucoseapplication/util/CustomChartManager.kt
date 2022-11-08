package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.hanait.noninvasiveglucoseapplication.R
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

    inner class MyXAxisFormatter : ValueFormatter(){
        private val days = arrayOf("1차","2차","3차","4차","5차","6차","7차")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days.getOrNull(value.toInt()-1) ?: value.toString()
        }
    }

    //실수 난수 생성
    fun makeThermometerDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 0 until 50) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "체온")
    }

    //실수 난수 생성
    fun makeHeartDataSet() : LineDataSet {
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
    fun makeGlucoseDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 0 until 50) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "혈당")
    }

    //=================================================================================================
    //심박수 막대 데이터 생성성
    fun setThermometerDashboardLineData() : LineDataSet {
        val lineDataSet = makeThermometerDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
//        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_thermometer_bar_100))
        lineDataSet.lineWidth = 3F //선 굵기
//        lineDataSet.circleRadius = 7F
//        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.valueFormatter = MyXAxisFormatter()
        lineDataSet.setDrawCircles(false)   //동그란거 없애기
        lineDataSet.setDrawValues(false)
//        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_thermometer_line_100))
        lineDataSet.valueTextSize = 0F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        lineDataSet.isHighlightEnabled = true   //클릭시 마크 보이게
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
        lineDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
        lineDataSet.setDrawCircleHole(false)
        return lineDataSet
    }

    //체온 라인데이터 생성
    fun setHeartLineData() : LineDataSet {
        val lineDataSet = makeHeartDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
//        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_heart_100))
        lineDataSet.lineWidth = 3F //선 굵기
//        lineDataSet.circleRadius = 7F
//        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.valueFormatter = MyXAxisFormatter()
        lineDataSet.setDrawCircles(false)   //동그란거 없애기
        lineDataSet.setDrawValues(false)
//        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_thermometer_line_100))
        lineDataSet.valueTextSize = 0F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        lineDataSet.isHighlightEnabled = true   //클릭시 마크 보이게
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
        lineDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
        lineDataSet.setDrawCircleHole(false)
        return lineDataSet
    }

    //체온 라인데이터 생성
    fun setGlucoseLineData() : LineDataSet {
        val lineDataSet = makeGlucoseDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
//        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_glucose_100))
        lineDataSet.lineWidth = 3F //선 굵기
//        lineDataSet.circleRadius = 7F
//        lineDataSet.cir`cleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.valueFormatter = MyXAxisFormatter()
        lineDataSet.setDrawCircles(false)   //동그란거 없애기
        lineDataSet.setDrawValues(false)
//        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_thermometer_line_100))
        lineDataSet.valueTextSize = 0F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        lineDataSet.isHighlightEnabled = true   //클릭시 마크 보이게
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
        lineDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
        lineDataSet.setDrawCircleHole(false)
        return lineDataSet
    }

//==================================================================================================
    //심박수 상세 그래프 생성
    fun setThermometer7DayLineData() : LineDataSet {
        val lineDataSet = makeThermometerDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
//        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_thermometer_bar_100))
        lineDataSet.lineWidth = 3F //선 굵기
        lineDataSet.circleRadius = 7F
        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.valueFormatter = MyXAxisFormatter()
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawValues(true)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_thermometer_bar_100))
        lineDataSet.valueTextSize = 0F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        lineDataSet.isHighlightEnabled = true
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
        lineDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }

    //심박수 상세 그래프 생성
    fun setHeart7DayLineData() : LineDataSet {
        val lineDataSet = makeThermometerDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
//        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_heart_100))
        lineDataSet.lineWidth = 3F //선 굵기
        lineDataSet.circleRadius = 7F
        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.valueFormatter = MyXAxisFormatter()
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawValues(true)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_heart_100))
        lineDataSet.valueTextSize = 0F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        lineDataSet.isHighlightEnabled = true
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
        lineDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }

    //심박수 상세 그래프 생성
    fun setGlucose7DayLineData() : LineDataSet {
        val lineDataSet = makeThermometerDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
//        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_glucose_100))
        lineDataSet.lineWidth = 3F //선 굵기
        lineDataSet.circleRadius = 7F
        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.valueFormatter = MyXAxisFormatter()
        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawValues(true)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_glucose_100))
        lineDataSet.valueTextSize = 0F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        lineDataSet.isHighlightEnabled = true
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
        lineDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }

}