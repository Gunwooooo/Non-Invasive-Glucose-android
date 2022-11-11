package com.hanait.noninvasiveglucoseapplication.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.navigation.NavigationBarView
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeBinding
import com.hanait.noninvasiveglucoseapplication.user.*
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {


    private lateinit var binding: ActivityHomeBinding
    private var waitTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        //툴바 보이도록 설정
        setSupportActionBar(binding.homeToolbar)
        supportActionBar?.title = ""
        //대쉬보드 프래그먼트 실행
        supportFragmentManager.beginTransaction().replace(R.id.home_frameId, HomeDashboardFragment()).commitAllowingStateLoss()

        //스테이터스바 색깔 변경
        this.window.statusBarColor = ContextCompat.getColor(this, R.color.StatusBarColor)

        //바텀 네비게이션 리스너
        binding.homeBottomNav.setOnItemSelectedListener(object: NavigationBarView.OnItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.bottomNav_dashboard -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.home_frameId, HomeDashboardFragment())
                            .commitAllowingStateLoss()
                        return true
                    }
                    R.id.bottomNav_analysis -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.home_frameId, HomeAnalysisFragment())
                            .commitAllowingStateLoss()
                        return true
                    }
                    R.id.bottomNav_protector -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.home_frameId, HomeProtectorFragment())
                            .commitAllowingStateLoss()
                        return true
                    }
                    else -> return false
                }
            }
        })
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.home_toolbar_account -> {
//                val intent = Intent(this, HomeAccountActivity::class.java)
//                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Override
    override fun onBackPressed() {
        if(System.currentTimeMillis() - waitTime >=1500 ) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAffinity()
            exitProcess(0)
        }
    }

    fun setTitleVisible(flag: Boolean, title: String) {
        binding.homeTextViewTitle.text = title
        if(flag)
            binding.homeTextViewTitle.visibility = View.VISIBLE
        else binding.homeTextViewTitle.visibility = View.GONE
    }
}