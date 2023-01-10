package com.hanait.noninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeAccountBinding
import com.hanait.noninvasiveglucoseapplication.model.UserData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.user.UserActivity
import com.hanait.noninvasiveglucoseapplication.util.Constants.prefs
import com.hanait.noninvasiveglucoseapplication.util.CustomCalendarManager
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogManager
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

class HomeAccountActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeAccountBinding.inflate(layoutInflater) }
    lateinit var datePickerDialogListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
//
//        window.statusBarColor = ContextCompat.getColor(this, R.color.android_blue_100)

        //생년월일 변경 리스너
        setDatePickerDialogListener()

        //토큰이 유효한지 확인만 계속 필요...
        //코드 구현 필요
        /////
        setUserInfoData()

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
            binding.homeAccountBtnBack ->
                finish()
            binding.homeAccountTextViewModifyNickname ->
                showModifyNicknameDialog()
            binding.homeAccountLayoutModifySex ->
                showModifySexDialog()
            binding.homeAccountLayoutModifyBirthday ->
                makeDatePickerDialog()
            binding.homeAccountBtnModifyPassword ->
                showModifyPasswordDialog()
            binding.homeAccountBtnDeleteUser ->
                showDeleteUserDialog()
            binding.homeAccountBtnLogoutUser ->
                showLogoutUserDialog()
        }
    }

    //토큰이 유효하면 기존에 로그인된 사용자 정보 표시
    private fun setUserInfoData() {
        binding.homeAccountTextViewNickname.text = LoginedUserClient.nickname
        binding.homeAccountTextViewPhoneNumber.text = LoginedUserClient.phoneNumber
        binding.homeAccountTextViewSex.text = LoginedUserClient.sex
        binding.homeAccountTextViewBirthday.text = LoginedUserClient.birthDay
        binding.homeAccountTextViewCreatedDate.text = LoginedUserClient.createdDate
    }

    //캘린더 피커 다이어로그 리스너 설정
    @SuppressLint("SetTextI18n")
    private fun setDatePickerDialogListener() {
        datePickerDialogListener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.homeAccountTextViewBirthday.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                retrofitEditLoginedUser()
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
        val customDialog = CustomDialogManager(R.layout.home_account_modify_nickname_dialog, null)
        customDialog.setTwoButtonWithOneDataDialogListener(object : CustomDialogManager.TwoButtonWithOneDataDialogListener {
            override fun onPositiveClicked(data: String) {
                //회원정보 수정 기능
                binding.homeAccountTextViewNickname.text = data
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
        val customDialog = CustomDialogManager(R.layout.home_account_modify_sex_dialog, null)
        customDialog.setTwoButtonWithOneDataDialogListener(object : CustomDialogManager.TwoButtonWithOneDataDialogListener {
            override fun onPositiveClicked(data: String) {
                //회원정보 수정 기능 추가 필요 ( 성별 )
                binding.homeAccountTextViewSex.text = data
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
        val customDialog = CustomDialogManager(R.layout.home_account_modify_password_dialog, null)
        customDialog.setTwoButtonWithThreeDataDialogListener(object : CustomDialogManager.TwoButtonWithThreeDataDialogListener {
            override fun onPositiveClicked(data1: String, data2: String, data3: String) {
                customDialog.dismiss()

                //비밀번호 일치 여부 확인
                if(data2 != data3) {
                    Toast.makeText(applicationContext, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT ).show()
                    return
                }
                //정규화 확인
                if(!checkPasswordRegex(data2)) {
                    Toast.makeText(applicationContext, "영문자, 특수문자, 숫자 3개를 조합하여 8자리 이상 입력해 주세요.", Toast.LENGTH_SHORT).show()
                    return
                }

                //현재 비밀번호 확인
                retrofitCheckAndModifyCurrentPassword(data1, data2)
            }
            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(supportFragmentManager, "home_account_modify_password_dialog")
    }

    //비밀번호 정규식 체크  영문자, 특수문자, 숫자 3개 조합하여 8자리 이상 ~20까지
    private fun checkPasswordRegex(stringData: String) : Boolean {
        val pwPattern =  "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@!%*#?&]).{8,20}.$"
        val pattern = Pattern.compile(pwPattern)
        val matcher = pattern.matcher(stringData)
        return matcher.find()
    }


    //회원 탈퇴 다이어로그 출력
    private fun showDeleteUserDialog() {
        val customDialog = CustomDialogManager(R.layout.home_account_delete_user_dialog, null)
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
        val customDialog = CustomDialogManager(R.layout.home_account_logout_user_dialog, null)
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


    //회원 탈퇴 레트로핏 통신
    private fun retrofitDeleteLoginedUser() {
        RetrofitManager.instance.deleteLoginedUser(completion = { completionResponse, response ->
            when (completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "HomeAccountActivity - retrofitDeleteLoginedUser : ${response}")
                    //자동 로그인 해제
                    prefs.setBoolean("AUTO_LOGIN", false)
                    prefs.setString("USER_PHONENUMBER", "")
                    prefs.setString("USER_PASSWORD", "")

                    Toast.makeText(this, "탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                    //초기화면으로 돌아가기
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    finish()

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
                    //데이터 오브젝트에 새로 저장
                    LoginedUserClient.nickname = nickname
                    LoginedUserClient.birthDay = birthday
                    LoginedUserClient.sex = binding.homeAccountTextViewSex.text.toString()
                    Toast.makeText(this, "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("로그", "HomeAccountActivity - retrofitEditLoginedUser : ${response}")
                }
                CompletionResponse.FAIL -> {
                    Toast.makeText(this, "정보 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show()
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
    
    //비밀번호 변경 시 현재 비밀번호 확인 레트로핏 통신
    private fun retrofitCheckAndModifyCurrentPassword(password: String, newPassword: String) {
        RetrofitManager.instance.checkAndModifyCurrentPassword(password, newPassword, completion = {completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "HomeAccountActivity - retrofitCheckCurrentPassword : ${response}")
                    when(response?.code()) {
                        //비밀번호 일치할 경우
                        200 -> {
                            //정보 수정
                        }
                        //비밀번호 일치하지 않을 경우
                        else -> {
                            Toast.makeText(applicationContext, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT ).show()
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitCheckCurrentPassword : 통신 실패")
                }
            }
        })
    }
}
