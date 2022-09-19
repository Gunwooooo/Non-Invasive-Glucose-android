package com.hanait.noninvasiveglucoseapplication.user

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityUserBinding
import com.hanait.noninvasiveglucoseapplication.util.Constants.prevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.progressBar
import kotlin.system.exitProcess

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private var waitTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.userProgressBar
        progressBar.indeterminateDrawable

        //toolbar 생성
        setSupportActionBar(binding.userToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = ""

        supportFragmentManager.beginTransaction().replace(R.id.user_frameId, UserSetPhoneNumberFragment()).commitAllowingStateLoss()
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

    private fun changePrevFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.user_frameId, prevFragment).commitAllowingStateLoss()
    }

    @Override
    override fun onBackPressed() {
        super.onBackPressed()
        changePrevFragment()
    }
}