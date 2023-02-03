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
import com.jakewharton.rxbinding4.widget.textChanges

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

        //에딧 텍스트 subscribe
        binding.userSetNicknameEditTextNickname.textChanges().subscribe {
            binding.userSetNicknameBtnNext.isEnabled = it.isNotEmpty()
        }
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