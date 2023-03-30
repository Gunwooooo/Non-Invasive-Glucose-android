package com.example.newnoninvasiveglucoseapplication.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
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

class UserCheckPasswordFragment : BaseFragment<FragmentUserCheckPasswordBinding>(FragmentUserCheckPasswordBinding::inflate), View.OnClickListener  {

    private val customProgressDialog by lazy { CustomDialogManager(requireContext(), R.layout.common_progress_dialog, null) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

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

        setEditTextTextChanged()
    }

    //텍스트 비어있을 경우 버튼 색상 변경 이벤트
    private fun setEditTextTextChanged() {
        binding.userCheckPasswordEditTextPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userCheckPasswordBtnNext.isEnabled = s?.length != 0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
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