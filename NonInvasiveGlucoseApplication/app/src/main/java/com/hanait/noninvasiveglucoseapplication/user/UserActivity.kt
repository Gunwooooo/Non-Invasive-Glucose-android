package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityUserBinding
import kotlin.system.exitProcess

class UserActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserBinding
    private var waitTime = 0L
    private lateinit var prevFragment: Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        //프로그래스 바 설정
        binding.userProgressBar.indeterminateDrawable

        //prevFragment 초기화
        prevFragment = UserSetPhoneNumberFragment()

        supportFragmentManager.beginTransaction().replace(R.id.user_frameId, UserSetPhoneNumberFragment()).commitAllowingStateLoss()

        binding.userBtnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            //뒤로가기 클릭
            binding.userBtnBack -> {
                changePrevFragment()
            }
        }
    }
    
    //프래그먼트 이동 메서드
    fun changeFragmentTransaction(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.right_to_center_anim, R.anim.center_to_left_anim, R.anim.right_to_center_anim, R.anim.center_to_left_anim)
        fragmentTransaction.replace(R.id.user_frameId, fragment).commitAllowingStateLoss()
    }

    //뒤로가기 버튼 비져빌리티 설정
    fun setBtnBackVisible(visibility: Int) {
        binding.userBtnBack.visibility = visibility
    }

    //이전 프래그먼트 저장
    fun setPrevFragment(fragment: Fragment) {
        prevFragment = fragment
    }

    //프로그래스바 게이지 및 비져빌리티 설정
    fun setProgressDialogValueAndVisible(value: Int, visibility: Int) {
        binding.userProgressBar.progress = value
        binding.userProgressBar.visibility = visibility
    }

    //이전 프래그먼트로 이동
    fun changePrevFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.left_to_center_anim, R.anim.center_to_right_anim, R.anim.left_to_center_anim, R.anim.center_to_right_anim)
        fragmentTransaction.replace(R.id.user_frameId, prevFragment).commitAllowingStateLoss()
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


}