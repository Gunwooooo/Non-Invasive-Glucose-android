package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeAnalysisBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment

class HomeAnalysisFragment : BaseFragment<FragmentHomeAnalysisBinding>(FragmentHomeAnalysisBinding::inflate), View.OnClickListener {
    private lateinit var textViewList : List<TextView>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }
    private fun init() {
        setViewPagerAndTabLayout()

        textViewList = listOf(binding.homeAnalysisBtn7day, binding.homeAnalysisBtn30day, binding.homeAnalysisBtn90day)
        binding.homeAnalysisBtn7day.setOnClickListener(this)
        binding.homeAnalysisBtn30day.setOnClickListener(this)
        binding.homeAnalysisBtn90day.setOnClickListener(this)

        //스테이터스바 색깔 변경
//        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.StatusBarColor)

    }

    //뷰페이저 탭레이아웃 연결
    private fun setViewPagerAndTabLayout() {
        binding.homeAnalysisViewPager.adapter = ThermometerViewPagerFragmentAdapter(requireActivity())
        binding.homeAnalysisViewPager.isUserInputEnabled = false //뷰페이저 스와이프 안되도록
        val tabTitles = listOf("체온", "심박수", "혈당")
        TabLayoutMediator(binding.homeAnalysisTabLayout, binding.homeAnalysisViewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }


    override fun onClick(v: View?) {
        when(v) {
            binding.homeAnalysisBtn7day -> {
                changeTextViewBackgroundColor(0)
            }
            binding.homeAnalysisBtn30day -> {
                changeTextViewBackgroundColor(1)
            }
            binding.homeAnalysisBtn90day -> {
                changeTextViewBackgroundColor(2)
            }
        }
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

    //toolbar 클릭 리스너
    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            android.R.id.home -> {
//                finish()
//            }
            R.id.home_toolbar_account -> {
//                val intent = Intent(this, HomeAccountActivity::class.java)
//                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}