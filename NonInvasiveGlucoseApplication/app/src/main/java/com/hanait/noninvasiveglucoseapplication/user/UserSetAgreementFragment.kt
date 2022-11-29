package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetAgreementBinding
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._prevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._progressBar
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData


class UserSetAgreementFragment : BaseFragment<FragmentUserSetAgreementBinding>(FragmentUserSetAgreementBinding::inflate), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }


    private fun init() {
        _prevFragment = UserSetSexFragment()
        _progressBar.visibility = View.VISIBLE
        _progressBar.progress = 98


        binding.userSetAgreementTextViewAgreement1.setOnClickListener(this)
        binding.userSetAgreementTextViewAgreement2.setOnClickListener(this)
        binding.userSetAgreementBtnAgreeAll.setOnClickListener(this)
        binding.userSetAgreementBtnNext.setOnClickListener(this)

        binding.userSetAgreementCheckBoxAgreement1.setOnCheckedChangeListener(this)
        binding.userSetAgreementCheckBoxAgreement2.setOnCheckedChangeListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetAgreementTextViewAgreement1 -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragment("Agreement1Fragment")
            }
            binding.userSetAgreementTextViewAgreement2 -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragment("Agreement2Fragment")
            }
            binding.userSetAgreementBtnAgreeAll -> {
                binding.userSetAgreementCheckBoxAgreement1.isChecked = true
                binding.userSetAgreementCheckBoxAgreement2.isChecked = true
                binding.userSetAgreementBtnNext.isEnabled = true
            }
            binding.userSetAgreementBtnNext -> {
                retrofitJoinUser()

//                retrofitFindAllUser()

                val mActivity = activity as UserActivity
                mActivity.changeFragment("UserSetConnectDeviceFragment")
            }
        }
    }

    override fun onCheckedChanged(v: CompoundButton?, isChecked: Boolean) {
        when(v) {
            //checkBox 체크 후 버튼 확성화
            binding.userSetAgreementCheckBoxAgreement1, binding.userSetAgreementCheckBoxAgreement2 -> {
                binding.userSetAgreementBtnNext.isEnabled = binding.userSetAgreementCheckBoxAgreement1.isChecked && binding.userSetAgreementCheckBoxAgreement2.isChecked
            }
        }
    }
    
    private fun retrofitJoinUser() {
        RetrofitManager.instance.joinUser(_userData, completion = {
            completionResponse, s -> 
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "UserSetAgreementFragment - retrofitJoinUser : 통신 성공 $s")
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "UserSetAgreementFragment - retrofitJoinUser : 통신 에러")
                }
            }
        })
    }
}