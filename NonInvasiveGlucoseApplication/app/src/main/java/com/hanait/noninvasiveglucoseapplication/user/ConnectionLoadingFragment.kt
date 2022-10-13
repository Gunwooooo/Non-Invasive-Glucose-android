package com.hanait.noninvasiveglucoseapplication.user

import android.content.Intent
import android.os.Bundle
import android.os.Handler
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

        Handler().postDelayed({
            startActivity(Intent(context, UserActivity::class.java))
            val mActivity = activity as UserActivity
            mActivity.finish()
        }, 1000)

    }

    private fun init() {
        mPrevFragment = UserSetConnectDeviceFragment()
        mProgressBar.visibility = View.GONE

        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

}