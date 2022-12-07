package com.hanait.noninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import com.hanait.noninvasiveglucoseapplication.util.CustomCalendarManager
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogManager
import java.util.*

class HomeAccountActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityHomeAccountBinding
    lateinit var datePickerDialogListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        //toolbar 표시
        setSupportActionBar(binding.homeAccountToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        //로그인된 유저 데이터 가져오기
        retrofitInfoLoginedUser()

        //생년월일 변경 리스너
        setDatePickerDialogListener()

        binding.homeAccountTextViewModifySex.setOnClickListener(this)
        binding.homeAccountTextViewModifyBirthday.setOnClickListener(this)
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
            binding.homeAccountTextViewModifySex -> {
                showModifySexDialog()
            }
            binding.homeAccountTextViewModifyBirthday -> {
                makeDatePickerDialog()
            }
        }
    }

    //캘린더 피커 다이어로그 리스너 설정
    @SuppressLint("SetTextI18n")
    private fun setDatePickerDialogListener() {
        datePickerDialogListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.homeAccountTextViewModifyBirthday.text = "${year}년 ${month+1}월 ${dayOfMonth}일"
            }
    }

    //달력 날짜 선택 다이어로그 생성
    private fun makeDatePickerDialog() {
        val gregorianCalendar = GregorianCalendar()
        val year = gregorianCalendar.get(Calendar.YEAR)
        val month = gregorianCalendar.get(Calendar.MONTH)
        val dayOfMonth = gregorianCalendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, datePickerDialogListener, year, month, dayOfMonth).show()
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
        customDialog.setAccountModifySexDialogListener(object : CustomDialogManager.AccountModifySexDialogListener{
            override fun onPositiveClicked() {
                //회원정보 수정 기능 추가 필요 ( 성별 )
                Log.d("로그", "HomeProtectorFragment - onPositiveClicked : 예 버튼 클릭")
                customDialog.dismiss()
            }
            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(supportFragmentManager, "home_account_modify_sex_dialog")
    }
}