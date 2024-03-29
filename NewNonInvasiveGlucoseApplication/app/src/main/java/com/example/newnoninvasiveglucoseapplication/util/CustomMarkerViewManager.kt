package com.example.newnoninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.newnoninvasiveglucoseapplication.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@SuppressLint("ViewConstructor")
class CustomMarkerViewManager(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val customMarkerViewTextView: TextView = findViewById<View>(R.id.customMarkerView_textView) as TextView
    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun refreshContent(e: Entry, highlight: Highlight) {
        if (e is CandleEntry) {
            val text = StringBuilder()
            text.append("10월 ${e.x.toInt()}일\n")
            text.append("평균 체온 ${String.format("%.1f", e.y)}")
            customMarkerViewTextView.text = text
        } else {
            val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
            val x = LocalTime.ofSecondOfDay(e.x.toLong()).format(timeFormat)
            val text = StringBuilder()
            text.append("x : ${x}\n")
            text.append("y : ${e.y}")
            customMarkerViewTextView.text = text
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}