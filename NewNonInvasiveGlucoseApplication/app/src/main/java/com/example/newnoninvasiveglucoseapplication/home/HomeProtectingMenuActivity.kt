package com.example.newnoninvasiveglucoseapplication.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityHomeProtectingMenuBinding
import com.example.newnoninvasiveglucoseapplication.model.ProtectorData
import java.util.*

class HomeProtectingMenuActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeProtectingMenuBinding.inflate(layoutInflater) }
    private lateinit var protectingData: ProtectorData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() {

        //스테이터스바 색상 변경
        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.toss_black_600)
        window.decorView.systemUiVisibility
        //글라이드로 이미지 불러오기
        setImageViewWithGlide()

        //보호 대상자 정보 받기
        val id = intent.getIntExtra("protectingDataId", 0)
        val nickname = intent.getStringExtra("protectingDataNickname")!!
        val phoneNumber = intent.getStringExtra("protectingDataPhoneNumber")!!
        val birthDay = intent.getStringExtra("protectingDataBirthDay")!!
        val sex = intent.getStringExtra("protectingDataSex")!!
        protectingData = ProtectorData(id, nickname, phoneNumber, birthDay, sex)
        Log.d("로그", "HomeProtectingMenuActivity - init : 보호 대상자 정보 :  $protectingData")

        binding.homeProtectingMenuTextViewNickname.text = nickname
        binding.homeProtectingMenuTextViewAge.text = changeBirthDayToAge(birthDay)
        binding.homeProtectingMenuTextViewSex.text = changeSexToString(sex)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeProtectingMenuLayoutThermometer -> {

            }
            binding.homeProtectingMenuLayoutThermometerAnalysis -> {

            }
            binding.homeProtectingMenuLayoutHeart -> {

            }
            binding.homeProtectingMenuLayoutHeartAnalysis -> {

            }
            binding.homeProtectingMenuLayoutGlucose -> {

            }
            binding.homeProtectingMenuLayoutGlucoseAnalysis -> {

            }

            binding.homeProtectingMenuBtnBack -> {
                finish()
            }
        }
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(this)
        glide.load(R.drawable.icon_color_profile).into(binding.homeProtectingMenuImageViewProfile)
        glide.load(R.drawable.ic_baseline_arrow_back_24).into(binding.homeProtectingMenuImageViewBack)
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
        return if(sex == "T") "남성"
        else "여성"
    }

}