package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetNicknameBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData

class UserSetNicknameFragment : BaseFragment<FragmentUserSetNicknameBinding>(FragmentUserSetNicknameBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    private fun init() {
        val mActivity = activity as UserActivity
        mActivity.setProgressDialogValueAndVisible(42, View.VISIBLE)
        mActivity.setPrevFragment(UserSetAuthorizationFragment())

        binding.userSetNicknameBtnNext.setOnClickListener(this)

        setEditTextTextChanged()
    }

    //텍스트 비어있을 경우 버튼 색상 변경 이벤트
    private fun setEditTextTextChanged() {
        binding.userSetNicknameEditTextNickname.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userSetNicknameBtnNext.isEnabled = s?.length != 0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
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