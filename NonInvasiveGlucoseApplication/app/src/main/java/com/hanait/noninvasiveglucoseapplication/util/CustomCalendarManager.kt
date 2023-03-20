package com.hanait.noninvasiveglucoseapplication.util

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources.getSystem
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class CustomDatePickerDialogManager(context: Context, private val highlightedDate : LocalDateTime?) : DatePickerDialog(context) {
    private var mContext: Context = context
    //생년월일 변경 달력 날짜 선택 다이어로그 생성
    fun makeDatePickerDialog(datePickerDialogListener : OnDateSetListener) : DatePickerDialog {
        val gregorianCalendar = GregorianCalendar()
        val year = gregorianCalendar.get(Calendar.YEAR)
        val month = gregorianCalendar.get(Calendar.MONTH)
        val dayOfMonth = gregorianCalendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(mContext, datePickerDialogListener, year, month, dayOfMonth)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(highlightedDate == null) return
        val dayOfMonth = highlightedDate.dayOfMonth
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            datePicker.findViewById<DatePicker>(getSystem().getIdentifier("day", "id", "android"))
                .findViewById<NumberPicker>(getSystem().getIdentifier("numberpicker_input", "id", "android")).textColor = ContextCompat.getColor(
                context, R.color.text_blue_200)
        }

    }


    // Helper function to get the day of the month from a date
    private fun getDayOfMonth(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

}