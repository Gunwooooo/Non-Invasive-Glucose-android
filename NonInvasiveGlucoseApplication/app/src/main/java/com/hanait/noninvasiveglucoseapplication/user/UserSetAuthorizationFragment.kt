package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetAuthorizationBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.prevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.progressBar

class UserSetAuthorizationFragment : BaseFragment<FragmentUserSetAuthorizationBinding>(FragmentUserSetAuthorizationBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        prevFragment = UserSetPhoneNumberFragment()
        progressBar.progress = 32


        //액션바 다시 보이게하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.userSetAuthorizationBtnNext.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetAuthorizationBtnNext -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragment("UserSetPasswordFragment")
            }
        }
    }
}