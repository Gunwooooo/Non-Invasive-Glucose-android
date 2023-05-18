package com.example.newnoninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.ArrayMap
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.model.UserData
import com.example.newnoninvasiveglucoseapplication.retrofit.API.PHR_PROFILE_BASE_URL
import com.example.newnoninvasiveglucoseapplication.retrofit.CompletionResponse
import com.example.newnoninvasiveglucoseapplication.retrofit.RetrofitManager
import com.example.newnoninvasiveglucoseapplication.span.GravityDotSpan
import com.example.newnoninvasiveglucoseapplication.util.Constants.PROFILE_IMAGE_NAME
import com.example.newnoninvasiveglucoseapplication.util.LoginedUserClient.phoneNumber
import com.jakewharton.rxbinding4.widget.textChanges
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.wang.avi.AVLoadingIndicatorView
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import java.text.Format
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


class CustomDialogManager(private val mContext: Context, private val layout: Int, userData: UserData?) : DialogFragment(), View.OnClickListener {

//    private val customProgressDialog by lazy { CustomDialogManager(mContext, R.layout.common_progress_dialog, null) }

    private var stringData1 = ""
    private var stringData2 = ""
    private var stringData3 = ""

    //날짜별 데이터 유무 저장할 ArrayMap 선언
    private lateinit var hashSet: HashSet<String>

    lateinit var calendarDay : CalendarDay

    private var mUserData = userData

    //원 버튼 다이어로그
    private var oneButtonDialogListener: OneButtonDialogListener? = null
    interface OneButtonDialogListener {
        fun onPositiveClicked()
    }
    fun setOneButtonDialogListener(customDialogListener: OneButtonDialogListener) {
        this.oneButtonDialogListener = customDialogListener
    }

    //투 버튼 다이어로그
    private var twoButtonDialogListener: TwoButtonDialogListener? = null
    interface TwoButtonDialogListener {
        fun onPositiveClicked()
        fun onNegativeClicked()
    }
    fun setTwoButtonDialogListener(customDialogListener: TwoButtonDialogListener) {
        this.twoButtonDialogListener = customDialogListener
    }

    //String을 전달하는 투 버튼 다이어로그
    private var twoButtonWithOneDataDialogListener: TwoButtonWithOneDataDialogListener? = null
    interface TwoButtonWithOneDataDialogListener {
        fun onPositiveClicked(data: String)
        fun onNegativeClicked()
    }
    fun setTwoButtonWithOneDataDialogListener(customDialogListener: TwoButtonWithOneDataDialogListener) {
        this.twoButtonWithOneDataDialogListener = customDialogListener
    }

    //String을 전달하는 투 버튼 다이어로그
    private var twoButtonWithOneCalendarDataDialogListener: TwoButtonWithOneCalendarDataDialogListener? = null
    interface TwoButtonWithOneCalendarDataDialogListener {
        fun onPositiveClicked(calendarDay: CalendarDay)
        fun onNegativeClicked()
    }
    fun setTwoButtonWithOneCalendarDataDialogListener(customDialogListener: TwoButtonWithOneCalendarDataDialogListener) {
        this.twoButtonWithOneCalendarDataDialogListener = customDialogListener
    }

    //String을 전달하는 투 버튼 다이어로그
    private var twoButtonWithThreeDataDialogListener: TwoButtonWithThreeDataDialogListener? = null
    interface TwoButtonWithThreeDataDialogListener {
        fun onPositiveClicked(data1: String, data2: String, data3: String)
        fun onNegativeClicked()
    }
    fun setTwoButtonWithThreeDataDialogListener(customDialogListener: TwoButtonWithThreeDataDialogListener) {
        this.twoButtonWithThreeDataDialogListener = customDialogListener
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return view
    }


    @SuppressLint("SetTextI18n", "CheckResult")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(layout, null)
        builder.setView(view)

        var positiveButton : Button? = null
        var negativeButton : Button? = null
        var positiveTextView : TextView? = null
        var negativeTextView : TextView? = null
        stringData1 = ""
        stringData2 = ""
        stringData3 = ""

        when(layout) {
            /////////////////////////////////////////    protector   ///////////////////////////////
            //보호 대상자 삭제 다이어로그
            R.layout.home_protecting_delete_dialog -> {
                positiveButton = view.findViewById(R.id.homeProtectingDeleteDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeProtectingDeleteDialog_btn_negative) as Button
            }
            //보호자 삭제 다이어로그
            R.layout.home_protector_delete_dialog -> {
                positiveButton = view.findViewById(R.id.homeProtectorDeleteDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeProtectorDeleteDialog_btn_negative) as Button
            }
            //보호 대상자 정보 보기 다이어로그
            R.layout.home_protecting_info_dialog -> {
                positiveButton = view.findViewById(R.id.homeProtectingInfoDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeProtectingInfoDialog_btn_negative) as Button
                val nickNameTextView = view.findViewById(R.id.homeProtectingInfoDialog_textView_nickName) as TextView
                val ageTextView = view.findViewById(R.id.homeProtectingInfoDialog_textView_age) as TextView
                val sexTextView = view.findViewById(R.id.homeProtectingInfoDialog_textView_sex) as TextView
                val phoneNumberTextView = view.findViewById(R.id.homeProtectingInfoDialog_textView_phoneNumber) as TextView
                val profile = view.findViewById(R.id.homeProtectingInfoDialog_imageView_profile) as ImageView

                //닉네임, 나이, 성별 전화번호 뒷자리 출력
                nickNameTextView.text = mUserData?.nickname
                ageTextView.text = changeBirthDayToAge(mUserData!!.birthDay)
                sexTextView.text = changeSexToString(mUserData!!.sex)
                phoneNumberTextView.text = mUserData!!.phoneNumber.substring(7, 11)

                //프로필 이미지 넣기
                val glide = Glide.with(mContext)
                val sb = StringBuilder()
                sb.append(PHR_PROFILE_BASE_URL).append(mUserData!!.phoneNumber).append(PROFILE_IMAGE_NAME)
                glide.load(sb.toString()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .placeholder(R.drawable.icon_color_profile).circleCrop().into(profile)

            }
            //보호자 정보 보기 다이어로그
            R.layout.home_protector_info_dialog -> {
                positiveButton = view.findViewById(R.id.homeProtectorInfoDialog_btn_positive) as Button
                val nickNameTextView = view.findViewById(R.id.homeProtectorInfoDialog_textView_nickName) as TextView
                val ageTextView = view.findViewById(R.id.homeProtectorInfoDialog_textView_age) as TextView
                val sexTextView = view.findViewById(R.id.homeProtectorInfoDialog_textView_sex) as TextView
                val phoneNumberTextView = view.findViewById(R.id.homeProtectorInfoDialog_textView_phoneNumber) as TextView
                val profile = view.findViewById(R.id.homeProtectorInfoDialog_imageView_profile) as ImageView

                //닉네임, 나이, 성별 전화번호 뒷자리 출력
                nickNameTextView.text = mUserData?.nickname
                ageTextView.text = changeBirthDayToAge(mUserData!!.birthDay)
                sexTextView.text = changeSexToString(mUserData!!.sex)
                phoneNumberTextView.text = mUserData!!.phoneNumber.substring(7, 11)

                //프로필 이미지 넣기
                val glide = Glide.with(mContext)
                val sb = StringBuilder()
                sb.append(PHR_PROFILE_BASE_URL).append(mUserData!!.phoneNumber).append(PROFILE_IMAGE_NAME)
                glide.load(sb.toString()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .placeholder(R.drawable.icon_color_profile).circleCrop().into(profile)
            }
            //보호자 검색 및 조회 다이어로그
            R.layout.home_protector_search_info_dialog -> {
                positiveButton = view.findViewById(R.id.homeProtectorSearchInfoDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeProtectorSearchInfoDialog_btn_negative) as Button
                val nickNameTextView = view.findViewById(R.id.homeProtectorSearchInfoDialog_textView_nickName) as TextView
                val ageTextView = view.findViewById(R.id.homeProtectorSearchInfoDialog_textView_age) as TextView
                val sexTextView = view.findViewById(R.id.homeProtectorSearchInfoDialog_textView_sex) as TextView
                val phoneNumberTextView = view.findViewById(R.id.homeProtectorSearchInfoDialog_textView_phoneNumber) as TextView
                val profile = view.findViewById(R.id.homeProtectorSearchInfoDialog_imageView_profile) as ImageView

                //현재 나이, 이름 설정
                nickNameTextView.text = mUserData?.nickname
                ageTextView.text = changeBirthDayToAge(mUserData!!.birthDay)
                sexTextView.text = changeSexToString(mUserData!!.sex)
                phoneNumberTextView.text = mUserData!!.phoneNumber.subSequence(7, 11)
                //프로필 이미지 넣기
                val glide = Glide.with(mContext)
                val sb = StringBuilder()
                sb.append(PHR_PROFILE_BASE_URL).append(mUserData!!.phoneNumber).append(PROFILE_IMAGE_NAME)
                glide.load(sb.toString()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .placeholder(R.drawable.icon_color_profile).circleCrop().into(profile)
            }
            //////////////////////////////////////////    Account   /////////////////////////////////////
            //사용자 성별 수정 다이어로그
            R.layout.home_account_modify_sex_dialog -> {
                positiveButton = view.findViewById(R.id.homeAccountModifySexDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeAccountModifySexDialog_btn_negative) as Button
                val maleButton = view.findViewById(R.id.homeAccountModifySexDialog_btn_male) as Button
                val femaleButton = view.findViewById(R.id.homeAccountModifySexDialog_btn_female) as Button
                //성별 토글 기능 구현
                maleButton.setOnClickListener {
                    maleButton.isEnabled = false
                    maleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    femaleButton.isEnabled = true
                    femaleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.toss_black_500))
                    stringData1 = "남성"
                }
                femaleButton.setOnClickListener {
                    femaleButton.isEnabled = false
                    femaleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    maleButton.isEnabled = true
                    maleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.toss_black_500))
                    stringData1 = "여성"
                }
            }
            //비밀번호 수정 다이어로그
            R.layout.home_account_modify_password_dialog -> {
                positiveButton = view.findViewById(R.id.homeAccountModifyPasswordDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeAccountModifyPasswordDialog_btn_negative) as Button
                val curPasswordEditText = view.findViewById(R.id.homeAccountModifyPasswordDialog_editText_curPassword) as EditText
                val newPasswordEditText = view.findViewById(R.id.homeAccountModifyPasswordDialog_editText_newPassword) as EditText
                val checkPasswordEditText = view.findViewById(R.id.homeAccountModifyPasswordDialog_editText_checkPassword) as EditText
                curPasswordEditText.textChanges().subscribe {
                    stringData1 = it.toString()
                }
                newPasswordEditText.textChanges().subscribe {
                    stringData2 = it.toString()
                }
                checkPasswordEditText.textChanges().subscribe {
                    stringData3 = it.toString()
                }
            }
            //회원탈퇴 다이어로그
            R.layout.home_account_delete_user_dialog -> {
                positiveButton = view.findViewById(R.id.homeAccountDeleteUserDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeAccountDeleteUserDialog_btn_negative) as Button
            }
            //로그아웃 다이어로그
            R.layout.home_account_logout_user_dialog -> {
                positiveButton = view.findViewById(R.id.homeAccountLogoutUserDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeAccountLogoutUserDialog_btn_negative) as Button
            }
            //닉네임 변경 다이어로그
            R.layout.home_account_modify_nickname_dialog -> {
                positiveButton = view.findViewById(R.id.homeAccountModifyNicknameDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeAccountModifyNicknameDialog_btn_negative) as Button
                val nicknameEditText = view.findViewById(R.id.homeAccountModifyNicknameDialog_editText_nickname) as EditText
                //텍스트 감지 후 데이터 저장
                nicknameEditText.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        stringData1 = nicknameEditText.text.toString()
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun afterTextChanged(s: Editable?) {}
                })
            }
            //////////////////////////////////////////    Common   /////////////////////////////////////
            //프로그레스 다이어로그
            R.layout.common_progress_dialog -> {
                val indicatorView = view.findViewById(R.id.commonProgressDialog_indicator) as AVLoadingIndicatorView
                indicatorView.show()
            }
            //달력 다이어로그
            R.layout.common_calendar_dialog -> {
                positiveTextView = view.findViewById(R.id.commonCalendarDialog_textView_positive) as TextView
                negativeTextView = view.findViewById(R.id.commonCalendarDialog_textView_negative) as TextView
                val calendarView = view.findViewById(R.id.commonCalendarDialog_calendar) as MaterialCalendarView

                //오늘 날짜 가져오기
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                
                //오늘 날짜 표시하기
                calendarView.setDateSelected(CalendarDay.from(year, month + 1, dayOfMonth), true)

                //calendarDay 값 오늘 날짜로 초기화 하기
                calendarDay = CalendarDay.from(year, month + 1, dayOfMonth)

                //선택된 날짜 반환 하기
                calendarView.setOnDateChangedListener { widget, date, selected ->
                    calendarDay = date
                }

                //해당 월에 데이터 유무 가져 오기
                retrofitGetDataExistDates(calendarView)

//                //스와이프 이벤트 설정
//                calendarView.setOnMonthChangedListener { widget, date ->
//                    //해당 년도, 월에 데이터 유무 가져오기
//                    retrofitGetDataExistDates(date.year, date.month, calendarView)
//                }
            }
        }
        positiveButton?.setOnClickListener(this)
        negativeButton?.setOnClickListener(this)
        positiveTextView?.setOnClickListener(this)
        negativeTextView?.setOnClickListener(this)
        return builder.create()
    }

    //생년월일로 나이 계산
    private fun changeBirthDayToAge(birthDay: String) : String {
        if(birthDay.length <= 5 ) return ""
        val userYear = birthDay.substring(0, 4).toInt()
        val curYear = GregorianCalendar().get(Calendar.YEAR)
        return "${curYear - userYear}세"
    }

    //성별 T,f -> 남성 여성
    private fun changeSexToString(sex: String) : String {
        return if(sex == "T") "남성"
        else "여성"
    }

    //다이어로그 버튼 별 클릭 리스너 등록
    override fun onClick(v: View?) {
        when(v?.id) {
            //원 버튼 파지티브 리스너 연결
            R.id.homeProtectorInfoDialog_btn_positive
            -> oneButtonDialogListener?.onPositiveClicked()
            
            //투 버튼 파지티브 리스너 연결
            R.id.homeProtectingDeleteDialog_btn_positive, R.id.homeProtectorDeleteDialog_btn_positive,
            R.id.homeAccountDeleteUserDialog_btn_positive, R.id.homeProtectingInfoDialog_btn_positive,
            R.id.homeAccountLogoutUserDialog_btn_positive, R.id.homeProtectorSearchInfoDialog_btn_positive,

            -> twoButtonDialogListener?.onPositiveClicked()

            //투 버튼 네가티브 리스너 연결
            R.id.homeProtectingDeleteDialog_btn_negative, R.id.homeProtectorDeleteDialog_btn_negative,
            R.id.homeAccountDeleteUserDialog_btn_negative, R.id.homeProtectingInfoDialog_btn_negative,
            R.id.homeAccountLogoutUserDialog_btn_negative, R.id.homeProtectorSearchInfoDialog_btn_negative,

            -> twoButtonDialogListener?.onNegativeClicked()


            //데이터 전달이 있는 투 버튼 리스너 연결
            R.id.homeAccountModifySexDialog_btn_positive, R.id.homeAccountModifyNicknameDialog_btn_positive,

            -> twoButtonWithOneDataDialogListener?.onPositiveClicked(stringData1)
            R.id.homeAccountModifySexDialog_btn_negative, R.id.homeAccountModifyNicknameDialog_btn_negative,

            -> twoButtonWithOneDataDialogListener?.onNegativeClicked()

            //캘린더 데이터를 전달하는 투 버튼 리스너 연결
            R.id.commonCalendarDialog_textView_positive,
            -> twoButtonWithOneCalendarDataDialogListener?.onPositiveClicked(calendarDay)
            R.id.commonCalendarDialog_textView_negative,
            -> twoButtonWithOneCalendarDataDialogListener?.onNegativeClicked()

            //데이터 전달이 세 개 있는 투 버튼 리스너 연결
            R.id.homeAccountModifyPasswordDialog_btn_positive
            -> twoButtonWithThreeDataDialogListener?.onPositiveClicked(stringData1, stringData2, stringData3)
            R.id.homeAccountModifyPasswordDialog_btn_negative
            -> twoButtonWithThreeDataDialogListener?.onNegativeClicked()
        }
    }

    /////////////////////////////////// ///////////////////////////////////////////////////////////
    //데이터 있는 날짜 캘린더에 표시하기 위해 전체 날짜 데이터 유무 가져오기
    private fun retrofitGetDataExistDates(calendarView: MaterialCalendarView) {
        RetrofitManager.instance.getDataExistDates(phoneNumber!!, completion = { completionResponse, response ->
                when (completionResponse) {
                    CompletionResponse.OK -> {
                        //날짜별 boolean 저장할 변수 선언
                        when (response!!.code()) {
                            200 -> {
//                                Log.d("로그", "CustomDialogManager - retrofitGetDataExistDates : ${response.body()!!.string()}")

                                hashSet = HashSet()
                                //로그인 된 유저 데이터 제이슨으로 파싱하기
                                val jsonObject = JSONObject(response.body()!!.string())

                                //키 개수만큼 HashSet에 넣기
                                jsonObject.keys().forEach { dateTime ->
                                    val localDateString = dateTime.split('T')[0]
                                    hashSet.add(localDateString)
                                }
                                Log.d("로그", "CustomDialogManager - retrofitGetDataExistDates : hash 사이즈 : ${hashSet.size}")

                                val calendar = Calendar.getInstance()
                                Log.d("로그", "CustomDialogManager - retrofitGetDataExistDates : $calendar")

                                //데이터 있는 부분 붉은색 점 표시하기
                                val mDayViewDecorator = object : DayViewDecorator {
                                    override fun shouldDecorate(day: CalendarDay?): Boolean {
                                        val year = day!!.year
                                        val month = day.month
                                        val dayOfMonth = day.day

                                        val calendar = Calendar.getInstance()
                                        calendar.set(year, month - 1, dayOfMonth)
                                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

                                        //해당 날짜에 데이터가 true이면
                                        if(hashSet.contains(sdf)) {
                                            return true
                                        }
                                        return false
                                    }

                                    override fun decorate(view: DayViewFacade?) {
                                        //캘린더에 붉은 점 표시하기
                                        val radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2F, resources.displayMetrics)
                                        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics)
                                        val gravityDotSpan = GravityDotSpan(radius, ContextCompat.getColor(mContext, R.color.circle_red_100), padding, Gravity.TOP and Gravity.END)
                                        view!!.addSpan(gravityDotSpan)
                                    }
                                }
                                calendarView.addDecorators(mDayViewDecorator)
                            }
                        }
                    }
                    CompletionResponse.FAIL -> {
                        Log.d("로그", "CustomDialogManager - retrofitGetDataExistDates : 통신 실패")
                    }
                }
            })
    }
}

