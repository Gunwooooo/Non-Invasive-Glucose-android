package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentConnectionLoadingBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mPrevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.mProgressBar


class ConnectionLoadingFragment : BaseFragment<FragmentConnectionLoadingBinding>(FragmentConnectionLoadingBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        mPrevFragment = UserSetConnectDeviceFragment()
        mProgressBar.visibility = View.GONE
    }

}