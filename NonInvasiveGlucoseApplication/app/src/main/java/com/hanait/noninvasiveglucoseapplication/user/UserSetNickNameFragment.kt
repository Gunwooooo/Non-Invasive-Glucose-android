package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetNickNameBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._prevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._progressBar
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData

class UserSetNickNameFragment : BaseFragment<FragmentUserSetNickNameBinding>(FragmentUserSetNickNameBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    private fun init() {
        _prevFragment = UserSetAuthorizationFragment()
        _progressBar.progress = 42

        binding.userSetNickNameBtnNext.setOnClickListener(this)

        setEditTextTextChanged()
    }

    //텍스트 비어있을 경우 버튼 색상 변경 이벤트
    private fun setEditTextTextChanged() {
        binding.userSetNickNameEditTextNickName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userSetNickNameBtnNext.isEnabled = s?.length != 0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetNickNameBtnNext -> {
                _userData.nickName = binding.userSetNickNameEditTextNickName.text.toString()

                val mActivity = activity as UserActivity
                mActivity.changeFragment("UserSetPasswordFragment")
            }
        }
    }
}