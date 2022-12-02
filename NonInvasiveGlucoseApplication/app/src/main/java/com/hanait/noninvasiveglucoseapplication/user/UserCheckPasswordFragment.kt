package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserCheckPasswordBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._userData

class UserCheckPasswordFragment : BaseFragment<FragmentUserCheckPasswordBinding>(FragmentUserCheckPasswordBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        //액션바 다시 보이게하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity.setProgressDialogValueAndVisible(60, View.VISIBLE)
        mActivity.setPrevFragment(UserSetPhoneNumberFragment())

        //전화번호 입력해놓기
        binding.userCheckPasswordEditTextPhoneNumber.hint = _userData.phoneNumber

        binding.userCheckPasswordBtnNext.setOnClickListener(this)

        setEditTextTextChanged()
    }

    //텍스트 비어있을 경우 버튼 색상 변경 이벤트
    private fun setEditTextTextChanged() {
        binding.userCheckPasswordEditTextPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.userCheckPasswordBtnNext.isEnabled = s?.length != 0
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }


    override fun onClick(v: View?) {
        when(v) {
            binding.userCheckPasswordBtnNext -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragment("UserSetConnectDeviceFragment")
            }
        }
    }
}