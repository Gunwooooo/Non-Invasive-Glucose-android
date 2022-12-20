package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetPhoneNumberBinding
import com.hanait.noninvasiveglucoseapplication.model.UserData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData


class UserSetPhoneNumberFragment : BaseFragment<FragmentUserSetPhoneNumberBinding>(FragmentUserSetPhoneNumberBinding::inflate), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()


    }

    //텍스트 비어있을 경우 버튼 색상 변경 이벤트
    private fun setEditTextTextChanged() {
        binding.userSetPhoneNumberEditTextPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userSetPhoneNumberBtnNext.isEnabled = s?.length != 0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun init(){
        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity.setProgressDialogValueAndVisible(14, View.VISIBLE)
        mActivity.setPrevFragment(UserSetPhoneNumberFragment())
        //프로그래스바 셋
        binding.userSetPhoneNumberBtnNext.setOnClickListener(this)

        setEditTextTextChanged()

        //유저 데이터 클래스 초기화
        _userData = UserData("", "", "", "", "T")
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetPhoneNumberBtnNext -> {
                _userData.phoneNumber = binding.userSetPhoneNumberEditTextPhoneNumber.text.toString()
                retrofitCheckJoinedUser()
            }
        }
    }

    //가입된 유저 체크
    private fun retrofitCheckJoinedUser() {
        RetrofitManager.instance.checkJoinedUser(_userData, completion = {
            completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    val mActivity = activity as UserActivity
                    when(response?.code()) {
                        //존재하지 않는 아이디일 경우 전화번호 인증화면으로 이동
                        200 -> {
                            mActivity.changeFragmentTransaction(UserSetAuthorizationFragment())
                        }
                        //존재하는 아이디일 경우 로그인 화면으로 이동
                        400 -> {
                            mActivity.changeFragmentTransaction(UserCheckPasswordFragment())
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "UserSetPhoneNumberFragment - retrofitCheckJoinedUser : 통신 실패")
                }
            }
        })
    }
}