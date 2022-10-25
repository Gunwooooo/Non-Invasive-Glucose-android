package com.hanait.noninvasiveglucoseapplication.util

import android.R
import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils


class MyMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val tvContent: TextView

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    fun refreshContent(e: Map.Entry<*, *>, highlight: Highlight?) {
        if (e is CandleEntry) {
            val ce = e as CandleEntry
            tvContent.text =
                "" + Utils.formatNumber(ce.high.toInt().toFloat(), 0, true) + "시간"
        } else {
            tvContent.text = "" + Utils.formatNumber(e.getY() as Int.toFloat(), 0, true) + "시간"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

    init {
        tvContent = findViewById<View>(R.id.tvContentHead) as TextView
    }
}