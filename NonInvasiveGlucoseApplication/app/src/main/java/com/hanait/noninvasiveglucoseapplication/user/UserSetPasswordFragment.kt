package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
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

    //???????????? ?????? ?????? ??????
    private fun checkPasswordNotSame(): Boolean {
        return binding.userSetPasswordEditTextPassword.text.toString() != binding.userSetPasswordEditTextPasswordCheck.text.toString()
    }

    //???????????? ????????? ??????  ?????????, ????????????, ?????? 3??? ???????????? 8?????? ?????? ~20??????
    private fun checkPasswordRegex() : Boolean {
        val pwPattern =  "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@!%*#?&]).{8,20}.$"
        val pattern = Pattern.compile(pwPattern)
        val matcher = pattern.matcher(binding.userSetPasswordEditTextPassword.text)
        return matcher.find()
    }

    //editText ????????? ??? ?????? ?????? ????????? ??????
    private fun resetPasswordEditTextWithToast(content: String) {
        Toast.makeText(requireContext(), content, Toast.LENGTH_SHORT).show()
        binding.userSetPasswordEditTextPassword.setText("")
        binding.userSetPasswordEditTextPasswordCheck.setText("")
        binding.userSetPasswordEditTextPassword.requestFocus()
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetPasswordBtnNext -> {
//                //???????????? ?????? ?????? ??????
//                if(checkPasswordNotSame()) {
//                    resetPasswordEditTextWithToast("??????????????? ?????? ???????????? ????????????.")
//                    return
//                }
//
//                //???????????? ????????? ??????
//                if(!checkPasswordRegex()) {
//                    resetPasswordEditTextWithToast("?????????, ????????????, ?????? 3?????? ???????????? 8?????? ?????? ????????? ?????????.")
//                    return
//                }

                _userData.password = binding.userSetPasswordEditTextPassword.text.toString()

                val mActivity = activity as UserActivity
                mActivity.changeFragmentTransaction(UserSetBirthdayFragment())
            }
        }
    }
}