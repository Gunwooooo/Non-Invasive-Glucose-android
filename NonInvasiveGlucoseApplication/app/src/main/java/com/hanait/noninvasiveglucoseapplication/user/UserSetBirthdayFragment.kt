package com.hanait.noninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetBirthdayBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData
import com.hanait.noninvasiveglucoseapplication.util.CustomCalendarManager
import java.util.*

class UserSetBirthdayFragment : BaseFragment<FragmentUserSetBirthdayBinding>(FragmentUserSetBirthdayBinding::inflate), View.OnClickListener{

    private var selectedYear = ""
    private var selectedMonth = ""
    private var selectedDayOfMonth = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }


    private fun init() {
        val mActivity = activity as UserActivity
        mActivity.setProgressDialogValueAndVisible(70, View.VISIBLE)
        mActivity.setPrevFragment(UserSetPasswordFragment())

        //날짜 선택 리스너 설정
        setDataPickerDialogListener()

        binding.userSetBirthdayBtnNext.setOnClickListener(this)
        binding.userSetBirthdayBtnCalendar.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v) {
            binding.userSetBirthdayBtnNext -> {
                _userData.birthDay = "${selectedYear}년 ${selectedMonth}월 ${selectedDayOfMonth}일"

                val mActivity = activity as UserActivity
                mActivity.changeFragmentTransaction(UserSetSexFragment())
            }
            binding.userSetBirthdayBtnCalendar -> {
                CustomCalendarManager(requireContext()).makeDatePickerDialog(setDataPickerDialogListener())
            }
        }
    }

    //날짜 선택 리스너 설정
    @SuppressLint("SetTextI18n")
    private fun setDataPickerDialogListener() : DatePickerDialog.OnDateSetListener {
        val datePickerDialogListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            binding.userSetBirthdayTextViewYear.text = "$year 년"
            binding.userSetBirthdayTextViewMonth.text = "${month + 1} 월"
            binding.userSetBirthdayTextViewDay.text = "$dayOfMonth 일"
            binding.userSetBirthdayBtnNext.isEnabled = true
            selectedYear = year.toString()
            selectedMonth = (month + 1).toString()
            selectedDayOfMonth = dayOfMonth.toString()
        }
        return datePickerDialogListener
    }
}