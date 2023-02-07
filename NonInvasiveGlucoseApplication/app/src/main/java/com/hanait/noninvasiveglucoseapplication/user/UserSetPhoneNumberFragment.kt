package com.hanait.noninvasiveglucoseapplication.user

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetPhoneNumberBinding
import com.hanait.noninvasiveglucoseapplication.model.UserData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogManager
import com.jakewharton.rxbinding4.widget.textChanges


class UserSetPhoneNumberFragment : BaseFragment<FragmentUserSetPhoneNumberBinding>(FragmentUserSetPhoneNumberBinding::inflate), View.OnClickListener {

    private val customProgressDialog by lazy { CustomDialogManager(R.layout.common_progress_dialog, null) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){
        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.setBtnBackVisible(View.INVISIBLE)
        mActivity.setProgressDialogValueAndVisible(14, View.VISIBLE)
        mActivity.setPrevFragment(UserSetPhoneNumberFragment())
        //프로그래스바 셋
        binding.userSetPhoneNumberBtnNext.setOnClickListener(this)

        //에딧텍스트 문자열 subscribe
        binding.userSetPhoneNumberEditTextPhoneNumber.textChanges().subscribe {
            binding.userSetPhoneNumberBtnNext.isEnabled = it.isNotEmpty()
        }

        //키보드 출력하기
        showSoftInput()

        //서버에 보낼 유저 데이터 클래스 초기화
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

    //키보드 올리기
    private fun showSoftInput() {
        if (binding.userSetPhoneNumberEditTextPhoneNumber.requestFocus()) {
            // 프래그먼트기 때문에 getActivity() 사용
            val inputManager: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(binding.userSetPhoneNumberEditTextPhoneNumber, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    //가입된 유저 체크
    private fun retrofitCheckJoinedUser() {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(childFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.checkJoinedUser(_userData, completion = {
            completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    val mActivity = activity as UserActivity
                    customProgressDialog.dismiss()
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