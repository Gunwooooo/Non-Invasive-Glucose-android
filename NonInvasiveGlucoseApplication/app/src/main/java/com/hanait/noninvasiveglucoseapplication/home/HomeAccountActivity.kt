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
import com.hanait.noninvasiveglucoseapplication.model.UserData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.CustomCalendarManager
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogManager
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class HomeAccountActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityHomeAccountBinding
    lateinit var datePickerDialogListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        //로그인된 유저 데이터 가져오기
        retrofitInfoLoginedUser()

        //생년월일 변경 리스너
        setDatePickerDialogListener()

        binding.homeAccountTextViewModifyNickname.setOnClickListener(this)
        binding.homeAccountLayoutModifySex.setOnClickListener(this)
        binding.homeAccountLayoutModifyBirthday.setOnClickListener(this)
        binding.homeAccountBtnModifyPassword.setOnClickListener(this)
        binding.homeAccountBtnDeleteUser.setOnClickListener(this)
        binding.homeAccountBtnLogoutUser.setOnClickListener(this)
        binding.homeAccountBtnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.homeAccountBtnBack -> {
                finish()
            }
            binding.homeAccountTextViewModifyNickname -> {
                showModifyNicknameDialog()
            }
            binding.homeAccountLayoutModifySex -> {
                showModifySexDialog()
            }
            binding.homeAccountLayoutModifyBirthday -> {
                makeDatePickerDialog()
            }
            binding.homeAccountBtnModifyPassword -> {
                showModifyPasswordDialog()
            }
            binding.homeAccountBtnDeleteUser -> {
                showDeleteUserDialog()
            }
            binding.homeAccountBtnLogoutUser -> {
                showLogoutUserDialog()
            }
        }
    }

    //캘린더 피커 다이어로그 리스너 설정
    @SuppressLint("SetTextI18n")
    private fun setDatePickerDialogListener() {
        datePickerDialogListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                retrofitEditLoginedUser()
                binding.homeAccountTextViewBirthday.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
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

    //닉네임 변경 다이어로그 호출
    private fun showModifyNicknameDialog() {
        val customDialog = CustomDialogManager(R.layout.home_account_modify_nickname_dialog)
        customDialog.setTwoButtonDialogListener(object : CustomDialogManager.TwoButtonDialogListener {
            override fun onPositiveClicked() {
                //회원정보 수정 기능
                retrofitEditLoginedUser()
                customDialog.dismiss()
            }
            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(supportFragmentManager, "home_account_modify_nickname_dialog")
    }

    //보호자 삭제 다이어로그 호출
    private fun showModifySexDialog() {
        Log.d("로그", "HomeAccountActivity - showModifySexDialog : 다이어로그 호출됨")
        val customDialog = CustomDialogManager(R.layout.home_account_modify_sex_dialog)
        customDialog.setTwoButtonDialogListener(object : CustomDialogManager.TwoButtonDialogListener {
            override fun onPositiveClicked() {
                //회원정보 수정 기능 추가 필요 ( 성별 )
                retrofitEditLoginedUser()
                customDialog.dismiss()
            }

            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(supportFragmentManager, "home_account_modify_sex_dialog")
    }

    //비밀번호 변경 다이어로그 출력
    private fun showModifyPasswordDialog() {
        val customDialog = CustomDialogManager(R.layout.home_account_modify_password_dialog)
        customDialog.setTwoButtonDialogListener(object : CustomDialogManager.TwoButtonDialogListener {
            override fun onPositiveClicked() {
                //비밀번호 변경 확인 및 수정 retrofit 필요
                customDialog.dismiss()
            }

            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(supportFragmentManager, "home_account_modify_password_dialog")
    }

    //회원 탈퇴 다이어로그 출력
    private fun showDeleteUserDialog() {
        val customDialog = CustomDialogManager(R.layout.home_account_delete_user_dialog)
        customDialog.setTwoButtonDialogListener(object : CustomDialogManager.TwoButtonDialogListener {
            override fun onPositiveClicked() {
                //회원탈퇴 retrofit 통신 필요
                retrofitDeleteLoginedUser()
                customDialog.dismiss()
            }

            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(supportFragmentManager, "home_account_modify_password_dialog")
    }

    //로그아웃 다이어로그 출력
    private fun showLogoutUserDialog() {
        val customDialog = CustomDialogManager(R.layout.home_account_logout_user_dialog)
        customDialog.setTwoButtonDialogListener(object : CustomDialogManager.TwoButtonDialogListener {
            override fun onPositiveClicked() {
                //로그아웃 retrofit 통신 필요
                retrofitLogoutUser()
                customDialog.dismiss()
            }

            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(supportFragmentManager, "home_account_modify_password_dialog")
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //로그인 된 회원 정보 가져오기
    @SuppressLint("SetTextI18n")
    private fun retrofitInfoLoginedUser() {
        RetrofitManager.instance.infoLoginedUser(completion = { completionResponse, response ->
            when (completionResponse) {
                CompletionResponse.OK -> {
                    when (response?.code()) {
                        200 -> {
                            //로그인 된 유저 데이터 제이슨으로 파싱하기
                            val str = response.body()?.string()
                            Log.d("로그", "HomeAccountActivity - retrofitInfoLoginedUser : ${str}")
                            val jsonObjectUser = str?.let { JSONObject(it) }
                            binding.homeAccountTextViewNickname.text =
                                "${jsonObjectUser?.getString("nickname")}님"

                            if (jsonObjectUser?.getString("sex").equals("T")) {
                                binding.homeAccountTextViewSex.text = "남성"
                            } else
                                binding.homeAccountTextViewSex.text = "여성"

                            binding.homeAccountTextViewPhoneNumber.text =
                                jsonObjectUser?.getString("phoneNumber")
                            binding.homeAccountTextViewBirthday.text =
                                jsonObjectUser?.getString("birthDay")
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitInfoLogineduser : 통신 실패")
                }
            }
        })
    }

    //회원 탈퇴 레트로핏 통신
    private fun retrofitDeleteLoginedUser() {
        RetrofitManager.instance.deleteLoginedUser(completion = { completionResponse, response ->
            when (completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "HomeAccountActivity - retrofitDeleteLoginedUser : ${response}")
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitDeleteLoginedUser : 통신 실패")
                }
            }
        })
    }

    //회원 정보 수정 레트로핏 통신
    private fun retrofitEditLoginedUser() {
        var sex = "F"
        if(binding.homeAccountTextViewSex.text.equals("남성"))
            sex = "T"
        val nickname = binding.homeAccountTextViewNickname.text.toString()
        val phoneNumber = binding.homeAccountTextViewPhoneNumber.text.toString()
        val birthday = binding.homeAccountTextViewBirthday.text.toString()
        val userData = UserData(nickname, phoneNumber, "", birthday, sex)
        RetrofitManager.instance.editLoginedUser(userData, completion = {completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "HomeAccountActivity - retrofitEditLoginedUser : ${response}")
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitEditLoginedUser : 통신 실패")
                }
            }
        })
    }

    //로그아웃 레트로핏 통신
    private fun retrofitLogoutUser() {
        RetrofitManager.instance.logoutUser(completion = {completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "HomeAccountActivity - retrofitLogoutUser : ${response}")
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitLogoutUser : 통신 실패")
                }
            }
        })
    }
}
