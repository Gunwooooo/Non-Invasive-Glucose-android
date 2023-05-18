package com.example.newnoninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentUserModifyForgottenPasswordBinding
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import com.example.newnoninvasiveglucoseapplication.util.Constants._userData
import com.example.newnoninvasiveglucoseapplication.util.CustomDialogManager
import com.example.newnoninvasiveglucoseapplication.util.LoginedUserClient
import com.jakewharton.rxbinding4.widget.textChanges
import org.json.JSONObject
import java.util.regex.Pattern

class UserModifyForgottenPasswordFragment : BaseFragment<FragmentUserModifyForgottenPasswordBinding>(
    FragmentUserModifyForgottenPasswordBinding::inflate), View.OnClickListener {
    private val customProgressDialog by lazy { CustomDialogManager(requireContext(), R.layout.common_progress_dialog, null, null) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    @SuppressLint("CheckResult")
    private fun init() {
        val mActivity = activity as UserActivity
        mActivity.setProgressDialogValueAndVisible(56, View.VISIBLE)
        mActivity.setPrevFragment(UserAuthorizationForModifyForgottenPasswordFragment())

        binding.userModifyForgottenPasswordBtnNext.setOnClickListener(this)

        //에딧 텍스트 subscribe
        binding.userModifyForgottenPasswordEditTextPassword.textChanges().subscribe { password ->
            binding.userModifyForgottenPasswordEditTextPasswordCheck.textChanges().subscribe {
                binding.userModifyForgottenPasswordBtnNext.isEnabled = password.isNotEmpty() && it.isNotEmpty()
            }
        }

        //에딧 텍스트 키보드 완료 리스너 구현
        binding.userModifyForgottenPasswordEditTextPasswordCheck.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            //에딧 텍스트 검색 아이콘 클릭 이벤트 처리
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    binding.userModifyForgottenPasswordBtnNext.performClick()
                    return true
                }
                return false
            }
        })
    }

    //비밀번호 일치 여부 체크
    private fun checkPasswordNotSame(): Boolean {
        return binding.userModifyForgottenPasswordEditTextPassword.text.toString() != binding.userModifyForgottenPasswordEditTextPasswordCheck.text.toString()
    }

    //비밀번호 정규식 체크  영문자, 특수문자, 숫자 3개 조합하여 8자리 이상 ~20까지
    private fun checkPasswordRegex() : Boolean {
        val pwPattern =  "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@!%*#?&]).{8,20}.$"
        val pattern = Pattern.compile(pwPattern)
        val matcher = pattern.matcher(binding.userModifyForgottenPasswordEditTextPassword.text)
        return matcher.find()
    }

    //editText 비우기 및 에러 안내 토스트 발생
    private fun resetPasswordEditTextWithToast(content: String) {
        Toast.makeText(requireContext(), content, Toast.LENGTH_SHORT).show()
        binding.userModifyForgottenPasswordEditTextPassword.setText("")
        binding.userModifyForgottenPasswordEditTextPasswordCheck.setText("")
        binding.userModifyForgottenPasswordEditTextPassword.requestFocus()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.userModifyForgottenPasswordBtnNext -> {
//                //비밀번호 일치 여부 체크
//                if (checkPasswordNotSame()) {
//                    resetPasswordEditTextWithToast("비밀번호가 서로 일치하지 않습니다.")
//                    return
//                }
//
//                //비밀번호 정규식 체크
//                if (!checkPasswordRegex()) {
//                    resetPasswordEditTextWithToast("영문자, 특수문자, 숫자 3개를 조합하여 8자리 이상 입력해 주세요.")
//                    return
//                }

                _userData.password = binding.userModifyForgottenPasswordEditTextPassword.text.toString()
                //비밀번호 변경 retrofit 통신 후 HomeAcitivty로 이동
                retrofitModifyForgottenPassword()
            }
        }
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun retrofitModifyForgottenPassword() {
    //로딩 프로그레스 바 출력
    customProgressDialog.show(childFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.modifyForgottenPassword(_userData.phoneNumber, _userData.password, completion = {
            completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d(
                        "로그",
                        "UserModifyForgottenPasswordFragment - retrofitModifyForgottenPassword : $response"
                    )

                    //비밀번호 변경 후 토큰 값 가져오기
                    retrofitLoginUser()
                }
                CompletionResponse.FAIL -> {
                    Log.d(
                        "로그",
                        "UserModifyForgottenPasswordFragment - retrofitModifyForgottenPassword : 통신 실패"
                    )
                }
            }
        })
    }

    private fun retrofitLoginUser() {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(childFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.loginUser(_userData, completion = {
                completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        //로그인 성공
                        200 -> {
                            //토큰 값 저장
                            LoginedUserClient.authorization = response.headers()["Authorization"]
                            LoginedUserClient.refreshToken = response.headers()["refresh_token"]

                            Toast.makeText(requireContext(), "비밀번호 변경 성공!", Toast.LENGTH_SHORT).show()
                            val mActivity = activity as UserActivity
                            mActivity.changeFragmentTransaction(UserSetConnectDeviceFragment())
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