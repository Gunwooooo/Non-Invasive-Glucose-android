package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.view.View
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetConnectDeviceBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mPrevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mProgressBar


class UserSetConnectDeviceFragment : BaseFragment<FragmentUserSetConnectDeviceBinding>(FragmentUserSetConnectDeviceBinding::inflate), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        mPrevFragment = UserSetAgreementFragment()
        mProgressBar.visibility = View.GONE

        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.userSetConnectDeviceBtnNext.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.userSetConnectDeviceBtnNext -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragment("ConnectionLoadingFragment")
            }
        }
    }
}