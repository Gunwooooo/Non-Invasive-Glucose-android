package com.hanait.noninvasiveglucoseapplication.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeAccountBinding
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogManager

class HomeAccountActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityHomeAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        //toolbar 표시
        setSupportActionBar(binding.homeAccountToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        //로그인된 유저 데이터 가져오기
        retrofitInfoLoginedUser()

        binding.homeAccountLayoutModifySex.setOnClickListener(this)
    }

    //toolbar 클릭 리스너
    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeAccountLayoutModifySex -> {
                showModifySexDialog()
            }
        }
    }

    private fun retrofitInfoLoginedUser() {
        RetrofitManager.instance.infoLoginedUser(completion = {
            completionResponse, response -> 
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        200 -> {
                            Log.d("로그", "HomeAccountActivity - retrofitInfoLogineduser : response body : ${response.body()}")
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitInfoLogineduser : 통신 실패")
                }
            }
        })
    }

    //보호자 삭제 다이어로그 호출
    private fun showModifySexDialog() {
        Log.d("로그", "HomeAccountActivity - showModifySexDialog : 다이어로그 호출됨")
        val customDialog = CustomDialogManager(R.layout.home_account_modify_sex_dialog)

        val maleButton = findViewById<Button>(R.id.homeAccountModifySexDialog_btn_male)
        val femaleButton = findViewById<Button>(R.id.homeAccountModifySexDialog_btn_female)

        customDialog.setAccountModifySexDialogListener(object : CustomDialogManager.AccountModifySexDialogListener{
            override fun onPositiveClicked() {
                //회원정보 수정 기능 추가 필요 ( 성별 )
                Log.d("로그", "HomeProtectorFragment - onPositiveClicked : 예 버튼 클릭")
                customDialog.dismiss()
            }
            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
            override fun onMaleBtnClicked() {
                changeButtonColor(maleButton, femaleButton)
            }
            override fun onFemaleBtnClicked() {
                changeButtonColor(femaleButton, maleButton)
            }
        })
        customDialog.show(supportFragmentManager, "home_account_modify_sex_dialog")
    }

    //버튼 토글 기능
    private fun changeButtonColor(onButton: Button, offButton: Button) {
        onButton.isEnabled = false
        onButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        offButton.isEnabled = true
        offButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.toss_black_500))
    }
}