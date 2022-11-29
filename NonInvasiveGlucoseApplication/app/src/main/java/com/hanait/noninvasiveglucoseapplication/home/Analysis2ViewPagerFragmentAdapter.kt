package com.hanait.noninvasiveglucoseapplication.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class Analysis2ViewPagerFragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val fragmentList = listOf(HomeAnalysis2ThermometerFragment(), HomeAnalysis2HeartFragment(), HomeAnalysis2GlucoseFragment())

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position] as Fragment
    }
}