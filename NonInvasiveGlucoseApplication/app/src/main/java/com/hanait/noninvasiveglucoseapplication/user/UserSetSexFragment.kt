package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetSexBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mPrevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mProgressBar

class UserSetSexFragment : BaseFragment<FragmentUserSetSexBinding>(FragmentUserSetSexBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        mPrevFragment = UserSetBirthdayFragment()
        mProgressBar.progress = 80

        binding.userSetSexBtnNext.setOnClickListener(this)
        binding.userSetSexBtnFemale.setOnClickListener(this)
        binding.userSetSexBtnMale.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetSexBtnNext -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragment("UserSetAgreementFragment")
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
        onButton.setBackgroundResource(R.drawable.btn_border_gray)
        onButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        offButton.setBackgroundResource(R.drawable.btn_border_light_gray)
        offButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.toss_black_500))
    }

}