package com.example.newnoninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentConnectionLoadingBinding
import com.example.newnoninvasiveglucoseapplication.home.HomeActivity
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment


class ConnectionLoadingFragment : BaseFragment<FragmentConnectionLoadingBinding>(FragmentConnectionLoadingBinding::inflate), View.OnClickListener {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

//        Handler().postDelayed({
//            binding.connectionLoadingTextViewTitle.text = "연결을 완료했습니다.\n건강 관리를 시작해보세요."
//            binding.connectionLoadingBtnNext.isEnabled = true
//            binding.connectionLoadingBtnNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.iphone_green_200))
//        }, 500)

    }

    private fun init() {
        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.setBtnBackVisible(View.INVISIBLE)
        mActivity.setProgressDialogValueAndVisible(100, View.GONE)
        mActivity.setPrevFragment(UserSetConnectDeviceFragment())

        binding.connectionLoadingLottie.progress = 1F



        binding.connectionLoadingBtnNext.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.connectionLoadingBtnNext -> {
                binding.connectionLoadingLottie.playAnimation()
                binding.connectionLoadingBtnNext.visibility = View.INVISIBLE
                Handler().postDelayed({
                    //홈액티비티 실행
                    startActivity(Intent(context, HomeActivity::class.java))
                    val mActivity = activity as UserActivity
                    mActivity.finish()
                }, 2700)
            }
        }
    }
}