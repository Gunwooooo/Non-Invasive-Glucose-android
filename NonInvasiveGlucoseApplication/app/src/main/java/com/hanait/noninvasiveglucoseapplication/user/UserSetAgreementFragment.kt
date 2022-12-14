package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetAgreementBinding
import com.hanait.noninvasiveglucoseapplication.model.UserData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient


class UserSetAgreementFragment : BaseFragment<FragmentUserSetAgreementBinding>(FragmentUserSetAgreementBinding::inflate), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        val mActivity = activity as UserActivity
        mActivity.setProgressDialogValueAndVisible(98, View.VISIBLE)
        mActivity.setPrevFragment(UserSetSexFragment())


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
                mActivity.changeFragmentTransaction(Agreement1Fragment())
            }
            binding.userSetAgreementTextViewAgreement2 -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragmentTransaction(Agreement2Fragment())
            }
            binding.userSetAgreementBtnAgreeAll -> {
                binding.userSetAgreementCheckBoxAgreement1.isChecked = true
                binding.userSetAgreementCheckBoxAgreement2.isChecked = true
                binding.userSetAgreementBtnNext.isEnabled = true
            }
            binding.userSetAgreementBtnNext -> {
                retrofitJoinUser()
            }
        }
    }

    override fun onCheckedChanged(v: CompoundButton?, isChecked: Boolean) {
        when(v) {
            //checkBox ?????? ??? ?????? ?????????
            binding.userSetAgreementCheckBoxAgreement1, binding.userSetAgreementCheckBoxAgreement2 -> {
                binding.userSetAgreementBtnNext.isEnabled = binding.userSetAgreementCheckBoxAgreement1.isChecked && binding.userSetAgreementCheckBoxAgreement2.isChecked
            }
        }
    }
    
    //???????????? ???????????? ??????
    private fun retrofitJoinUser() {
        RetrofitManager.instance.joinUser(_userData, completion = { completionResponse, s ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("??????", "UserSetAgreementFragment - retrofitJoinUser : ?????? ?????? $s")
                    //???????????? ????????? ?????? ?????????
                    retrofitLoginUser()
                }
                CompletionResponse.FAIL -> {
                    Log.d("??????", "UserSetAgreementFragment - retrofitJoinUser : ?????? ??????")
                }
            }
        })
    }

    //????????? ???????????? ??????
    private fun retrofitLoginUser() {
        RetrofitManager.instance.loginUser(_userData, completion = { completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        //????????? ??????
                        200 -> {
                            //?????? ??? ??????
                            LoginedUserClient.authorization = response.headers()["Authorization"]

                            //?????? ?????? ????????? ?????????
                            _userData = UserData("", "", "", "", "T")

                            val mActivity = activity as UserActivity
                            mActivity.changeFragmentTransaction(UserSetConnectDeviceFragment())
                        }
                        else -> {
                            Toast.makeText(requireContext(), "?????? ??????", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("??????", "UserCheckPasswordFragment - retrofitLoginUser : ????????? ?????? ??????")
                }
            }
        })
    }
}