package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeAnalysisBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment

class HomeAnalysisFragment : BaseFragment<FragmentHomeAnalysisBinding>(FragmentHomeAnalysisBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }
    private fun init() {
        setViewPagerAndTabLayout()

        //toolbar 표시
        val mActivity = activity as HomeActivity
        mActivity.setTitleVisible(false)

        //스테이터스바 색깔 변경
//        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.StatusBarColor)

    }

    private fun setViewPagerAndTabLayout() {
        binding.homeThermometerViewPager.adapter = ThermometerViewPagerFragmentAdapter(requireActivity())
        binding.homeThermometerViewPager.isUserInputEnabled = false //뷰페이저 스와이프 안되도록
        val tabTitles = listOf("체온", "심박수", "혈당")
        TabLayoutMediator(binding.homeThermometerTabLayout, binding.homeThermometerViewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
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