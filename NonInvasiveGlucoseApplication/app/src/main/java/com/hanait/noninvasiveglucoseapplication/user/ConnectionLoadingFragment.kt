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
import com.hanait.noninvasiveglucoseapplication.home.HomeActivity
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._prevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._progressBar


class ConnectionLoadingFragment : BaseFragment<FragmentConnectionLoadingBinding>(FragmentConnectionLoadingBinding::inflate), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        Handler().postDelayed({
            binding.connectionLoadingTextViewTitle.text = "연결을 완료했습니다.\n건강 관리를 시작해보세요."
            binding.connectionLoadingLottie.visibility = View.GONE
            binding.connectionLoadingImageView.visibility = View.VISIBLE
            binding.connectionLoadingBtnNext.isEnabled = true


        }, 3000)

    }

    private fun init() {
        _prevFragment = UserSetConnectDeviceFragment()
        _progressBar.visibility = View.GONE

        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.connectionLoadingBtnNext.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.connectionLoadingBtnNext -> {
                startActivity(Intent(context, HomeActivity::class.java))
                val mActivity = activity as UserActivity
                mActivity.finish()
            }
        }
    }
}