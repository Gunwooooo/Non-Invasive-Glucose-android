package com.example.newnoninvasiveglucoseapplication.user

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentUserSetAgreementBinding
import com.example.newnoninvasiveglucoseapplication.model.UserData
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import com.example.newnoninvasiveglucoseapplication.util.Constants._prefs
import com.example.newnoninvasiveglucoseapplication.util.Constants._userData
import com.example.newnoninvasiveglucoseapplication.util.CustomDialogManager
import com.example.newnoninvasiveglucoseapplication.util.LoginedUserClient


class UserSetAgreementFragment : BaseFragment<FragmentUserSetAgreementBinding>(FragmentUserSetAgreementBinding::inflate), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private val customProgressDialog by lazy { CustomDialogManager(requireContext(), R.layout.common_progress_dialog, null, null) }

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
            //checkBox 체크 후 버튼 확성화
            binding.userSetAgreementCheckBoxAgreement1, binding.userSetAgreementCheckBoxAgreement2 -> {
                binding.userSetAgreementBtnNext.isEnabled = binding.userSetAgreementCheckBoxAgreement1.isChecked && binding.userSetAgreementCheckBoxAgreement2.isChecked
            }
        }
    }
    
    //회원가입 레트로핏 통신
    private fun retrofitJoinUser() {
        RetrofitManager.instance.joinUser(_userData, completion = { completionResponse, s ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "UserSetAgreementFragment - retrofitJoinUser : 통신 성공 $s")
                    //회원가입 성공시 자동 로그인
                    retrofitLoginUser()
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "UserSetAgreementFragment - retrofitJoinUser : 통신 에러")
                }
            }
        })
    }

    //로그인 레트로핏 통싱
    private fun retrofitLoginUser() {
        customProgressDialog.show(childFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.loginUser(_userData, completion = { completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        //로그인 성공
                        200 -> {
                            //토큰 값 저장
                            LoginedUserClient.authorization = response.headers()["Authorization"]
                            LoginedUserClient.refreshToken = response.headers()["refresh_token"]

                            //자동 로그인 설정
                            _prefs.setBoolean("AUTO_LOGIN", true)
                            _prefs.setString("USER_PHONENUMBER", _userData.phoneNumber)
                            _prefs.setString("USER_PASSWORD", _userData.password)

                            //전역 유저 데이터 초기화
                            _userData = UserData("", "", "", "", "T")

                            val mActivity = activity as UserActivity
                            mActivity.changeFragmentTransaction(UserSetConnectDeviceFragment())
                        }
                        else -> {
                            Toast.makeText(requireContext(), "통신 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "UserCheckPasswordFragment - retrofitLoginUser : 로그인 통신 실패")
                }
            }
        })
    }
}