package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetPasswordBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData
import java.util.regex.Pattern


class UserSetPasswordFragment : BaseFragment<FragmentUserSetPasswordBinding>(FragmentUserSetPasswordBinding::inflate), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()


    }

    private fun init() {
        val mActivity = activity as UserActivity
        mActivity.setProgressDialogValueAndVisible(56, View.VISIBLE)
        mActivity.setPrevFragment(UserSetNicknameFragment())

        binding.userSetPasswordBtnNext.setOnClickListener(this)

        setEditTextTextChanged()
    }

    private fun setEditTextTextChanged() {
        binding.userSetPasswordEditTextPassword.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userSetPasswordBtnNext.isEnabled = s?.length != 0 && binding.userSetPasswordEditTextPasswordCheck.text.isNotEmpty()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.userSetPasswordEditTextPasswordCheck.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userSetPasswordBtnNext.isEnabled = s?.length != 0 && binding.userSetPasswordEditTextPassword.text.isNotEmpty()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
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
//                //비밀번호 일치 여부 체크
                if(checkPasswordNotSame()) {
                    resetPasswordEditTextWithToast("비밀번호가 서로 일치하지 않습니다.")
                    return
                }

                //비밀번호 정규식 체크
                if(!checkPasswordRegex()) {
                    resetPasswordEditTextWithToast("영문자, 특수문자, 숫자 3개를 조합하여 8자리 이상 입력해 주세요.")
                    return
                }

                _userData.password = binding.userSetPasswordEditTextPassword.text.toString()

                val mActivity = activity as UserActivity
                mActivity.changeFragment("UserSetBirthdayFragment")
            }
        }
    }
}