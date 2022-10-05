package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.util.Log
import android.view.View
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetAuthorizationBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mPrevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mProgressBar
import com.hanait.noninvasiveglucoseapplication.util.Constants.mUserData

class UserSetAuthorizationFragment : BaseFragment<FragmentUserSetAuthorizationBinding>(FragmentUserSetAuthorizationBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        mPrevFragment = UserSetPhoneNumberFragment()
        mProgressBar.progress = 32

        //액션바 다시 보이게하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.userSetAuthorizationBtnNext.setOnClickListener(this)

        //입력받은 휴대전화 번호 넣어놓기
        binding.userSetAuthorizationEditTextPhoneNumber.hint = mUserData.phoneNumber
        binding.userSetAuthorizationEditTextPhoneNumber.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val mActivity = activity as UserActivity
        when(v) {
            binding.userSetAuthorizationBtnNext -> {
                mActivity.changeFragment("UserSetPasswordFragment")
            }

            binding.userSetAuthorizationEditTextPhoneNumber -> {
                mActivity.changePrevFragment()
            }
        }
    }
}