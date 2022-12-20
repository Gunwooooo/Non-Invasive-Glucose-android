package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeThermometerBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment


class HomeThermometerFragment : BaseFragment<FragmentHomeThermometerBinding>(FragmentHomeThermometerBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        binding.homeThermometerNtsAverage.setTabIndex(0, true)
    }
}