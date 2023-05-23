package com.example.newnoninvasiveglucoseapplication.user

import android.annotation.SuppressLint
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
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentUserCheckPasswordBinding
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import com.example.newnoninvasiveglucoseapplication.util.Constants._userData
import com.example.newnoninvasiveglucoseapplication.util.Constants._prefs
import com.example.newnoninvasiveglucoseapplication.util.CustomDialogManager
import com.example.newnoninvasiveglucoseapplication.util.LoginedUserClient
import com.jakewharton.rxbinding4.widget.textChanges

class UserCheckPasswordFragment : BaseFragment<FragmentUserCheckPasswordBinding>(FragmentUserCheckPasswordBinding::inflate), View.OnClickListener  {

    private val customProgressDialog by lazy { CustomDialogManager(requireContext(), R.layout.common_progress_dialog, null, null) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    @SuppressLint("CheckResult")
    private fun init() {
        //액션바 다시 보이게하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.setBtnBackVisible(View.VISIBLE)
        mActivity.setProgressDialogValueAndVisible(60, View.VISIBLE)
        mActivity.setPrevFragment(UserSetPhoneNumberFragment())

        //자동 로그인 체크
        binding.userCheckPasswordCheckBoxAutoLogin.isChecked = _prefs.getBoolean("AUTO_LOGIN", false)

        //전화번호 입력해놓기
        binding.userCheckPasswordEditTextPhoneNumber.hint = _userData.phoneNumber

        binding.userCheckPasswordBtnNext.setOnClickListener(this)
        binding.userCheckPasswordEditTextPhoneNumber.setOnClickListener(this)
        binding.userCheckPasswordTextViewForgetPassword.setOnClickListener(this)

        //에딧텍스트 문자열 subscribe
        binding.userCheckPasswordEditTextPassword.textChanges().subscribe {
            binding.userCheckPasswordBtnNext.isEnabled = it.isNotEmpty()
        }

        //에딧 텍스트 키보드 완료 리스너 구현
        binding.userCheckPasswordEditTextPassword.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            //에딧 텍스트 검색 아이콘 클릭 이벤트 처리
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    binding.userCheckPasswordBtnNext.performClick()
                    return true
                }
                return false
            }
        })
    }

    override fun onClick(v: View?) {
        val mActivity = activity as UserActivity
        when(v) {
            binding.userCheckPasswordBtnNext -> {
                _userData.password = binding.userCheckPasswordEditTextPassword.text.toString()

                retrofitLoginUser()
            }
            binding.userCheckPasswordEditTextPhoneNumber -> {
                mActivity.changePrevFragment()
            }
            binding.userCheckPasswordTextViewForgetPassword -> {
                mActivity.changeFragmentTransaction(UserAuthorizationForModifyForgottenPasswordFragment())
            }
        }
    }

    //자동 로그인 시 데이터 로컬 저장
    private fun setAutoLogin() {
        val autoLoginCheckBox = binding.userCheckPasswordCheckBoxAutoLogin
        _prefs.setBoolean("AUTO_LOGIN", autoLoginCheckBox.isChecked)
        when(autoLoginCheckBox.isChecked) {
            true -> {
                _prefs.setString("USER_PHONENUMBER", _userData.phoneNumber)
                _prefs.setString("USER_PASSWORD", _userData.password)
            }
            false -> {
                _prefs.setString("USER_PHONENUMBER", "")
                _prefs.setString("USER_PASSWORD", "")
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    private fun retrofitLoginUser() {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(childFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.loginUser(_userData, completion = {
            completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        //로그인 실패 했을 경우
                        401 -> {
                            Toast.makeText(requireContext(), "회원 정보가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                            binding.userCheckPasswordEditTextPassword.setText("")
                        }
                        //로그인 성공
                        200 -> {
                            //토큰 값 저장
                            LoginedUserClient.authorization = response.headers()["Authorization"]
                            LoginedUserClient.refreshToken = response.headers()["refresh_token"]
                            Log.d("로그", "UserCheckPasswordFragment - retrofitLoginUser : AT : ${LoginedUserClient.authorization}")
                            Log.d("로그", "UserCheckPasswordFragment - retrofitLoginUser : RT : ${LoginedUserClient.refreshToken}")

                            //자동 로그인 설정
                            setAutoLogin()

                            Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()
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