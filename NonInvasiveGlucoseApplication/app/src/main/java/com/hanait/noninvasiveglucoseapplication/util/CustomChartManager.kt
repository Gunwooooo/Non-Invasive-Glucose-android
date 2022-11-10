package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
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

    class CustomTimeXAxisFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val time = value.toInt()
//            if(time )
            return "오후${time}시"
        }
    }
    
    class CustomDateXAxisFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val time = value.toInt()
            return "${time}일"
        }
    }

    //실수 난수 생성
    fun makeThermometerDashboardLineDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 50) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "체온")
    }

    //실수 난수 생성
    fun makeHeartDashboardLineDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 50) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "심박수")
    }

    //실수 난수 생성
    fun makeGlucoseDashboardLineDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 50) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "혈당")
    }

    //==============================================================================================
    //캔들 데이터 생성
    fun makeThermometer7dayCandleDataSet() : CandleDataSet {
        val entry: MutableList<CandleEntry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 50) {
            val shadowHigh = (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)
            val shadowLow = (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)
            entry.add(CandleEntry(i.toFloat(), shadowHigh,  shadowLow, shadowLow, shadowHigh))
        }
        return CandleDataSet(entry, "체온")
    }

    //캔들 데이터 생성
    fun makeHeart7dayCandleDataSet() : CandleDataSet {
        val entry: MutableList<CandleEntry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 50) {
            val shadowHigh = (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)
            val shadowLow = (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)
            entry.add(CandleEntry(i.toFloat(), shadowHigh,  shadowLow, shadowLow, shadowHigh))
        }
        return CandleDataSet(entry, "심박수")
    }

    //캔들 데이터 생성
    fun makeGlucose7dayCandleDataSet() : CandleDataSet {
        val entry: MutableList<CandleEntry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 50) {
            val shadowHigh = (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)
            val shadowLow = (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)
            entry.add(CandleEntry(i.toFloat(), shadowHigh,  shadowLow, shadowLow, shadowHigh))
        }
        return CandleDataSet(entry, "혈당")
    }

    //=================================================================================================
    //심박수 막대 데이터 생성성
    fun setThermometerDashboardLineData() : LineDataSet {
        val lineDataSet = makeThermometerDashboardLineDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
//        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_thermometer_100))
        lineDataSet.lineWidth = 3F //선 굵기
        lineDataSet.circleRadius = 7F
        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.setDrawCircles(true)   //동그란거 없애기
        lineDataSet.setDrawValues(true)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_thermometer_100))
        lineDataSet.valueTextSize = 0F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        lineDataSet.isHighlightEnabled = true   //클릭시 마크 보이게
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
        lineDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }

    //체온 라인데이터 생성
    fun setHeartLineData() : LineDataSet {
        val lineDataSet = makeHeartDashboardLineDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
//        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_heart_100))
        lineDataSet.lineWidth = 3F //선 굵기
        lineDataSet.circleRadius = 7F
        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.setDrawCircles(true)   //동그란거 없애기
        lineDataSet.setDrawValues(true)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_heart_100))
        lineDataSet.valueTextSize = 0F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        lineDataSet.isHighlightEnabled = true   //클릭시 마크 보이게
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
        lineDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }

    //체온 라인데이터 생성
    fun setGlucoseLineData() : LineDataSet {
        val lineDataSet = makeGlucoseDashboardLineDataSet()
        lineDataSet.mode = LineDataSet.Mode.LINEAR
//        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.graph_glucose_100))
        lineDataSet.lineWidth = 3F //선 굵기
        lineDataSet.circleRadius = 7F
        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.setDrawCircles(true)   //동그란거 없애기
        lineDataSet.setDrawValues(true)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_glucose_100))
        lineDataSet.valueTextSize = 0F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        lineDataSet.isHighlightEnabled = true   //클릭시 마크 보이게
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
        lineDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
        lineDataSet.setDrawCircleHole(true)
        return lineDataSet
    }

//==================================================================================================
    //체온 7일 상세 그래프 생성
    fun setThermometer7DayCandleData() : CandleDataSet {
        val candleDataSet = makeThermometer7dayCandleDataSet()
        candleDataSet.run {
            //심지 부분
            shadowColor = Color.LTGRAY
            shadowWidth = 1f

            //음봉
            decreasingColor = ContextCompat.getColor(context, R.color.graph_thermometer_100)
            decreasingPaintStyle = Paint.Style.FILL

            //양봉
            increasingColor = ContextCompat.getColor(context, R.color.graph_thermometer_100)
            increasingPaintStyle = Paint.Style.FILL

            neutralColor = Color.DKGRAY
            setDrawValues(false)

            //터치시 노란 선 제거
            highLightColor = Color.TRANSPARENT
        }
        return candleDataSet
    }

    //심박수 7일 그래프 생성
    fun setHeart7DayCandleData() : CandleDataSet {
        val candleDataSet = makeHeart7dayCandleDataSet()
        candleDataSet.run {
            //심지 부분
            shadowColor = Color.LTGRAY
            shadowWidth = 1f

            //음봉
            decreasingColor = ContextCompat.getColor(context, R.color.graph_heart_100)
            decreasingPaintStyle = Paint.Style.FILL

            //양봉
            increasingColor = ContextCompat.getColor(context, R.color.graph_heart_100)
            increasingPaintStyle = Paint.Style.FILL

            neutralColor = Color.DKGRAY
            setDrawValues(false)

            //터치시 노란 선 제거
            highLightColor = Color.TRANSPARENT
        }
        return candleDataSet
    }

    //심박수 상세 그래프 생성
    fun setGlucose7DayCandleData() : CandleDataSet {
        val candleDataSet = makeGlucose7dayCandleDataSet()
        candleDataSet.run {
            //심지 부분
            shadowColor = Color.LTGRAY
            shadowWidth = 1f

            //음봉
            decreasingColor = ContextCompat.getColor(context, R.color.graph_glucose_100)
            decreasingPaintStyle = Paint.Style.FILL

            //양봉
            increasingColor = ContextCompat.getColor(context, R.color.graph_glucose_100)
            increasingPaintStyle = Paint.Style.FILL

            neutralColor = Color.DKGRAY
            setDrawValues(false)

            //터치시 노란 선 제거
            highLightColor = Color.TRANSPARENT
        }
        return candleDataSet
    }
}

data class CandleData(var data: Float = 0f, var open: Float, var close: Float)