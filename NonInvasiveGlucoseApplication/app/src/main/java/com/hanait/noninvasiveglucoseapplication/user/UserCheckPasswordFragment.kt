package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserCheckPasswordBinding
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData
import com.hanait.noninvasiveglucoseapplication.util.Constants.prefs
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient

class UserCheckPasswordFragment : BaseFragment<FragmentUserCheckPasswordBinding>(FragmentUserCheckPasswordBinding::inflate), View.OnClickListener  {
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
        binding.userCheckPasswordCheckBoxAutoLogin.isChecked = prefs.getBoolean("AUTO_LOGIN", false)

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
        prefs.setBoolean("AUTO_LOGIN", autoLoginCheckBox.isChecked)
        when(autoLoginCheckBox.isChecked) {
            true -> {
                prefs.setString("USER_PHONENUMBER", _userData.phoneNumber)
                prefs.setString("USER_PASSWORD", _userData.password)
            }
            false -> {
                prefs.setString("USER_PHONENUMBER", "")
                prefs.setString("USER_PASSWORD", "")
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    private fun retrofitLoginUser() {
        RetrofitManager.instance.loginUser(_userData, completion = {
            completionResponse, response ->
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