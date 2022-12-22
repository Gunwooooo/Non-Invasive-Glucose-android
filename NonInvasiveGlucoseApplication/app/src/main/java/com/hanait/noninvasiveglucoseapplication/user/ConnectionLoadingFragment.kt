package com.hanait.noninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentConnectionLoadingBinding
import com.hanait.noninvasiveglucoseapplication.home.HomeActivity
import com.hanait.noninvasiveglucoseapplication.model.UserData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient
import org.json.JSONObject


class ConnectionLoadingFragment : BaseFragment<FragmentConnectionLoadingBinding>(FragmentConnectionLoadingBinding::inflate), View.OnClickListener {
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        Handler().postDelayed({
            binding.connectionLoadingTextViewTitle.text = "연결을 완료했습니다.\n건강 관리를 시작해보세요."
            binding.connectionLoadingLottie.visibility = View.GONE
            binding.connectionLoadingImageView.visibility = View.VISIBLE
            binding.connectionLoadingBtnNext.isEnabled = true
        }, 500)

    }

    private fun init() {
        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.setBtnBackVisible(View.INVISIBLE)
        mActivity.setProgressDialogValueAndVisible(100, View.GONE)
        mActivity.setPrevFragment(UserSetConnectDeviceFragment())

        //로그인된 유저 데이터 가져오기
        retrofitInfoLoginedUser()

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

    //로그인 된 회원 정보 가져오기
    @SuppressLint("SetTextI18n")
    private fun retrofitInfoLoginedUser() {
        RetrofitManager.instance.infoLoginedUser(completion = { completionResponse, response ->
            when (completionResponse) {
                CompletionResponse.OK -> {
                    when (response?.code()) {
                        200 -> {
                            //로그인 된 유저 데이터 제이슨으로 파싱하기
                            val str = response.body()?.string()
                            Log.d("로그", "HomeAccountActivity - retrofitInfoLoginedUser : ${str}")
                            val jsonObjectUser = str?.let { JSONObject(it) }
                            LoginedUserClient.nickname =
                                "${jsonObjectUser?.getString("nickname")}"

                            if (jsonObjectUser?.getString("sex").equals("T")) {
                                LoginedUserClient.sex = "남성"
                            } else
                                LoginedUserClient.sex = "여성"

                            LoginedUserClient.phoneNumber =
                                jsonObjectUser?.getString("phoneNumber")
                            LoginedUserClient.birthDay =
                                jsonObjectUser?.getString("birthDay")
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitInfoLogineduser : 통신 실패")
                }
            }
        })
    }
}