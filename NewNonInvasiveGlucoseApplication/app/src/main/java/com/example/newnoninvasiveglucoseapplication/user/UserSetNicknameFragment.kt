package com.example.newnoninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentUserSetNicknameBinding
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import com.example.newnoninvasiveglucoseapplication.util.Constants._userData
import com.jakewharton.rxbinding4.widget.textChanges

class UserSetNicknameFragment : BaseFragment<FragmentUserSetNicknameBinding>(FragmentUserSetNicknameBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    @SuppressLint("CheckResult")
    private fun init() {
        val mActivity = activity as UserActivity
        mActivity.setProgressDialogValueAndVisible(42, View.VISIBLE)
        mActivity.setPrevFragment(UserSetAuthorizationFragment())

        binding.userSetNicknameBtnNext.setOnClickListener(this)

        //에딧 텍스트 subscribe
        binding.userSetNicknameEditTextNickname.textChanges().subscribe {
            binding.userSetNicknameBtnNext.isEnabled = it.isNotEmpty()
        }

        //에딧 텍스트 키보드 완료 리스너 구현
        binding.userSetNicknameEditTextNickname.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            //에딧 텍스트 검색 아이콘 클릭 이벤트 처리
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    binding.userSetNicknameBtnNext.performClick()
                    return true
                }
                return false
            }
        })
    }


    override fun onClick(v: View?) {
        when(v) {
            binding.userSetNicknameBtnNext -> {
                _userData.nickname = binding.userSetNicknameEditTextNickname.text.toString()

                val mActivity = activity as UserActivity
                mActivity.changeFragmentTransaction(UserSetPasswordFragment())
            }
        }
    }
}