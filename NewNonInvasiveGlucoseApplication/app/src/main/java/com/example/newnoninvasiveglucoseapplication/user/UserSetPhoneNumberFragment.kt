package com.example.newnoninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentUserSetPhoneNumberBinding
import com.example.newnoninvasiveglucoseapplication.model.UserData
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import com.example.newnoninvasiveglucoseapplication.util.Constants._userData
import com.example.newnoninvasiveglucoseapplication.util.CustomDialogManager
import com.jakewharton.rxbinding4.widget.textChanges
import kotlinx.coroutines.CoroutineScope


class UserSetPhoneNumberFragment : BaseFragment<FragmentUserSetPhoneNumberBinding>(FragmentUserSetPhoneNumberBinding::inflate), View.OnClickListener {

    private val customProgressDialog by lazy { CustomDialogManager(requireContext(), R.layout.common_progress_dialog, null, null) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    @SuppressLint("CheckResult")
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

        //에딧 텍스트 키보드 완료 리스너 구현
        binding.userSetPhoneNumberEditTextPhoneNumber.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            //에딧 텍스트 검색 아이콘 클릭 이벤트 처리
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    binding.userSetPhoneNumberBtnNext.performClick()
                    return true
                }
                return false
            }
        })

        //키보드 출력하기
        showSoftInput()

        //서버에 보낼 유저 데이터 클래스 초기화
        _userData = UserData( "", "", "", "", "T")
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetPhoneNumberBtnNext -> {
                _userData.phoneNumber = binding.userSetPhoneNumberEditTextPhoneNumber.text.toString()
                //전화번호 길이 체크
                if(_userData.phoneNumber.length != 11) {
                    Toast.makeText(requireContext(), "올바른 전화번호 양식이 아닙니다.", Toast.LENGTH_SHORT).show()
                    binding.userSetPhoneNumberEditTextPhoneNumber.setText("")
                    return
                }
                //등록된 회원인지 확인
                retrofitCheckJoinedUser()
            }
        }
    }

    //전화번호 포맷 확인
    private fun checkCorrectPhoneNumber() : Boolean {
        return _userData.phoneNumber.length == 11
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