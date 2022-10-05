package com.hanait.noninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetBirthdayBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mPrevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mProgressBar
import java.util.*

class UserSetBirthdayFragment : BaseFragment<FragmentUserSetBirthdayBinding>(FragmentUserSetBirthdayBinding::inflate), View.OnClickListener{


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        mPrevFragment = UserSetPasswordFragment()
        mProgressBar.progress = 64

        binding.userSetBirthdayBtnNext.setOnClickListener(this)
        binding.userSetBirthdayBtnCalendar.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        when(v) {
            binding.userSetBirthdayBtnNext -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragment("UserSetSexFragment")
            }
            binding.userSetBirthdayBtnCalendar -> {
                val today = GregorianCalendar()
                val year: Int = today.get(Calendar.YEAR)
                val month: Int = today.get(Calendar.MONTH)
                val date: Int = today.get(Calendar.DATE)
                val dlg = DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        binding.userSetBirthdayTextViewYear.text = "$year 년"
                        binding.userSetBirthdayTextViewMonth.text = "${month + 1} 월"
                        binding.userSetBirthdayTextViewDay.text = "$dayOfMonth 일"
                    }
                }, year, month, date)
                dlg.show()
            }
        }
    }
}