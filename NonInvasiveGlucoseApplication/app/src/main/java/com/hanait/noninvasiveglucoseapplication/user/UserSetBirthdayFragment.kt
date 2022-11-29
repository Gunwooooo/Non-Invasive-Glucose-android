package com.hanait.noninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetBirthdayBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants
import com.hanait.noninvasiveglucoseapplication.util.Constants._prevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._progressBar
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData
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
        _prevFragment = UserSetPasswordFragment()
        _progressBar.progress = 70

        binding.userSetBirthdayBtnNext.setOnClickListener(this)
        binding.userSetBirthdayBtnCalendar.setOnClickListener(this)
    }

    //날짜 선택 다이어로그 만들기
    @SuppressLint("SetTextI18n")
    private fun makeDatePickDialog() {
        val today = GregorianCalendar()
        val todayYear: Int = today.get(Calendar.YEAR)
        val todayMonth: Int = today.get(Calendar.MONTH)
        val todayDate: Int = today.get(Calendar.DATE)

        val dateSetListener : DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.userSetBirthdayTextViewYear.text = "$year 년"
                binding.userSetBirthdayTextViewMonth.text = "${month + 1} 월"
                binding.userSetBirthdayTextViewDay.text = "$dayOfMonth 일"
                binding.userSetBirthdayBtnNext.isEnabled = true

                selectedYear = year.toString()
                selectedMonth = (month + 1).toString()
                selectedDayOfMonth = dayOfMonth.toString()
            }
        val datePickerDialog = DatePickerDialog(requireContext(), dateSetListener,  todayYear, todayMonth, todayDate)
        datePickerDialog.show()
    }



    override fun onClick(v: View?) {
        when(v) {
            binding.userSetBirthdayBtnNext -> {
                _userData.birthDay = "$selectedYear-$selectedMonth-$selectedDayOfMonth"

                val mActivity = activity as UserActivity
                mActivity.changeFragment("UserSetSexFragment")
            }
            binding.userSetBirthdayBtnCalendar -> {
                makeDatePickDialog()
            }
        }
    }
}