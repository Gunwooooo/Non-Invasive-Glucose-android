package com.hanait.noninvasiveglucoseapplication.home

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
        supportFragmentManager.beginTransaction().replace(R.id.home_frameId, HomeDashboardFragment()).commitAllowingStateLoss()

        //스테이터스바 색깔 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.iphone_gray_200)

        //바텀 네비게이션 리스너
        binding.homeBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottomNav_dashboard -> {
                    changeFragmentTransactionNoAnimation(HomeDashboardFragment())
                }

                R.id.bottomNav_protector ->
                    changeFragmentTransactionNoAnimation(HomeProtectorFragment())
                else -> false
            }
        }
    }

    //애니메이션 있게 프래그먼트 전환
    fun changeFragmentTransactionWithAnimation(fragment: Fragment) : Boolean {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.right_to_center_anim, R.anim.center_to_left_anim, R.anim.right_to_center_anim, R.anim.center_to_left_anim)
        fragmentTransaction.replace(R.id.home_frameId, fragment).commitAllowingStateLoss()

        //화면 맨 위로 올리기(속도 조절)
        binding.homeBouncyNestedScrollView.post(Runnable {
            ObjectAnimator.ofInt( binding.homeBouncyNestedScrollView, "scrollY", 0).setDuration(300).start()
        })
        return true
    }

    //애니메이션 없이 프래그먼트로 이동
    fun changeFragmentTransactionNoAnimation(fragment: Fragment) : Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.home_frameId, fragment).commitAllowingStateLoss()

        //화면 맨 위로 올리기(속도 조절)
        //화면 맨 위로 올리기(속도 조절)
        binding.homeBouncyNestedScrollView.post(Runnable {
            ObjectAnimator.ofInt( binding.homeBouncyNestedScrollView, "scrollY", 0).setDuration(300).start()
        })
        return true
    }

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