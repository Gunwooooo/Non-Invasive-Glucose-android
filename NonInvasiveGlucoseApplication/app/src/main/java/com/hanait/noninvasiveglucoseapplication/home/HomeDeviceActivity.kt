package com.hanait.noninvasiveglucoseapplication.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeDeviceBinding


class HomeDeviceActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityHomeDeviceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.homeAccountBtnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeAccountBtnBack -> {
                finish()
            }
        }
    }
}