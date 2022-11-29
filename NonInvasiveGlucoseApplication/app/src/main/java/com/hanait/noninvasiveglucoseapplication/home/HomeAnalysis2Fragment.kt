package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeAnalysis2Binding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment

class HomeAnalysis2Fragment : BaseFragment<FragmentHomeAnalysis2Binding>(FragmentHomeAnalysis2Binding::inflate), View.OnClickListener {
    private lateinit var textViewList : List<TextView>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        setViewPagerAndTabLayout()

        textViewList = listOf(binding.homeAnalysis2Btn7day, binding.homeAnalysis2Btn30day, binding.homeAnalysis2Btn90day)
        binding.homeAnalysis2Btn7day.setOnClickListener(this)
        binding.homeAnalysis2Btn30day.setOnClickListener(this)
        binding.homeAnalysis2Btn90day.setOnClickListener(this)
    }

    //뷰페이저 탭레이아웃 연결
    private fun setViewPagerAndTabLayout() {
        binding.homeAnalysis2ViewPager.adapter = Analysis2ViewPagerFragmentAdapter(requireActivity())
        binding.homeAnalysis2ViewPager.isUserInputEnabled = false //뷰페이저 스와이프 안되도록
        val tabTitles = listOf("체온", "심박수", "혈당")
        TabLayoutMediator(binding.homeAnalysis2TabLayout, binding.homeAnalysis2ViewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }



    //일, 월, 년 토글 버튼 구현
    private fun changeTextViewBackgroundColor(index : Int) {
        for(i in 0 until 3) {
            if(i == index) {
                textViewList[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.toss_blue_200))
                textViewList[i].setBackgroundResource(R.color.toss_blue_100)
                continue
            }
            textViewList[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.toss_black_500))
            textViewList[i].setBackgroundResource(R.color.white)
        }
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeAnalysis2Btn7day -> {
                changeTextViewBackgroundColor(0)
            }
            binding.homeAnalysis2Btn30day -> {
                changeTextViewBackgroundColor(1)
            }
            binding.homeAnalysis2Btn90day -> {
                changeTextViewBackgroundColor(2)
            }
        }
    }
}