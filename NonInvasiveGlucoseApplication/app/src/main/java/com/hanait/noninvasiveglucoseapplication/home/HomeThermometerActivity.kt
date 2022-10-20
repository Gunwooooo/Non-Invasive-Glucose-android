package com.hanait.noninvasiveglucoseapplication.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.tabs.TabLayoutMediator
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeThermometerBinding

class HomeThermometerActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomeThermometerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeThermometerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setViewPagerAndTabLayout()


        //toolbar 표시
        setSupportActionBar(binding.homeThermometerToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "체온"
    }

    private fun setViewPagerAndTabLayout() {
        binding.homeThermometerViewPager.adapter = ThermometerViewPagerFragmentAdapter(this)

        val tabTitles = listOf("일", "월", "년")
        TabLayoutMediator(binding.homeThermometerTabLayout, binding.homeThermometerViewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
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
            R.id.home_toolbar_bluetooth -> {
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