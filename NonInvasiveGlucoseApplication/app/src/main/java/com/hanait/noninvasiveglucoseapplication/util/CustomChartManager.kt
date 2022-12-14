package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.hanait.noninvasiveglucoseapplication.R
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
    
    class CustomTimeXAxisFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val time = value.toInt()
//            if(0<time)
            return "${time}시"
        }
    }

    class CustomDateXAxisFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val time = value.toInt()
            return "${time}일"
        }
    }

    //실수 난수 생성
    private fun makeThermometerDashboardLineDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 24) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "체온")
    }

    //실수 난수 생성
    private fun makeHeartDashboardLineDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 24) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "심박수")
    }

    //실수 난수 생성
    private fun makeGlucoseDashboardLineDataSet() : LineDataSet {
        val entry: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 24) {
            entry.add(Entry(i.toFloat(), (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)))
        }
        return LineDataSet(entry, "혈당")
    }

    //==============================================================================================
    //캔들 데이터 생성
    private fun makeThermometer7dayCandleLineDataSet() : CandleLineDataSet {
        val entryCandle: MutableList<CandleEntry> = ArrayList()
        val entryLine: MutableList<Entry> = ArrayList()
        val max = 40.0
        val min = 35.0
        val random = Random()
        random.setSeed(Date().time)
        for (i in 1 until 50) {
            val shadowHigh = (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)
            val shadowLow = (((min + random.nextFloat() * (max - min))*10).roundToInt()/10f)
            val midValue = ((shadowHigh + shadowLow) / 2F * 10).roundToInt()/10f
            entryCandle.add(CandleEntry(i.toFloat(), shadowLow,  shadowHigh, midValue, midValue))
            entryLine.add(Entry(i.toFloat(), midValue))
        }
        return CandleLineDataSet(CandleDataSet(entryCandle, "체온"), LineDataSet(entryLine, "평균체온"))
    }

    //실수 난수 생성
    private fun makeThermometer7dayLineDataSet() : LineDataSet {
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
    private fun makeHeart7dayCandleDataSet() : CandleDataSet {
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
    private fun makeGlucose7dayCandleDataSet() : CandleDataSet {
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

    private fun makeThermometerAnalysis2BarDataSet() : BarDataSet {
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
    //심박수 라인 데이터 생성성
    fun setThermometerDashboardLineData() : LineDataSet {
        val lineDataSet = makeThermometerDashboardLineDataSet()
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.toss_black_500))
        lineDataSet.lineWidth = 2F //선 굵기
        lineDataSet.circleRadius = 7F
        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.setDrawCircles(false)   //동그란거 없애기
        lineDataSet.setDrawValues(true)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.toss_black_500))
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

    fun setHeartLineData() : LineDataSet {
        val lineDataSet = makeHeartDashboardLineDataSet()
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.text_red_200))
        lineDataSet.lineWidth = 2F //선 굵기
        lineDataSet.circleRadius = 7F
        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.setDrawCircles(false)   //동그란거 없애기
        lineDataSet.setDrawValues(true)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.text_red_200))
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

    fun setGlucoseLineData() : LineDataSet {
        val lineDataSet = makeGlucoseDashboardLineDataSet()
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        lineDataSet.setColor(ContextCompat.getColor(context, R.color.text_blue_200))
        lineDataSet.lineWidth = 2F //선 굵기
        lineDataSet.circleRadius = 7F
        lineDataSet.circleHoleRadius = 4F
//        lineDataSet.enableDashedLine(10f, 5f, 0f)
        lineDataSet.setDrawCircles(false)   //동그란거 없애기
        lineDataSet.setDrawValues(true)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.text_blue_200))
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

//==================================================================================================
    //체온 7일 상세 그래프 생성
    fun setThermometer7DayCandleLineData() : CandleLineDataSet {
        val candleLindDataSet = makeThermometer7dayCandleLineDataSet()
        val candleDataSet = candleLindDataSet.candleDataSet
        candleDataSet.run {
            //심지 부분
            shadowColor = ContextCompat.getColor(context, R.color.toss_black_150)
            shadowWidth = 2f

            //음봉
            decreasingColor = ContextCompat.getColor(context, R.color.transparent)
            decreasingPaintStyle = Paint.Style.STROKE

            //양봉
            increasingColor = ContextCompat.getColor(context, R.color.transparent)
            increasingPaintStyle = Paint.Style.STROKE

            neutralColor = ContextCompat.getColor(context, R.color.  transparent)


            setDrawValues(false)
            //터치시 노란 선 제거
            highLightColor = Color.TRANSPARENT
        }
        val lineDataSet = candleLindDataSet.lineDataSet
        lineDataSet.run {
            mode = LineDataSet.Mode.LINEAR
//            .cubicIntensity = 0.2F //베지어 곡선 휘는 정도
//            .setDrawFilled(true)
//            .fillDrawable = ContextCompat.getDrawable(context, R.color.graph_blue_100)
            setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
            setColor(ContextCompat.getColor(context, R.color.toss_black_500))
            lineWidth = 2F //선 굵기
            circleRadius = 3F
            circleHoleRadius = 1F
//          enableDashedLine(10f, 5f, 0f)
            setDrawCircles(true)   //동그란거 없애기
            setDrawValues(false)
            setCircleColor(ContextCompat.getColor(context, R.color.toss_black_500))
            valueTextSize = 0F
//            fillAlpha = 50
            isHighlightEnabled = false   //클릭시 마크 보이게
            setDrawHorizontalHighlightIndicator(false)  //가로 하이라이트 줄 없애기
            setDrawVerticalHighlightIndicator(false) //세로 하이라이트 줄 없애기
            setDrawCircleHole(true)
//        barDataSet.setDrawCircleHole(true)
        }
        return candleLindDataSet
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //analysis2 bar chart 데이터셋 생성
    //심박수 막대 데이터 생성성
    fun setThermometerAbnormalData() : BarDataSet {
        val barDataSet = makeThermometerAnalysis2BarDataSet()
//        barDataSet.mode = LineDataSet.Mode.LINEAR
//        barDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        barDataSet.setColor(ContextCompat.getColor(context, R.color.text_red_200))
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

    //체온 라인데이터 생성
    fun setHeartAnalysis2BarData() : BarDataSet {
        val barDataSet = makeHeartAnalysis2BarDataSet()
//        barDataSet.mode = LineDataSet.Mode.LINEAR
//        barDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        barDataSet.setColor(ContextCompat.getColor(context, R.color.graph_heart_100))
//        barDataSet.lineWidth = 3F //선 굵기
//        barDataSet.circleRadius = 7F
//        barDataSet.circleHoleRadius = 4F
//        barDataSet.setDrawCircles(true)   //동그란거 없애기
        barDataSet.setDrawValues(true)
//        barDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_heart_100))
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


    //체온 라인데이터 생성
    fun setGlucoseAnalysis2BarData() : BarDataSet {
        val barDataSet = makeGlucoseAnalysis2BarDataSet()
//        barDataSet.mode = LineDataSet.Mode.LINEAR
//        barDataSet.setDrawHorizontalHighlightIndicator(false)  //클릭 시 선 보이게 하기
        barDataSet.setColor(ContextCompat.getColor(context, R.color.graph_glucose_100))
//        barDataSet.lineWidth = 3F //선 굵기
//        barDataSet.circleRadius = 7F
//        barDataSet.circleHoleRadius = 4F
//        barDataSet.setDrawCircles(true)   //동그란거 없애기
        barDataSet.setDrawValues(true)
//        barDataSet.setCircleColor(ContextCompat.getColor(context, R.color.graph_glucose_100))
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

data class CandleLineDataSet(var candleDataSet: CandleDataSet, var lineDataSet: LineDataSet)