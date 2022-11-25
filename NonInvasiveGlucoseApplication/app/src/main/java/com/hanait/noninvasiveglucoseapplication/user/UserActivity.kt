package com.hanait.noninvasiveglucoseapplication.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityUserBinding
import com.hanait.noninvasiveglucoseapplication.util.Constants._prevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._progressBar
import kotlin.system.exitProcess

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private var waitTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _progressBar = binding.userProgressBar
        _progressBar.indeterminateDrawable

        //toolbar 생성
        setSupportActionBar(binding.userToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = ""

        supportFragmentManager.beginTransaction().replace(R.id.user_frameId, UserSetPhoneNumberFragment()).commitAllowingStateLoss()
    }

    //프래그먼트 이동 메서드
    fun changeFragment(fragmentName: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.right_to_center_anim, R.anim.center_to_left_anim, R.anim.right_to_center_anim, R.anim.center_to_left_anim)
        when(fragmentName) {
            "UserSetAuthorizationFragment" -> fragmentTransaction.replace(R.id.user_frameId, UserSetAuthorizationFragment()).commitAllowingStateLoss()
            "UserSetNickNameFragment" -> fragmentTransaction.replace(R.id.user_frameId, UserSetNickNameFragment()).commitAllowingStateLoss()
            "UserSetPasswordFragment" -> fragmentTransaction.replace(R.id.user_frameId, UserSetPasswordFragment()).commitAllowingStateLoss()
            "UserSetBirthdayFragment" -> fragmentTransaction.replace(R.id.user_frameId, UserSetBirthdayFragment()).commitAllowingStateLoss()
            "UserSetSexFragment" -> fragmentTransaction.replace(R.id.user_frameId, UserSetSexFragment()).commitAllowingStateLoss()
            "UserSetAgreementFragment" -> fragmentTransaction.replace(R.id.user_frameId, UserSetAgreementFragment()).commitAllowingStateLoss()
            "Agreement1Fragment" -> fragmentTransaction.replace(R.id.user_frameId, Agreement1Fragment()).commitAllowingStateLoss()
            "Agreement2Fragment" -> fragmentTransaction.replace(R.id.user_frameId, Agreement2Fragment()).commitAllowingStateLoss()
            "UserSetConnectDeviceFragment" -> fragmentTransaction.replace(R.id.user_frameId, UserSetConnectDeviceFragment()).commitAllowingStateLoss()
            "ConnectionLoadingFragment" -> fragmentTransaction.replace(R.id.user_frameId, ConnectionLoadingFragment()).commitAllowingStateLoss()
        }
    }

    //toolbar 클릭 리스너
    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                changePrevFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun changePrevFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.left_to_center_anim, R.anim.center_to_right_anim, R.anim.left_to_center_anim, R.anim.center_to_right_anim)
        fragmentTransaction.replace(R.id.user_frameId, _prevFragment).commitAllowingStateLoss()
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