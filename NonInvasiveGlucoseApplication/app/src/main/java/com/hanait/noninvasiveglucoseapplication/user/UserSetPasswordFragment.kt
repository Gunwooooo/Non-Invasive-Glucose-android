package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.view.View
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetPasswordBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mPrevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mProgressBar


class UserSetPasswordFragment : BaseFragment<FragmentUserSetPasswordBinding>(FragmentUserSetPasswordBinding::inflate), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        mPrevFragment = UserSetAuthorizationFragment()
        mProgressBar.progress = 48

        binding.userSetPasswordBtnNext.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetPasswordBtnNext -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragment("UserSetBirthdayFragment")
            }
        }
    }
}