package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeThermometerBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment

class HomeThermometerFragment : BaseFragment<FragmentHomeThermometerBinding>(FragmentHomeThermometerBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        val mActivity = activity as HomeActivity
        mActivity.setTextViewTitle("체온")
//        mActivity.setBottomNavVisible(false)

        setViewPagerAndTabLayout()
    }


    
    private fun setViewPagerAndTabLayout() {
        binding.homeThermometerViewPager.adapter = ThermometerViewPagerFragmentAdapter(requireActivity())

        val tabTitles = listOf("일", "월", "년")
        TabLayoutMediator(binding.homeThermometerTabLayout, binding.homeThermometerViewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}