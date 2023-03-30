package com.example.newnoninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.newnoninvasiveglucoseapplication.R
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt


class CustomChartManager(val context: Context) {

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
    
    //시간:분으로 X축 설정
    @SuppressLint("SimpleDateFormat")
    class CustomTimeXAxisFormatter : ValueFormatter() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getFormattedValue(value: Float): String {
            val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
            return LocalTime.ofSecondOfDay(value.toLong()).format(timeFormat)
        }
    }

//    //3시간씩 X축 고정값 설정
//    class CustomFixedTimeXAxisFormatter : ValueFormatter() {
//        val xValues = listOf(7200f, 14440f, 21600f, 28800f, 36000f, 43200f, 50400f, 57600f, 64800f, 72000f, 79200f, 86400f)
//        override fun getFormattedValue(value: Float): String {
//            return xValues.format(value.toDouble()) // e.g. append a dollar-sign
//        }
//    }

    //소수점 첫째자리까지 Y값 설정
    class CustomDecimalYAxisFormatter : ValueFormatter() {
        private val mFormat: DecimalFormat = DecimalFormat("###,###,##0.0")
        override fun getFormattedValue(value: Float): String {
            return mFormat.format(value.toDouble()) // e.g. append a dollar-sign
        }
    }

    //==============================================================================================
    //캔들라인 데이터 생성
//    public fun makeThermometer7dayCandleLineDataSet() : CandleLineDataSet {
//        val entryCandle: MutableList<CandleEntry> = ArrayList()
//        val entryLine: MutableList<Entry> = ArrayList()
//        val max = 40.0
//        val min = 35.0
//        val random = Random()
//        random.setSeed(Date().time)
//        for (i in 0 until 8) {
//            val shadowHigh = (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)
//            val shadowLow = (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)
//            val midValue = ((shadowHigh + shadowLow) / 2F * 10).roundToInt()/10f
//            entryCandle.add(CandleEntry((i*3*3600).toFloat(), shadowLow,  shadowHigh, midValue, midValue))
//            entryLine.add(Entry((i*3*3600).toFloat(), midValue))
//        }
//        return CandleLineDataSet(CandleDataSet(entryCandle, "범위"), LineDataSet(entryLine, "평균"))
//    }

    //실수 난수 생성
    fun makeThermometer7dayLineDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 50) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "평균체온")
    }


    //심박수 캔들 데이터 생성
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

    //혈당 캔들 데이터 생성
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
    //================================================================================================
    //분석2 BarChart 데이터 생성
    fun makeThermometerAnalysis2BarDataSet() : BarDataSet {
        val entry: MutableList<BarEntry> = ArrayList()
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 9) {
            entry.add(BarEntry(i.toFloat(), random.nextInt(18).toFloat()))
        }
        return BarDataSet(entry, "체온")
    }

    //실수 난수 생성
    private fun makeHeartAnalysis2BarDataSet() : BarDataSet {
        val entry: MutableList<BarEntry> = ArrayList()
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 9) {
            entry.add(BarEntry(i.toFloat(), random.nextInt(20).toFloat()))
        }
        return BarDataSet(entry, "심박수")
    }

    //실수 난수 생성
    private fun makeGlucoseAnalysis2BarDataSet() : BarDataSet {
        val entry: MutableList<BarEntry> = ArrayList()
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 9) {
            entry.add(BarEntry(i.toFloat(), random.nextInt(20).toFloat()))
        }
        return BarDataSet(entry, "혈당")
    }

    //=================================================================================================

    //심박수 7일 그래프 생성
    fun setHeart7DayCandleData() : CandleDataSet {
        val candleDataSet = makeHeart7dayCandleDataSet()
        return candleDataSet.apply {
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
    }

    //심박수 상세 그래프 생성
    fun setGlucose7DayCandleData() : CandleDataSet {
        val candleDataSet = makeGlucose7dayCandleDataSet()
        return candleDataSet.apply {
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
    }

    //==================================================================================================


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //analysis2 bar chart 데이터셋 생성
    //심박수 막대 데이터 생성성


    //심박수 막대 데이터 생성성
    fun setThermometerNormalData() : BarDataSet {
        val barDataSet = makeThermometerAnalysis2BarDataSet()
//        barDataSet.mode = LineDataSet.Mode.LINEAR
//        barDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        barDataSet.setColor(ContextCompat.getColor(context, R.color.text_blue_200))
//        barDataSet.lineWidth = 3F //선 굵기
//        barDataSet.circleRadius = 7F
//        barDataSet.circleHoleRadius = 4F
//        barDataSet.setDrawCircles(true)   //동그란거 없애기
        barDataSet.valueTextColor = ContextCompat.getColor(context, R.color.toss_black_500)
        barDataSet.setDrawValues(true)
//        barDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_thermometer_100))
        barDataSet.valueTextSize = 12F
//        lineDataSet.fillAlpha = 50
//        lineDataSet.highLightColor = Color.BLACK
//        lineDataSet.highlightLineWidth = 2F
        barDataSet.isHighlightEnabled = true   //클릭시 마크 보이게
//        barDataSet.setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
//        barDataSet.setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
//        barDataSet.setDrawCircleHole(true)
        return barDataSet
    }
}