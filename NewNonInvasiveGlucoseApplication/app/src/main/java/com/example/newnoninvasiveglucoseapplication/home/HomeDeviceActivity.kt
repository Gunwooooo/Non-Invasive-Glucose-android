package com.example.newnoninvasiveglucoseapplication.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import com.bumptech.glide.Glide
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.ActivityHomeDeviceBinding
import com.example.newnoninvasiveglucoseapplication.util.Constants._prefs


class HomeDeviceActivity : AppCompatActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private val binding by lazy { ActivityHomeDeviceBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        
        //글라이드로 이미지 불러오기
        setImageViewWithGlide()
        
        binding.homeDeviceBtnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeDeviceBtnBack -> {
                finish()
            }
        }
    }

    //스위치 체인지 리스너
    override fun onCheckedChanged(v: CompoundButton?, boolean: Boolean) {
        when(v) {
            binding.homeDeviceSwitch -> {
                Log.d("로그", "HomeDeviceActivity - onCheckedChanged : 스위치 : $boolean")
                _prefs.setBoolean("AUTO_DEVICE_AWAKE", boolean)
            }
        }
    }
    

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(this)
        glide.load(R.drawable.icon_color_device).into(binding.homeDeviceImageViewDevice)
        glide.load(R.drawable.ic_baseline_arrow_back_24).into(binding.homeDeviceImageViewBack)
    }

}