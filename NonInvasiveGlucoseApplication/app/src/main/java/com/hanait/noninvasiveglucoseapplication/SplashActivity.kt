package com.hanait.noninvasiveglucoseapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.db.PreferenceManager
import com.hanait.noninvasiveglucoseapplication.user.UserActivity
import com.hanait.noninvasiveglucoseapplication.util.Constants.prefs

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT:Long = 1000 //2초

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.statusBarColor = ContextCompat.getColor(this, R.color.iphone_gray_200)

        //sharedPreference에서 사용자 데이터 가져오기
        prefs = PreferenceManager(applicationContext)


        Handler().postDelayed({
            startActivity(Intent(this, UserActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }
}