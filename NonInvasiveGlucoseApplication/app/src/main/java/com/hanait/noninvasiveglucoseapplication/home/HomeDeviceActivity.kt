package com.hanait.noninvasiveglucoseapplication.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeDeviceBinding


class HomeDeviceActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeDeviceBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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