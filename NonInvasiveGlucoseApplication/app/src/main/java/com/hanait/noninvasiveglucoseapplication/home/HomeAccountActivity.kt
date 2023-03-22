package com.hanait.noninvasiveglucoseapplication.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeAccountBinding
import com.hanait.noninvasiveglucoseapplication.model.UserData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.user.UserActivity
import com.hanait.noninvasiveglucoseapplication.util.BaseActivity
import com.hanait.noninvasiveglucoseapplication.util.Constants.PROFILE_IMAGE_NAME
import com.hanait.noninvasiveglucoseapplication.util.Constants._prefs
import com.hanait.noninvasiveglucoseapplication.util.CustomDatePickerDialogManager
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogManager
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import java.io.File
import java.util.regex.Pattern


class HomeAccountActivity : View.OnClickListener, BaseActivity() {
    private val customProgressDialog by lazy { CustomDialogManager(R.layout.common_progress_dialog, null) }

    private val PERM_STORAGE = 99   //외부 저장소 권한 처리

    private lateinit var activityResultLauncherGallery: ActivityResultLauncher<Intent>
    private lateinit var activityResultLauncherCropImage: ActivityResultLauncher<Intent>

    private val binding by lazy { ActivityHomeAccountBinding.inflate(layoutInflater) }
    private val glide by lazy { Glide.with(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        //권한 요청 초기화
        setActivityResultLauncher()

        //유저 정보 가져오기
        retrofitInfoLoginedUser()

        //글라이드로 모든 이미지 불러오기
        setImageViewWithGlide()

        //생년월일 변경 리스너 설정
        setDatePickerDialogListener()

        binding.homeAccountLayoutModifyProfile.setOnClickListener(this)
        binding.homeAccountTextViewModifyNickname.setOnClickListener(this)
        binding.homeAccountLayoutModifySex.setOnClickListener(this)
        binding.homeAccountLayoutModifyBirthday.setOnClickListener(this)
        binding.homeAccountBtnModifyPassword.setOnClickListener(this)
        binding.homeAccountBtnDeleteUser.setOnClickListener(this)
        binding.homeAccountBtnLogoutUser.setOnClickListener(this)
        binding.homeAccountBtnBack.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {
        when (v) {
            binding.homeAccountLayoutModifyProfile -> {
                requirePermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERM_STORAGE)
            }
            binding.homeAccountBtnBack -> finish()
            binding.homeAccountTextViewModifyNickname ->
                showModifyNicknameDialog()
            binding.homeAccountLayoutModifySex ->
                showModifySexDialog()
            binding.homeAccountLayoutModifyBirthday ->
                CustomDatePickerDialogManager(this).makeDatePickerDialog(setDatePickerDialogListener()).show()
            binding.homeAccountBtnModifyPassword ->
                showModifyPasswordDialog()
            binding.homeAccountBtnDeleteUser ->
                showDeleteUserDialog()
            binding.homeAccountBtnLogoutUser ->
                showLogoutUserDialog()
        }
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        glide.load(R.drawable.ic_baseline_arrow_back_24).into(binding.homeAccountImageViewBack)
        glide.load(R.drawable.icon_color_profile).into(binding.homeAccountImageViewProfile)
    }

    //캘린더 피커 다이어로그 리스너 설정
    @SuppressLint("SetTextI18n")
    private fun setDatePickerDialogListener() : DatePickerDialog.OnDateSetListener {
        val datePickerDialogListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            binding.homeAccountTextViewBirthday.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
            retrofitModifyLoginedUser()
        }
        return datePickerDialogListener
    }

    ///////////////////////////////////////다이어로그 구현///////////////////////////////////////////////

    //닉네임 변경 다이어로그 호출
    private fun showModifyNicknameDialog() {
        val customDialog = CustomDialogManager(R.layout.home_account_modify_nickname_dialog, null)
        customDialog.setTwoButtonWithOneDataDialogListener(object : CustomDialogManager.TwoButtonWithOneDataDialogListener {
            override fun onPositiveClicked(data: String) {
                customDialog.dismiss()

                //공백일 경우
                if(data == "") {
                    Toast.makeText(applicationContext, "변경하실 닉네임을 입력해 주세요", Toast.LENGTH_SHORT).show()
                    return
                }
                //회원정보 수정 기능
                binding.homeAccountTextViewNickname.text = data
                retrofitModifyLoginedUser()
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
                customDialog.dismiss()

                //공백일 경우
                if(data == "") {
                    Toast.makeText(applicationContext, "변경하실 성별을 선택해 주세요", Toast.LENGTH_SHORT).show()
                    return
                }

                //회원정보 수정 기능 추가 필요 ( 성별 )
                binding.homeAccountTextViewSex.text = data
                retrofitModifyLoginedUser()
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
                customDialog.dismiss()

                Toast.makeText(applicationContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

                //자동로그인 초기화 및 초기 화면으로 이동
                resetAutoLoginAndFinish()
            }
            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(supportFragmentManager, "home_account_modify_password_dialog")
    }

    //자동 로그인 해제 및 초기 화면으로 이동
    private fun resetAutoLoginAndFinish() {
        //자동 로그인 해제
        _prefs.setBoolean("AUTO_LOGIN", false)
        _prefs.setString("USER_PHONENUMBER", "")
        _prefs.setString("USER_PASSWORD", "")

        //초기화면으로 돌아가기
        val intent = Intent(this, UserActivity::class.java)
        startActivity(intent)
        finish()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //로그인 된 회원 정보 가져오기
    @SuppressLint("SetTextI18n")
    private fun retrofitInfoLoginedUser() {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(supportFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.infoLoginedUser(completion = { completionResponse, response ->
            customProgressDialog.dismiss()
            when (completionResponse) {
                CompletionResponse.OK -> {
                    when (response?.code()) {
                        200 -> {
                            //로그인 된 유저 데이터 제이슨으로 파싱하기
                            val jsonArray = JSONArray(response.body()?.string())
                            //유저 토큰 만료 시간 저장
                            LoginedUserClient.exp = jsonArray.getJSONObject(0).getLong("exp")
                            //유저 개인 정보 담기
                            val jsonObjectUser = jsonArray.getJSONObject(1)
                            LoginedUserClient.phoneNumber = jsonObjectUser!!.getString("phoneNumber")
                            val nickname = jsonObjectUser.getString("nickname")
                            binding.homeAccountTextViewNickname.text = nickname
                            binding.homeAccountRlv.titleText = nickname[0].toString()
                            binding.homeAccountTextViewPhoneNumber.text = jsonObjectUser.getString("phoneNumber")
                            if (jsonObjectUser.getString("sex").equals("T")) {
                                binding.homeAccountTextViewSex.text = "남성"
                            } else
                                binding.homeAccountTextViewSex.text = "여성"
                            binding.homeAccountTextViewBirthday.text = jsonObjectUser.getString("birthDay")
                            binding.homeAccountTextViewCreatedDate.text = jsonObjectUser.getString("createdDate").substring(0, 10)
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
        //로딩 프로그레스 바 출력
        customProgressDialog.show(supportFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.deleteLoginedUser(completion = { completionResponse, response ->
            customProgressDialog.dismiss()
            when (completionResponse) {
                CompletionResponse.OK -> {

                    when(response!!.code()) {
                        200 -> {
                            Log.d("로그", "HomeAccountActivity - retrofitDeleteLoginedUser : ${response}")
                            Toast.makeText(this, "탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                            //자동로그인 초기화 및 초기 화면으로 이동
                            resetAutoLoginAndFinish()
                        }
                    }

                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitDeleteLoginedUser : 통신 실패")
                }
            }
        })
    }

    //회원 정보 수정 레트로핏 통신
    private fun retrofitModifyLoginedUser() {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(supportFragmentManager, "common_progress_dialog")
        var sex = "F"
        if(binding.homeAccountTextViewSex.text.equals("남성"))
            sex = "T"
        val nickname = binding.homeAccountTextViewNickname.text.toString()
        val phoneNumber = binding.homeAccountTextViewPhoneNumber.text.toString()
        val birthday = binding.homeAccountTextViewBirthday.text.toString()
        val userData = UserData(nickname, phoneNumber, "", birthday, sex)
        RetrofitManager.instance.modifyLoginedUser(userData, completion = {completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        200 -> {
                            Toast.makeText(this, "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, "정보 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Toast.makeText(this, "정보 수정에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("로그", "HomeAccountActivity - retrofitmodifyLoginedUser : 통신 실패")
                }
            }
        })
    }

    //비밀번호 변경 시 현재 비밀번호 확인 레트로핏 통신
    private fun retrofitCheckAndModifyCurrentPassword(password: String, newPassword: String) {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(supportFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.checkAndModifyCurrentPassword(password, newPassword, completion = {completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "HomeAccountActivity - retrofitCheckCurrentPassword : ${response}")
                    when(response?.code()) {
                        //비밀번호 일치할 경우
                        200 -> {
                            //정보 수정
                            Toast.makeText(applicationContext, "비밀번호가 수정되었습니다.", Toast.LENGTH_SHORT ).show()
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

    //이미지 파일 서버로 보내기
    private fun retrofitModifyProfileImage(imageFile : File) {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(supportFragmentManager, "common_progress_dialog")
        val requestBody = imageFile.asRequestBody("image/png".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("profileImage", imageFile.name, requestBody)
        RetrofitManager.instance.modifyProfileImage(multipartBody, completion = {
            completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    //사진 이미지뷰에 넣기
                    Toast.makeText(baseContext, "프로필 이미지가 변경되었습니다.", Toast.LENGTH_SHORT).show()
//                    glide.load(imageFile).circleCrop().into(binding.homeAccountImageViewProfile)
                    Log.d("로그", "HomeAccountActivity - retrofitModifyProfileImage : $response")
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitModifyProfileImage : 통신 실패")
                }
            }
        })
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //이미지 변경 권한 요청
    override fun permissionGranted(requestCode: Int) {
        when(requestCode) {
            //저장소 권한 허용
            PERM_STORAGE -> {
                //갤러리 호출 함수
                openGallery()
            }
        }
    }

    //퍼미션 거부 시
    override fun permissionDenied(requestCode: Int) {
        when(requestCode) {
            PERM_STORAGE -> {
                Toast.makeText(baseContext, "갤러리 접근 권한을 승인해야 이용할 수 있는 서비스 입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //갤러리 호출 함수
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        activityResultLauncherGallery.launch(intent)
    }

    //반환 처리
    private fun setActivityResultLauncher() {
        //갤러리 콜백
        activityResultLauncherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //갤러리 호출 반환
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data

                //이미지 자르는 activity 호출
                val intent = Intent(baseContext, HomeCropImageActivity::class.java)
                intent.putExtra("imageUri", imageUri)
                activityResultLauncherCropImage.launch(intent)
            }
        }
        //이미지 자르기 콜백
        activityResultLauncherCropImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            //갤러리 호출 반환
            if (result.resultCode == RESULT_OK) {

                //이미지 파일 가져오기
                val imageName = result.data?.getStringExtra("imageName")
                val mFile = File(cacheDir, imageName!!)

                //프로필 이미지 보이게 설정
                binding.homeAccountImageViewProfile.visibility = View.VISIBLE
                binding.homeAccountRlv.visibility = View.GONE
                //사진 이미지뷰에 넣기
                //파일 이름이 동일한 캐시가 있을 수 있으므로 캐시 모두 지우기
                glide.load(mFile).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).circleCrop().into(binding.homeAccountImageViewProfile)

                //서버에 파일 전송
                retrofitModifyProfileImage(mFile)
            }
        }
    }
}
