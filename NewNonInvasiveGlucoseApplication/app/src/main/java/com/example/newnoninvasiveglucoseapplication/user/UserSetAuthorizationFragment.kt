package com.example.newnoninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentUserSetAuthorizationBinding
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import com.example.newnoninvasiveglucoseapplication.util.Constants._userData
import com.example.newnoninvasiveglucoseapplication.util.CustomDialogManager
import com.example.newnoninvasiveglucoseapplication.util.NaverCloudServiceManager
import com.jakewharton.rxbinding4.widget.textChanges

class UserSetAuthorizationFragment : BaseFragment<FragmentUserSetAuthorizationBinding>(FragmentUserSetAuthorizationBinding::inflate), View.OnClickListener {
    private val customProgressDialog by lazy { CustomDialogManager(requireContext(), R.layout.common_progress_dialog, null, null) }
    private var countDownTimer: CountDownTimer? = null

    companion object {
        //결과 인증 코드
        var smsAuthCode = ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    @SuppressLint("CheckResult")
    private fun init() {
        //액션바 다시 보이게하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.setBtnBackVisible(View.VISIBLE)
        mActivity.setProgressDialogValueAndVisible(28, View.VISIBLE)
        mActivity.setPrevFragment(UserSetPhoneNumberFragment())

        //입력받은 휴대전화 번호 넣어놓기
        binding.userSetAuthorizationEditTextPhoneNumber.hint = _userData.phoneNumber

        binding.userSetAuthorizationEditTextPhoneNumber.setOnClickListener(this)
        binding.userSetAuthorizationBtnGetAuthNum.setOnClickListener(this)
        binding.userSetAuthorizationBtnNext.setOnClickListener(this)

        //에딧 텍스트 subscribe
        binding.userSetAuthorizationEditTextInputAuthNum.textChanges().subscribe {
            binding.userSetAuthorizationBtnNext.isEnabled = it.isNotEmpty()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        val mActivity = activity as UserActivity
        when(v) {
            binding.userSetAuthorizationBtnNext -> {
                mActivity.changeFragmentTransaction(UserSetNicknameFragment())
//                인증번호가 일치할 경우, 불일치 경우
                if(smsAuthCode == binding.userSetAuthorizationEditTextInputAuthNum.text.toString()) {
                    countDownTimer?.cancel()
                    Toast.makeText(requireContext(), "인증을 성공했어요.", Toast.LENGTH_SHORT).show()
                    mActivity.changeFragmentTransaction(UserSetNicknameFragment())
                } else {
                    Toast.makeText(requireContext(), "인증번호가 일치하지 않아요.", Toast.LENGTH_SHORT).show()
                    binding.userSetAuthorizationEditTextInputAuthNum.setText("")
                }
            }

            binding.userSetAuthorizationEditTextPhoneNumber -> {
                mActivity.changePrevFragment()
            }
            binding.userSetAuthorizationBtnGetAuthNum -> {
                //인증 번호 생성 후 retrofit으로 사용자에게 문자 전송
                retrofitSendSMSAuthCode()

            }
        }
    }
    
    //인증 번호 보내는 함수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun retrofitSendSMSAuthCode() {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(childFragmentManager, "common_progress_dialog")
        val timestamp = System.currentTimeMillis().toString()
        val naverCloudServiceManager = NaverCloudServiceManager.getInstance()
        val signature = naverCloudServiceManager.makeSignature(timestamp)
        smsAuthCode = naverCloudServiceManager.makeSMSAuthCode()
        val bodyRequest = NaverCloudServiceManager.getInstance().makeBodyRequest(_userData.phoneNumber, smsAuthCode)
        RetrofitManager.instance.sendSMS(timestamp, signature, bodyRequest, completion = {
                completionResponse, s ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "UserSetAuthorizationFragment - onClick : OK!")
                    binding.userSetAuthorizationTextViewCountTime.visibility = View.VISIBLE
                    binding.userSetAuthorizationBtnGetAuthNum.text = "재발송"

                    //타이머 3분 작동
                    if(countDownTimer != null) {
                        countDownTimer!!.cancel()
                    }
                    countDownTimer = naverCloudServiceManager.countDownTimer(180 * 1000, 1000, binding.userSetAuthorizationTextViewCountTime, requireContext()).start()
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "UserSetAuthorizationFragment - onClick : 통신 실패")
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //카운트 타이머 초기화
        countDownTimer?.cancel()
    }
}