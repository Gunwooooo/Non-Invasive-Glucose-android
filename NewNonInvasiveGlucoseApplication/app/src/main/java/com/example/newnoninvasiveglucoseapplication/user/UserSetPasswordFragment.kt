package com.example.newnoninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentUserSetPasswordBinding
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import com.example.newnoninvasiveglucoseapplication.util.Constants._userData
import com.jakewharton.rxbinding4.widget.textChanges
import java.util.regex.Pattern

class UserSetPasswordFragment : BaseFragment<FragmentUserSetPasswordBinding>(FragmentUserSetPasswordBinding::inflate), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    @SuppressLint("CheckResult")
    private fun init() {
        val mActivity = activity as UserActivity
        mActivity.setProgressDialogValueAndVisible(56, View.VISIBLE)
        mActivity.setPrevFragment(UserSetNicknameFragment())

        binding.userSetPasswordBtnNext.setOnClickListener(this)

        //에딧 텍스트 subscribe
        binding.userSetPasswordEditTextPassword.textChanges().subscribe { password ->
            binding.userSetPasswordEditTextPasswordCheck.textChanges().subscribe {
                binding.userSetPasswordBtnNext.isEnabled = password.isNotEmpty() && it.isNotEmpty()
            }
        }

        //에딧 텍스트 키보드 완료 리스너 구현
        binding.userSetPasswordEditTextPassword.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            //에딧 텍스트 검색 아이콘 클릭 이벤트 처리
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    binding.userSetPasswordBtnNext.performClick()
                    return true
                }
                return false
            }
        })
    }

    //비밀번호 일치 여부 체크
    private fun checkPasswordNotSame(): Boolean {
        return binding.userSetPasswordEditTextPassword.text.toString() != binding.userSetPasswordEditTextPasswordCheck.text.toString()
    }

    //비밀번호 정규식 체크  영문자, 특수문자, 숫자 3개 조합하여 8자리 이상 ~20까지
    private fun checkPasswordRegex() : Boolean {
        val pwPattern =  "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@!%*#?&]).{8,20}.$"
        val pattern = Pattern.compile(pwPattern)
        val matcher = pattern.matcher(binding.userSetPasswordEditTextPassword.text)
        return matcher.find()
    }

    //editText 비우기 및 에러 안내 토스트 발생
    private fun resetPasswordEditTextWithToast(content: String) {
        Toast.makeText(requireContext(), content, Toast.LENGTH_SHORT).show()
        binding.userSetPasswordEditTextPassword.setText("")
        binding.userSetPasswordEditTextPasswordCheck.setText("")
        binding.userSetPasswordEditTextPassword.requestFocus()
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetPasswordBtnNext -> {
                //비밀번호 일치 여부 체크
                if(checkPasswordNotSame()) {
                    resetPasswordEditTextWithToast("비밀번호가 서로 일치하지 않습니다.")
                    return
                }

                //비밀번호 정규식 체크
                if(!checkPasswordRegex()) {
                    resetPasswordEditTextWithToast("영문자, 특수문자, 숫자 3개를 조합하여 8자리 이상 입력해 주세요.")
                    return
                }
                Log.d("로그", "UserSetPasswordFragment - onClick : AWEFAWEFAWEFAWEF")
                _userData.password = binding.userSetPasswordEditTextPassword.text.toString()

                val mActivity = activity as UserActivity
                mActivity.changeFragmentTransaction(UserSetBirthdayFragment())
            }
        }
    }
}