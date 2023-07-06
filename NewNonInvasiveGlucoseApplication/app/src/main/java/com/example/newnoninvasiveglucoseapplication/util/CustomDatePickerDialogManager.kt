package com.example.newnoninvasiveglucoseapplication.util

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

class CustomDatePickerDialogManager(context: Context) {
    private var mContext: Context = context
    //생년월일 변경 달력 날짜 선택 다이어로그 생성
    fun makeDatePickerDialog(datePickerDialogListener : DatePickerDialog.OnDateSetListener) : DatePickerDialog {
        val gregorianCalendar = GregorianCalendar()
        val year = gregorianCalendar.get(Calendar.YEAR)
        val month = gregorianCalendar.get(Calendar.MONTH)
        val dayOfMonth = gregorianCalendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(mContext, datePickerDialogListener, year, month, dayOfMonth)
    }
}