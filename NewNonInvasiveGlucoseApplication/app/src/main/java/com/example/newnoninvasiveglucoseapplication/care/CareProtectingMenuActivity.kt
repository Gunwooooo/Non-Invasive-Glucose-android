package com.example.newnoninvasiveglucoseapplication.care

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityCareProtectingMenuBinding

import com.example.newnoninvasiveglucoseapplication.model.ProtectorData
import com.example.newnoninvasiveglucoseapplication.retrofit.API
import com.example.newnoninvasiveglucoseapplication.util.Constants
import com.example.newnoninvasiveglucoseapplication.util.LoginedUserClient
import java.util.*

class CareProtectingMenuActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityCareProtectingMenuBinding.inflate(layoutInflater) }
    private lateinit var protectingData: ProtectorData
    
    companion object {
        var _protectingPhoneNumber: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        //스테이터스바 색상 변경
//        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.toss_black_600)

        //보호 대상자 정보 받기
        val id = intent.getIntExtra("protectingDataId", 0)
        val nickname = intent.getStringExtra("protectingDataNickname")!!
        val phoneNumber = intent.getStringExtra("protectingDataPhoneNumber")!!
        val birthDay = intent.getStringExtra("protectingDataBirthDay")!!
        val sex = intent.getStringExtra("protectingDataSex")!!
        protectingData = ProtectorData(id, nickname, phoneNumber, birthDay, sex)
        Log.d("로그", "HomeProtectingMenuActivity - init : 보호 대상자 정보 :  $protectingData")

        //전역변수 저장
        _protectingPhoneNumber = phoneNumber

        //글라이드로 이미지 불러오기
        setImageViewWithGlide()

        binding.careProtectingMenuTextViewNickname.text = nickname
        binding.careProtectingMenuTextViewAge.text = changeBirthDayToAge(birthDay)
        binding.careProtectingMenuTextViewSex.text = changeSexToString(sex)

        binding.careProtectingMenuLayoutThermometer.setOnClickListener(this)
        binding.careProtectingMenuLayoutThermometerAnalysis.setOnClickListener(this)
        binding.careProtectingMenuLayoutHeart.setOnClickListener(this)
        binding.careProtectingMenuLayoutHeartAnalysis.setOnClickListener(this)
        binding.careProtectingMenuLayoutGlucose.setOnClickListener(this)
        binding.careProtectingMenuLayoutGlucoseAnalysis.setOnClickListener(this)
        binding.careProtectingMenuBtnBack.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when(v) {
            binding.careProtectingMenuLayoutThermometer -> {
                val intent = Intent(applicationContext, CareThermometerFullChartActivity::class.java)
                startActivity(intent)
            }
            binding.careProtectingMenuLayoutThermometerAnalysis -> {
                val intent = Intent(applicationContext, CareThermometerAnalysisActivity::class.java)
                startActivity(intent)
            }
            binding.careProtectingMenuLayoutHeart -> {
                val intent = Intent(applicationContext, CareHeartFullChartActivity::class.java)
                startActivity(intent)
            }
            binding.careProtectingMenuLayoutHeartAnalysis -> {
                val intent = Intent(applicationContext, CareHeartAnalysisActivity::class.java)
                startActivity(intent)
            }
            binding.careProtectingMenuLayoutGlucose -> {
                val intent = Intent(applicationContext, CareGlucoseFullChartActivity::class.java)
                startActivity(intent)
            }
            binding.careProtectingMenuLayoutGlucoseAnalysis -> {
                val intent = Intent(applicationContext, CareGlucoseAnalysisActivity::class.java)
                startActivity(intent)
            }

            binding.careProtectingMenuBtnBack -> {
                finish()
            }
        }
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(this)
        glide.load(R.drawable.icon_color_profile).into(binding.careProtectingMenuImageViewProfile)
        glide.load(R.drawable.ic_baseline_arrow_back_24).into(binding.careProtectingMenuImageViewBack)

        //프로필 이미지 가져오기
        val sb = StringBuilder()
        sb.append(API.PHR_PROFILE_BASE_URL).append(_protectingPhoneNumber).append(Constants.PROFILE_IMAGE_NAME)
        glide.load(sb.toString()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
            .placeholder(R.drawable.icon_color_profile).circleCrop().into(binding.careProtectingMenuImageViewProfile)


        glide.load(R.drawable.icon_color_thermometer).into(binding.careProtectingMenuImageViewThermometer)
        glide.load(R.drawable.icon_color_thermometer_analysis).into(binding.careProtectingMenuImageViewThermometerAnalysis)
        glide.load(R.drawable.icon_color_heart).into(binding.careProtectingMenuImageViewHeart)
        glide.load(R.drawable.icon_color_heart_analysis).into(binding.careProtectingMenuImageViewHeartAnalysis)
        glide.load(R.drawable.icon_color_glucose).into(binding.careProtectingMenuImageViewGlucose)
        glide.load(R.drawable.icon_color_glucose_analysis).into(binding.careProtectingMenuImageViewGlucoseAnalysis)
    }

    //생년월일로 나이 계산
    private fun changeBirthDayToAge(birthDay: String) : String {
        if(birthDay.length <= 5 ) return ""
        val userYear = birthDay.substring(0, 4).toInt()
        val curYear = GregorianCalendar().get(Calendar.YEAR)
        return "${curYear - userYear}세"
    }

    //성별 T,f -> 남성 여성
    private fun changeSexToString(sex: String) : String {
        return if(sex == "T") "남"
        else "여"
    }

    override fun onDestroy() {
        super.onDestroy()

        _protectingPhoneNumber = null
    }
}