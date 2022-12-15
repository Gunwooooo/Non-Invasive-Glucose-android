package com.hanait.noninvasiveglucoseapplication.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeBinding
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
//        //툴바 보이도록 설정
//        setSupportActionBar(binding.homeToolbar)
//        supportActionBar?.title = ""
        //대쉬보드 프래그먼트 실행


        supportFragmentManager.beginTransaction().replace(R.id.home_frameId, HomeDashboardFragment()).commitAllowingStateLoss()

        //스테이터스바 색깔 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        //바텀 네비게이션 리스너
        binding.homeBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottomNav_dashboard ->
                    changeFragmentTransaction(HomeDashboardFragment())
                R.id.bottomNav_analysis ->
                    changeFragmentTransaction(HomeAnalysisFragment())
                R.id.bottomNav_analysis2 ->
                    changeFragmentTransaction(HomeAnalysis2Fragment())
                R.id.bottomNav_protector ->
                    changeFragmentTransaction(HomeProtectorFragment())
                else -> false
            }
        }
    }

    //프래그먼트 전환
    private fun changeFragmentTransaction(fragment: Fragment) : Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.home_frameId, fragment).commitAllowingStateLoss()
        return true
    }

//    @Override
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.appbar_home, menu)
//        return true
//    }
//
//    //툴바 클릭 리스너
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item.itemId) {
//            R.id.home_toolbar_account -> {
//                val intent = Intent(this, HomeAccountActivity::class.java)
//                startActivity(intent)
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    //뒤로가기 키 눌렸을 때 종료
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
}