package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.hanait.noninvasiveglucoseapplication.model.UserData


@SuppressLint("StaticFieldLeak")
object Constants {
    lateinit var mProgressBar: ProgressBar
    lateinit var mPrevFragment: Fragment

    lateinit var mUserData: UserData



    // 네이버 API 정보
    var NAVER_SERVICE_ID = "ncp:sms:kr:276214123809:hanait-noti"
    //String NAVER_SERVICE_ID = Uri.encode("ncp:sms:kr:276214123809:hanait-noti");
    var NAVER_SERVICE_SECRET_KEY = "ccf5e31ae7cb497caa96238d5a482425"
    var NAVER_ACCESS_KEY = "8qM8InIbLpyV8LC5jNaH"
    var NAVER_SECRET_KEY = "Gc1g48vw82OSiSziZsnHjxPHZSbXDPOxJbwP9v89"
    var NAVER_SMS_FROM_NUMBER = "0537453860"
}

