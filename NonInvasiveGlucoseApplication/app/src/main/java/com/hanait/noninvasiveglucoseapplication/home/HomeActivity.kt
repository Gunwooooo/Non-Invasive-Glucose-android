package com.hanait.noninvasiveglucoseapplication.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeBinding
import com.hanait.noninvasiveglucoseapplication.user.*
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity(), View.OnClickListener{


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
        //대쉬보드 프래그먼트 시랳ㅇ
        supportFragmentManager.beginTransaction().replace(R.id.home_frameId, HomeDashboardFragment()).commitAllowingStateLoss()

        binding.homeTextViewDashboard.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            //대쉬보드 하단 네비바 클릭
            binding.homeTextViewDashboard -> {
                setTextViewTitle("대쉬보드")
                changeFragment("HomeDashboardFragment")
            }
        }
    }

    //프래그먼트 이동 메서드
    fun changeFragment(fragmentName: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.setCustomAnimations(R.anim.right_to_center_anim, R.anim.center_to_left_anim, R.anim.right_to_center_anim, R.anim.center_to_left_anim)
        when(fragmentName) {
            "HomeDashboardFragment" -> fragmentTransaction.replace(R.id.home_frameId, HomeDashboardFragment()).commitAllowingStateLoss()
            "HomeThermometerFragment" -> fragmentTransaction.replace(R.id.home_frameId, HomeThermometerFragment()).commitAllowingStateLoss()
        }
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
            R.id.home_toolbar_bluetooth -> {
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

    //타이틀 설명 이름 변경하기
    fun setTextViewTitle(title: String) {
        binding.homeTextViewTitle.text = title
    }

    //바텀 네비바 숨기기
    fun setBottomNavVisible(visible: Boolean) {
        binding.homeCoordinatorLayout.isVisible = visible
    }
}