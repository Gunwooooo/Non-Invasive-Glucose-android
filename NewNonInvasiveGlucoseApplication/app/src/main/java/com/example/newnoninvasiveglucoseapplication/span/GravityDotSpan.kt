package com.example.newnoninvasiveglucoseapplication.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan

class GravityDotSpan(radius : Float, color: Int, padding : Float, gravity : Int) : LineBackgroundSpan{
    private var radius = 0f
    private var color = 0
    private var padding = 0f
    private var gravity = 0

    init {
        this.radius = radius
        this.color = color
        this.padding = padding
        this.gravity = gravity
    }

    override fun drawBackground(
        canvas: Canvas, paint: Paint, left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
        charSequence: CharSequence, start: Int, end: Int, lineNum: Int) {

        val oldColor = paint.color
        if (color != 0) {
            paint.color = color
        }

//        //붉은색 설정
//        paint.color = color

        //오른쪽 위에 표시점 위치시키기
        canvas.drawCircle(right - radius - padding, top.toFloat(), radius, paint)

        paint.color = oldColor
    }
}