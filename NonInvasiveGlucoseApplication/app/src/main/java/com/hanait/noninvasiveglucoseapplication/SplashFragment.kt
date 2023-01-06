package com.hanait.noninvasiveglucoseapplication

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentSplashBinding
import com.hanait.noninvasiveglucoseapplication.db.PreferenceManager
import com.hanait.noninvasiveglucoseapplication.model.UserData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.user.UserActivity
import com.hanait.noninvasiveglucoseapplication.user.UserSetConnectDeviceFragment
import com.hanait.noninvasiveglucoseapplication.user.UserSetPhoneNumberFragment
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.prefs
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient

class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    private val SPLASH_TIME_OUT:Long = 2000 //2초

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.setBtnBackVisible(View.INVISIBLE)
        mActivity.setProgressDialogValueAndVisible(0, View.GONE)

        //sharedPreference에서 사용자 데이터 가져오기
        prefs = PreferenceManager(requireContext())

        //자동 로그인 체크
        when(prefs.getBoolean("AUTO_LOGIN", false)) {
            true -> {
                val phoneNumber = prefs.getString("USER_PHONENUMBER", "")
                val password = prefs.getString("USER_PASSWORD", "")
                val userData = UserData("", phoneNumber, password, "", "")
                retrofitLoginUser(userData)
            }
            false -> {
                Handler().postDelayed({
                    mActivity.changeFragmentTransaction(UserSetPhoneNumberFragment())
                }, SPLASH_TIME_OUT)
            }
        }
    }

//    ///////////////////////////////////////////////////////////////////////////////////////////
    private fun retrofitLoginUser(userData : UserData) {
        RetrofitManager.instance.loginUser(userData, completion = {
                completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        //로그인 성공
                        200 -> {
                            //토큰 값 저장
                            LoginedUserClient.authorization = response.headers()["Authorization"]
                            Handler().postDelayed({
                                Toast.makeText(requireContext(), "자동 로그인 성공!", Toast.LENGTH_SHORT).show()
                                val mActivity = activity as UserActivity
                                mActivity.changeFragmentTransaction(UserSetConnectDeviceFragment())
                            }, SPLASH_TIME_OUT)
                        }
                        else -> {
                            //자동 로그인 초기화
                            prefs.setString("USER_PHONENUMBER", "")
                            prefs.setString("USER_PASSWORD", "")
                            prefs.setBoolean("AUTO_LOGIN", false)
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