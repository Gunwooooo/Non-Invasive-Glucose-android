package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.widget.ProgressBar
import androidx.fragment.app.Fragment


@SuppressLint("StaticFieldLeak")
object Constants {
    lateinit var mProgressBar: ProgressBar
    lateinit var mPrevFragment: Fragment

    lateinit var mUserData: UserData
}

data class UserData(var phoneNumber: String, var password: String, var birthDay: String, var sex: String)