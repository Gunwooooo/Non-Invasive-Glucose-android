package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.location.GnssAntennaInfo
import java.util.*

class CustomCalendarManager(val context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var INSTANCE: CustomCalendarManager? = null
        fun getInstance(context: Context): CustomCalendarManager {
            if (INSTANCE == null) {
                INSTANCE = CustomCalendarManager(context)
            }
            return INSTANCE as CustomCalendarManager
        }
    }

    //생년월일 변경 달력 날짜 선택 다이어로그 생성
    fun makeDatePickerDialog(datePickerDialogListener : DatePickerDialog.OnDateSetListener) : DatePickerDialog {
        val gregorianCalendar = GregorianCalendar()
        val year = gregorianCalendar.get(Calendar.YEAR)
        val month = gregorianCalendar.get(Calendar.MONTH)
        val dayOfMonth = gregorianCalendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(context, datePickerDialogListener, year, month, dayOfMonth)
    }
}