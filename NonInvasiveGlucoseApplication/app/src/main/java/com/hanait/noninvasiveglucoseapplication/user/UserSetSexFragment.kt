package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetSexBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants
import com.hanait.noninvasiveglucoseapplication.util.Constants.prevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.progressBar

class UserSetSexFragment : BaseFragment<FragmentUserSetSexBinding>(FragmentUserSetSexBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        prevFragment = UserSetBirthdayFragment()
        progressBar.progress = 90

        binding.userSetSexBtnNext.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v) {
            binding.userSetSexBtnNext -> {

            }
        }
    }

}