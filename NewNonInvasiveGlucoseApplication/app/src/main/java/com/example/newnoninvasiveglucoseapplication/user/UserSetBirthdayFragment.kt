package com.example.newnoninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentUserSetBirthdayBinding
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import com.example.newnoninvasiveglucoseapplication.util.Constants._userData
import com.example.newnoninvasiveglucoseapplication.util.CustomDatePickerDialogManager
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


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when(v) {
            binding.userSetBirthdayBtnNext -> {
                _userData.birthDay = "${selectedYear}년 ${selectedMonth}월 ${selectedDayOfMonth}일"

                val mActivity = activity as UserActivity
                mActivity.changeFragmentTransaction(UserSetSexFragment())
            }
            binding.userSetBirthdayBtnCalendar -> {
                CustomDatePickerDialogManager(requireContext()).makeDatePickerDialog(setDataPickerDialogListener()).show()
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