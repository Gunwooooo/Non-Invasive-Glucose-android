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
            val timeFormat = DateTimeFormatter.ofPattern("H:mm")
            return LocalTime.ofSecondOfDay(value.toLong()).format(timeFormat)
        }
    }

    //세 시간 간격으로 0~3 3~6 설정
    @SuppressLint("SimpleDateFormat")
    class CustomBarChartXAxisFormatter : ValueFormatter() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getFormattedValue(value: Float): String {
            val start = value.toInt() * 3
            val end = value.toInt() * 3 + 3
            return "$start-$end"
        }
    }

    //소수점 첫째자리까지 Y값 설정
    class CustomDecimalYAxisFormatter : ValueFormatter() {
        private val mFormat: DecimalFormat = DecimalFormat("###,###,##0.0")
        override fun getFormattedValue(value: Float): String {
            return mFormat.format(value.toDouble()) // e.g. append a dollar-sign
        }
    }

    //정수로 Y값 설정
    class CustomIntegerYAxisFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString() // e.g. append a dollar-sign
        }
    }
}

data class CandleScatterDataSet(var candleDataSet: CandleDataSet, var scatterDataSet: ScatterDataSet)