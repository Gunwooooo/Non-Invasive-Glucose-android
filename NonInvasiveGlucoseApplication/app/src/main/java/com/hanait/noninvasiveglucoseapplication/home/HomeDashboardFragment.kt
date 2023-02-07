package com.hanait.noninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginBottom
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeDashboardBinding
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.user.UserActivity
import com.hanait.noninvasiveglucoseapplication.user.UserSetPhoneNumberFragment
import com.hanait.noninvasiveglucoseapplication.util.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Thread.sleep
import java.util.*


class HomeDashboardFragment : BaseFragment<FragmentHomeDashboardBinding>(FragmentHomeDashboardBinding::inflate), View.OnClickListener {
    private val customProgressDialog by lazy { CustomDialogManager(R.layout.common_progress_dialog, null) }
    private val customChartManager by lazy { CustomChartManager.getInstance(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
//
    override fun onResume() {
        super.onResume()

        //사용자 정보 가져오기
        retrofitInfoLoginedUser()
    }

    private fun init() {
        //프로그래스 다이어로그 호출

        //글라이드로 모든 이미지 가져오기
        setImageViewWithGlide()

        //데이터 차트 설정
        setThermometerLineChart()
        setHeartLineChart()
        setGlucoseLineChart()


        binding.homeDashboardBtnThermometer.setOnClickListener(this)
        binding.homeDashboardBtnHeart.setOnClickListener(this)
        binding.homeDashboardBtnGlucose.setOnClickListener(this)
        binding.homeDashboardImageViewCalendar.setOnClickListener(this)
        binding.homeDashboardBtnAccount.setOnClickListener(this)
        binding.homeDashboardTextViewThermometerDetail.setOnClickListener(this)
        binding.homeDashboardTextViewHeartDetail.setOnClickListener(this)
        binding.homeDashboardTextViewGlucoseDetail.setOnClickListener(this)
        binding.homeDashboardImageViewSetting.setOnClickListener(this)

        //오늘 날짜 설정
        setTodayDate()

        //데이터피커 리스너 설정
        setDatePickerDialogListener()
    }

    override fun onClick(v: View?) {
        val mActivity = activity as HomeActivity
        when (v) {
            binding.homeDashboardTextViewThermometerDetail ->
                mActivity.changeFragmentTransactionWithAnimation(HomeThermometerFragment())
            binding.homeDashboardBtnThermometer -> {
                val intent = Intent(requireContext(), HomeFullChartActivity::class.java)
                startActivity(intent)
            }
            binding.homeDashboardBtnHeart, binding.homeDashboardTextViewHeartDetail ->
                mActivity.changeFragmentTransactionWithAnimation(HomeThermometerFragment())
            binding.homeDashboardBtnGlucose, binding.homeDashboardTextViewGlucoseDetail ->
                mActivity.changeFragmentTransactionWithAnimation(HomeThermometerFragment())
            binding.homeDashboardImageViewCalendar ->
                CustomCalendarManager(requireContext()).makeDatePickerDialog(setDatePickerDialogListener()).show()
            binding.homeDashboardBtnAccount -> {
                val intent = Intent(requireContext(), HomeAccountActivity::class.java)
                startActivity(intent)
            }
            binding.homeDashboardImageViewSetting -> {
                val intent = Intent(requireContext(), HomeDeviceActivity::class.java)
                startActivity(intent)
            }
        }
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(requireContext())
        glide.load(R.drawable.background_image_dashboard).into(binding.homeDashboardImageViewDashBoardBackground)
        glide.load(R.drawable.icon_color_profile).into(binding.homeDashboardImageViewProfile)
        glide.load(R.drawable.icon_color_calendar).into(binding.homeDashboardImageViewCalendar)
        glide.load(R.drawable.ic_baseline_settings_24).into(binding.homeDashboardImageViewSetting)
    }

    //초기 오늘 날짜 표시 되도록 설정
    @SuppressLint("SetTextI18n")
    private fun setTodayDate() {
        val gregorianCalendar = GregorianCalendar()
        val year = gregorianCalendar.get(Calendar.YEAR)
        val month = gregorianCalendar.get(Calendar.MONTH)
        val dayOfMonth = gregorianCalendar.get(Calendar.DAY_OF_MONTH)
        binding.homeDashboardTextViewDate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
    }

    //데이터피커 리스너 설정
    @SuppressLint("SetTextI18n")
    private fun setDatePickerDialogListener() : DatePickerDialog.OnDateSetListener {
        val datePickerDialogListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            binding.homeDashboardTextViewDate.text = "${year}년 ${month+1}월 ${dayOfMonth}일"
        }
        return datePickerDialogListener
    }
    //=================================================================================================

    //체온 차트 설정
    private fun setThermometerLineChart() {
        val thermometerLineData =  customChartManager.setThermometerDashboardLineData()
        val lineData = LineData(thermometerLineData)
        val lineThermometerDay = binding.homeDashboardThermometerChart
        //마커 뷰 설정
        val markerView = CustomMarkerViewManager(context, R.layout.custom_marker_view)
        lineThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = lineData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = false
            isScaleXEnabled = false //가로 확대 없애기
//            enableScroll()
//            setVisibleXRangeMaximum(6f) //
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                labelCount = 8
//                granularity = 3f  //X축 간격
                textSize = 12f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 32F   //최소값
                axisMaximum = 42F   //최대값
                isEnabled = true
                animateX(500)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
                isEnabled = false  //오른쪽 y축 없애기
            }
            legend.run {

                isEnabled = true //레전드 아이콘 표시
                form = Legend.LegendForm.CIRCLE
                textSize = 16f
                setExtraOffsets(5f, 5f, 5f, 15f)
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }

    //심박수 차트 설정
    private fun setHeartLineChart() {
        val heartLineData = customChartManager.setHeartLineData()
        val lineData = LineData(heartLineData)
        val lineChartHeart = binding.homeDashboardHeartChart
        val markerView = CustomMarkerViewManager(context, R.layout.custom_marker_view)
        lineChartHeart.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = lineData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = false
            isScaleXEnabled = false //가로 확대 없애기
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
//            enableScroll()
//            setVisibleXRangeMaximum(7f) //

            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                labelCount = 8
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 32F   //최소값
                axisMaximum = 42F   //최대값
                isEnabled = true
                animateX(500)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
                isEnabled = false  //오른쪽 y축 없애기
            }
            legend.run {
                isEnabled = true //레전드 아이콘 표시
                form = Legend.LegendForm.CIRCLE
                textSize = 16f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                setExtraOffsets(5f, 5f, 5f, 15f)
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }
    //혈당 차트 설정
    private fun setGlucoseLineChart() {
        val glucoseData = customChartManager.setGlucoseLineData()
        val lineData = LineData(glucoseData)
        val lineGlucoseBlood = binding.homeDashboardGlucoseChart
        val markerView = CustomMarkerViewManager(context, R.layout.custom_marker_view)
        lineGlucoseBlood.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = lineData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = false
            isScaleXEnabled = false //가로 확대 없애기
//            enableScroll()
//            setVisibleXRangeMaximum(7f) //
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                labelCount = 8
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 32F   //최소값
                axisMaximum = 42F   //최대값
                isEnabled = true
                animateX(500)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
                isEnabled = false  //오른쪽 y축 없애기
            }
            legend.run {
                isEnabled = true //레전드 아이콘 표시
                form = Legend.LegendForm.CIRCLE
                textSize = 16f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                setExtraOffsets(5f, 5f, 5f, 15f)
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }

    //생년월일로 나이 계산
    private fun changeBirthDayToAge(birthDay: String?) : String {
        if(birthDay?.length!! <= 5 ) return ""
        val userYear = birthDay.substring(0, 4).toInt()
        val curYear = GregorianCalendar().get(Calendar.YEAR)
        return "${curYear - userYear}세"
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //로그인 된 회원 정보 가져오기
    @SuppressLint("SetTextI18n")
    private fun retrofitInfoLoginedUser() {
        customProgressDialog.show(childFragmentManager, "common_progress_dialog")
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
                            LoginedUserClient.phoneNumber = jsonObjectUser?.getString("phoneNumber")
                            binding.homeDashboardTextViewNickname.text =
                                "${jsonObjectUser?.getString("nickname")}"
                            if (jsonObjectUser?.getString("sex").equals("T")) {
                                binding.homeDashboardTextViewSex.text = "남성"
                            } else
                                binding.homeDashboardTextViewSex.text = "여성"
                            binding.homeDashboardTextViewAge.text =
                                changeBirthDayToAge(jsonObjectUser?.getString("birthDay"))
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeAccountActivity - retrofitInfoLogineduser : 통신 실패")
                }
            }
        })
    }
}