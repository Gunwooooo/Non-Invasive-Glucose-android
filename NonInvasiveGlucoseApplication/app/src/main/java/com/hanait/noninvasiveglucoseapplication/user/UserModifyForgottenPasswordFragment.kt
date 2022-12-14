package com.hanait.noninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserModifyForgottenPasswordBinding
import com.hanait.noninvasiveglucoseapplication.home.HomeActivity
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient
import org.json.JSONObject
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
        mActivity.setPrevFragment(UserAuthorizationForModifyForgottenPasswordFragment())

        binding.userModifyForgottenPasswordBtnNext.setOnClickListener(this)

        setEditTextTextChanged()
    }

    private fun setEditTextTextChanged() {
        binding.userModifyForgottenPasswordEditTextPassword.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userModifyForgottenPasswordBtnNext.isEnabled = s?.length != 0 && binding.userModifyForgottenPasswordEditTextPasswordCheck.text.isNotEmpty()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.userModifyForgottenPasswordEditTextPasswordCheck.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userModifyForgottenPasswordBtnNext.isEnabled = s?.length != 0 && binding.userModifyForgottenPasswordEditTextPassword.text.isNotEmpty()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    //???????????? ?????? ?????? ??????
    private fun checkPasswordNotSame(): Boolean {
        return binding.userModifyForgottenPasswordEditTextPassword.text.toString() != binding.userModifyForgottenPasswordEditTextPasswordCheck.text.toString()
    }

    //???????????? ????????? ??????  ?????????, ????????????, ?????? 3??? ???????????? 8?????? ?????? ~20??????
    private fun checkPasswordRegex() : Boolean {
        val pwPattern =  "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@!%*#?&]).{8,20}.$"
        val pattern = Pattern.compile(pwPattern)
        val matcher = pattern.matcher(binding.userModifyForgottenPasswordEditTextPassword.text)
        return matcher.find()
    }

    //editText ????????? ??? ?????? ?????? ????????? ??????
    private fun resetPasswordEditTextWithToast(content: String) {
        Toast.makeText(requireContext(), content, Toast.LENGTH_SHORT).show()
        binding.userModifyForgottenPasswordEditTextPassword.setText("")
        binding.userModifyForgottenPasswordEditTextPasswordCheck.setText("")
        binding.userModifyForgottenPasswordEditTextPassword.requestFocus()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.userModifyForgottenPasswordBtnNext -> {
//                //???????????? ?????? ?????? ??????
//                if (checkPasswordNotSame()) {
//                    resetPasswordEditTextWithToast("??????????????? ?????? ???????????? ????????????.")
//                    return
//                }
//
//                //???????????? ????????? ??????
//                if (!checkPasswordRegex()) {
//                    resetPasswordEditTextWithToast("?????????, ????????????, ?????? 3?????? ???????????? 8?????? ?????? ????????? ?????????.")
//                    return
//                }

                _userData.password = binding.userModifyForgottenPasswordEditTextPassword.text.toString()
                //???????????? ?????? retrofit ?????? ??? HomeAcitivty??? ??????
                retrofitModifyForgottenPassword()
            }
        }
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private fun retrofitModifyForgottenPassword() {
        RetrofitManager.instance.modifyForgottenPassword(_userData.phoneNumber, _userData.password, completion = {
            completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d(
                        "??????",
                        "UserModifyForgottenPasswordFragment - retrofitModifyForgottenPassword : $response"
                    )

                    //???????????? ?????? ??? ?????? ??? ????????????
                    retrofitLoginUser()
                }
                CompletionResponse.FAIL -> {
                    Log.d(
                        "??????",
                        "UserModifyForgottenPasswordFragment - retrofitModifyForgottenPassword : ?????? ??????"
                    )
                }
            }
        })
    }

    private fun retrofitLoginUser() {
        RetrofitManager.instance.loginUser(_userData, completion = {
                completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        //????????? ??????
                        200 -> {
                            //?????? ??? ??????
                            LoginedUserClient.authorization = response.headers()["Authorization"]

                            Toast.makeText(requireContext(), "???????????? ?????? ??????!", Toast.LENGTH_SHORT).show()
                            val mActivity = activity as UserActivity
                            mActivity.changeFragmentTransaction(UserSetConnectDeviceFragment())
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("??????", "UserCheckPasswordFragment - retrofitLoginUser : ????????? ?????? ??????")
                }
            }
        })
    }

}