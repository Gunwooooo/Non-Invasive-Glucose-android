package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetSexBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData

class UserSetSexFragment : BaseFragment<FragmentUserSetSexBinding>(FragmentUserSetSexBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val mActivity = activity as UserActivity
        mActivity.setProgressDialogValueAndVisible(84, View.VISIBLE)
        mActivity.setPrevFragment(UserSetBirthdayFragment())

        binding.userSetSexBtnNext.setOnClickListener(this)
        binding.userSetSexBtnFemale.setOnClickListener(this)
        binding.userSetSexBtnMale.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetSexBtnNext -> {
                var tmpSex = "F"
                if(!binding.userSetSexBtnMale.isEnabled)
                    tmpSex = "T"
                _userData.sex = tmpSex

                val mActivity = activity as UserActivity
                mActivity.changeFragmentTransaction(UserSetAgreementFragment())
            }
            binding.userSetSexBtnFemale -> {
                changeButtonColor(binding.userSetSexBtnFemale, binding.userSetSexBtnMale)
            }
            binding.userSetSexBtnMale -> {
                changeButtonColor(binding.userSetSexBtnMale, binding.userSetSexBtnFemale)
            }
        }
    }

    //버튼 토글 기능
    private fun changeButtonColor(onButton: Button, offButton: Button) {
        binding.userSetSexBtnNext.isEnabled = true
        binding.userSetSexBtnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.iphone_green_200))
        onButton.isEnabled = false
        onButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        offButton.isEnabled = true
        offButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.toss_black_500))
    }
}