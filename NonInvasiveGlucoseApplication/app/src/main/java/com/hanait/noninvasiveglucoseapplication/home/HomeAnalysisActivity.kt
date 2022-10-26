package com.hanait.noninvasiveglucoseapplication.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeAnalysisBinding

class HomeAnalysisActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomeAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {


        setViewPagerAndTabLayout()


        //toolbar 표시
        setSupportActionBar(binding.homeThermometerToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "상세보기"

        //스테이터스바 색깔 변경
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.graph_thermometer_bar_100)


    }

    private fun setViewPagerAndTabLayout() {
        binding.homeThermometerViewPager.adapter = ThermometerViewPagerFragmentAdapter(this)
        binding.homeThermometerViewPager.isUserInputEnabled = false //뷰페이저 스와이프 안되도록
        val tabTitles = listOf("체온", "심박수", "혈당")
        TabLayoutMediator(binding.homeThermometerTabLayout, binding.homeThermometerViewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        //탭 번호 가져오기
        val tabNumber = intent.getIntExtra("TabNumber", 0)
        binding.homeThermometerTabLayout.getTabAt(tabNumber)?.select()

        
    }

    //toolbar 클릭 리스너
    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.home_toolbar_account -> {
//                val intent = Intent(this, HomeAccountActivity::class.java)
//                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_home, menu)
        return true
    }
}