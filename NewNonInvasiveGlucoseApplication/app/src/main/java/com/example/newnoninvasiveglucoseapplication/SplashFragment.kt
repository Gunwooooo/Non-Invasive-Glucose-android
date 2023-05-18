package com.example.newnoninvasiveglucoseapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentSplashBinding
import com.example.newnoninvasiveglucoseapplication.db.PreferenceManager
import com.example.newnoninvasiveglucoseapplication.model.UserData
import com.example.newnoninvasiveglucoseapplication.retrofit.API.PHR_BASE_URL
import com.example.newnoninvasiveglucoseapplication.retrofit.API.PHR_PROFILE_BASE_URL
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.user.UserActivity
import com.example.newnoninvasiveglucoseapplication.user.UserSetConnectDeviceFragment
import com.example.newnoninvasiveglucoseapplication.user.UserSetPhoneNumberFragment
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import com.example.newnoninvasiveglucoseapplication.util.Constants._prefs
import com.example.newnoninvasiveglucoseapplication.util.CustomDialogManager
import com.example.newnoninvasiveglucoseapplication.util.LoginedUserClient

class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {
    private val customProgressDialog by lazy { CustomDialogManager(requireContext(), R.layout.common_progress_dialog, null, null) }
    private val SPLASH_TIME_OUT:Long = 2000 //2초

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
//        binding.editText.textChanges().subscribe {
//            Log.d("로그", "SplashFragment - onViewCreated : text : $it")
//        }
    }

    private fun init() {
        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.setBtnBackVisible(View.INVISIBLE)
        mActivity.setProgressDialogValueAndVisible(0, View.GONE)

        //글라이드로 스플래쉬 이미지 가져오기
        setImageViewWithGlide()

        //sharedPreference에서 사용자 데이터 가져오기
        _prefs = PreferenceManager(requireContext())

        //자동 로그인 체크
        when(_prefs.getBoolean("AUTO_LOGIN", false)) {
            true -> {
                val phoneNumber = _prefs.getString("USER_PHONENUMBER", "")
                val password = _prefs.getString("USER_PASSWORD", "")
                val userData = UserData("", phoneNumber, password, "", "")
                retrofitLoginUser(userData)
            }
            false -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    mActivity.changeFragmentTransaction(UserSetPhoneNumberFragment())
                }, SPLASH_TIME_OUT)
            }
        }
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(requireContext())
        glide.load(R.drawable.icon_color_splash_logo).into(binding.homeSplashImageViewSplashLogo)

    }
//    ///////////////////////////////////////////////////////////////////////////////////////////
    //자동 로그인 시 유저 데이터 가져오기
    private fun retrofitLoginUser(userData : UserData) {
    customProgressDialog.show(childFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.loginUser(userData, completion = {
                completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        //로그인 성공
                        200 -> {
                            //토큰 값 저장
                            LoginedUserClient.authorization = response.headers()["Authorization"]
                            LoginedUserClient.refreshToken = response.headers()["refresh_token"]
                            Log.d("로그", "SplashFragment - retrofitLoginUser : AT : ${LoginedUserClient.authorization}")
                            Log.d("로그", "SplashFragment - retrofitLoginUser : RT : ${LoginedUserClient.refreshToken}")
                            Handler(Looper.getMainLooper()).postDelayed({
                                Toast.makeText(requireContext(), "자동 로그인 성공", Toast.LENGTH_SHORT).show()
                                val mActivity = activity as UserActivity
                                mActivity.changeFragmentTransaction(UserSetConnectDeviceFragment())
                            }, SPLASH_TIME_OUT)
                        }
                        else -> {
                            //자동 로그인 초기화
                            _prefs.setString("USER_PHONENUMBER", "")
                            _prefs.setString("USER_PASSWORD", "")
                            _prefs.setBoolean("AUTO_LOGIN", false)
                            val mActivity = activity as UserActivity
                            mActivity.changeFragmentTransaction(UserSetPhoneNumberFragment())
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "UserCheckPasswordFragment - retrofitLoginUser : 로그인 통신 실패")
                }
            }
        })
    }
}