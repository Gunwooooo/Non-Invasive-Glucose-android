package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetPhoneNumberBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mPrevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mProgressBar
import com.hanait.noninvasiveglucoseapplication.util.Constants.mUserData
import com.hanait.noninvasiveglucoseapplication.util.UserData


class UserSetPhoneNumberFragment : BaseFragment<FragmentUserSetPhoneNumberBinding>(FragmentUserSetPhoneNumberBinding::inflate), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setEditTextTextChanged()
    }

    //텍스트 비어있을 경우 버튼 색상 변경 이벤트
    private fun setEditTextTextChanged() {
        binding.userSetPhoneNumberEditTextPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length != 0)
                    binding.userSetPhoneNumberBtnNext.setBackgroundResource(R.drawable.btn_border_blue)
                else 
                    binding.userSetPhoneNumberBtnNext.setBackgroundResource(R.drawable.btn_border_light_gray)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun init(){
        mPrevFragment = UserSetPhoneNumberFragment()
        mProgressBar.progress = 16

        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.userSetPhoneNumberBtnNext.setOnClickListener(this)

        //유저 데이터 클래스 초기화
        mUserData = UserData("", "", "", "")
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetPhoneNumberBtnNext -> {
                mUserData.phoneNumber = binding.userSetPhoneNumberEditTextPhoneNumber.text.toString()

                val mActivity = activity as UserActivity
                mActivity.changeFragment("UserSetAuthorizationFragment")
            }
        }
    }


}