package com.hanait.noninvasiveglucoseapplication.user

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetAuthorizationBinding
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData
import com.hanait.noninvasiveglucoseapplication.util.NaverCloudServiceManager

class UserSetAuthorizationFragment : BaseFragment<FragmentUserSetAuthorizationBinding>(FragmentUserSetAuthorizationBinding::inflate), View.OnClickListener {

    companion object {
        //결과 인증 코드
        var smsAuthCode = ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        //액션바 다시 보이게하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity.setProgressDialogValueAndVisible(28, View.VISIBLE)
        mActivity.setPrevFragment(UserSetPhoneNumberFragment())

        binding.userSetAuthorizationBtnNext.setOnClickListener(this)

        //입력받은 휴대전화 번호 넣어놓기
        binding.userSetAuthorizationEditTextPhoneNumber.hint = _userData.phoneNumber
        binding.userSetAuthorizationEditTextPhoneNumber.setOnClickListener(this)
        binding.userSetAuthorizationBtnGetAuthNum.setOnClickListener(this)

        setEditTextTextChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        val mActivity = activity as UserActivity
        when(v) {
            binding.userSetAuthorizationBtnNext -> {
                mActivity.changeFragmentTransaction(UserSetNicknameFragment())
                //인증번호가 일치할 경우, 불일치 경우
//                if(smsAuthCode == binding.userSetAuthorizationEditTextInputAuthNum.text.toString()) {
//                    Toast.makeText(requireContext(), "인증을 성공했어요.", Toast.LENGTH_SHORT).show()
//                    mActivity.changeFragment("UserSetNicknameFragment")
//                } else {
//                    Toast.makeText(requireContext(), "인증번호가 일치하지 않아요.", Toast.LENGTH_SHORT).show()
//                    binding.userSetAuthorizationEditTextInputAuthNum.setText("")
//                }
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

    //텍스트 비어있을 경우 버튼 색상 변경 이벤트
    private fun setEditTextTextChanged() {
        binding.userSetAuthorizationEditTextInputAuthNum.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userSetAuthorizationBtnNext.isEnabled = s?.length != 0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }


    //인증 번호 보내는 함수
    @RequiresApi(Build.VERSION_CODES.O)
    private fun retrofitSendSMSAuthCode() {
        val timestamp = System.currentTimeMillis().toString()
        val naverCloudServiceManager = NaverCloudServiceManager.getInstance()

        val signature = naverCloudServiceManager.makeSignature(timestamp)
        smsAuthCode = naverCloudServiceManager.makeSMSAuthCode()
        val bodyRequest = NaverCloudServiceManager.getInstance().makeBodyRequest(_userData.phoneNumber, smsAuthCode)
        RetrofitManager.instance.sendSMS(timestamp, signature, bodyRequest, completion = {
                completionResponse, s ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "UserSetAuthorizationFragment - onClick : OK!")
                    binding.userSetAuthorizationTextViewCountTime.visibility = View.VISIBLE
                    binding.userSetAuthorizationBtnGetAuthNum.setBackgroundResource(R.drawable.btn_border_gray_reverse)
                    binding.userSetAuthorizationBtnGetAuthNum.text = "재발송"

                    //타이머 3분 작동
                    naverCloudServiceManager.countDownTimer(180 * 1000, 1000, binding.userSetAuthorizationTextViewCountTime, requireContext()).start()
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "UserSetAuthorizationFragment - onClick : FAIL!")
                }
            }
        })
    }
}