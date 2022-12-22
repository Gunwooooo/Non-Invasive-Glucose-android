package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.view.View
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetConnectDeviceBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment


class UserSetConnectDeviceFragment : BaseFragment<FragmentUserSetConnectDeviceBinding>(FragmentUserSetConnectDeviceBinding::inflate), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.setBtnBackVisible(View.INVISIBLE)
        mActivity.setProgressDialogValueAndVisible(99, View.GONE)
        mActivity.setPrevFragment(UserSetAgreementFragment())

        binding.userSetConnectDeviceBtnNext.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.userSetConnectDeviceBtnNext -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragmentTransaction(ConnectionLoadingFragment())
            }
        }
    }
}