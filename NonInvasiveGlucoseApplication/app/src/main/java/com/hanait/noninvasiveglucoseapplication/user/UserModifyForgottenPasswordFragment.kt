package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserModifyForgottenPasswordBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import java.util.regex.Pattern

class UserModifyForgottenPasswordFragment : BaseFragment<FragmentUserModifyForgottenPasswordBinding>(
    FragmentUserModifyForgottenPasswordBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        val mActivity = activity as UserActivity
        mActivity.setProgressDialogValueAndVisible(56, View.VISIBLE)
        mActivity.setPrevFragment(UserSetNicknameFragment())

        binding.userModifyForgottenPasswordBtnNext.setOnClickListener(this)

        setEditTextTextChanged()
    }

    private fun setEditTextTextChanged() {
        binding.userModifyForgottenPasswordEditTextPassword.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userModifyForgottenPasswordBtnNext.isEnabled = s?.length != 0 && binding.userModifyForgottenPasswordEditTextPasswordCheck.text.isNotEmpty()
                if(s?.length != 0 && binding.userModifyForgottenPasswordEditTextPasswordCheck.text.isNotEmpty()) binding.userModifyForgottenPasswordBtnNext.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.iphone_green_200))
                else binding.userModifyForgottenPasswordBtnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.userModifyForgottenPasswordEditTextPasswordCheck.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userModifyForgottenPasswordBtnNext.isEnabled = s?.length != 0 && binding.userModifyForgottenPasswordEditTextPassword.text.isNotEmpty()
                if(s?.length != 0 && binding.userModifyForgottenPasswordEditTextPassword.text.isNotEmpty()) binding.userModifyForgottenPasswordBtnNext.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.iphone_green_200))
                else binding.userModifyForgottenPasswordEditTextPassword.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
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
        when(v) {
            binding.userModifyForgottenPasswordBtnNext -> {
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
                
                //비밀번호 변경 retrofit 통신 후 HomeAcitivty로 이동

            }
        }
    }
}