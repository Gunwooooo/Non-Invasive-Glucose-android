package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.hanait.noninvasiveglucoseapplication.db.PreferenceManager
import com.hanait.noninvasiveglucoseapplication.model.UserData



object Constants {
    //회원가입 시 데이터 부분 저장
    lateinit var _userData: UserData

    //자동 로그인 변수 저장
    lateinit var prefs: PreferenceManager
    
    // 네이버 API 정보
    const val NAVER_SERVICE_ID = "ncp:sms:kr:276214123809:hanait-noti"
    //String NAVER_SERVICE_ID = Uri.encode("ncp:sms:kr:276214123809:hanait-noti");
    val NAVER_SERVICE_SECRET_KEY = "ccf5e31ae7cb497caa96238d5a482425"
//    var NAVER_ACCESS_KEY = "8qM8InIbLpyV8LC5jNaH"
    const val NAVER_ACCESS_KEY = "93D6C30F9DF6A199DD16"
//    var NAVER_SECRET_KEY = "Gc1g48vw82OSiSziZsnHjxPHZSbXDPOxJbwP9v89"
    const val NAVER_SECRET_KEY = "414021434924B06DEA23C608C08258A2E8E523DB"
    const val NAVER_SMS_FROM_NUMBER = "0537453860"


    //프로필 사진 경로
    const val PROFILE_IMAGE_NAME = "_profile.png"
}

