package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.hanait.noninvasiveglucoseapplication.R


@SuppressLint("ViewConstructor")
class CustomMarkerViewManager(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val customMarkerViewTextView: TextView = findViewById<View>(R.id.customMarkerView_textView) as TextView
    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @SuppressLint("SetTextI18n")
    override fun refreshContent(e: Entry, highlight: Highlight) {
        if (e is CandleEntry) {
            val text = StringBuilder()
            text.append("10월 ${e.x.toInt()}일\n")
            text.append("평균 체온 ${String.format("%.1f", e.y)}")
            customMarkerViewTextView.text = text
        } else {
            val text = StringBuilder()
            text.append("x : ${e.x}\n")
            text.append("y : ${e.y}")
            customMarkerViewTextView.text = text
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}