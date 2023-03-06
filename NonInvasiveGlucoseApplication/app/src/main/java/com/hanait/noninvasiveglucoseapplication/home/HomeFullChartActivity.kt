package com.hanait.noninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeFullChartBinding
import com.hanait.noninvasiveglucoseapplication.home.HomeActivity.Companion.thermometerLineData
import com.hanait.noninvasiveglucoseapplication.util.CustomCalendarManager
import com.hanait.noninvasiveglucoseapplication.util.CustomChartManager
import com.hanait.noninvasiveglucoseapplication.util.CustomMarkerViewManager
import java.util.*

class HomeFullChartActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeFullChartBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setThermometerLineChart()

        //캘린더 이미지 넣기
        Glide.with(this).load(R.drawable.ic_baseline_calendar_month_24).into(binding.homeFullChartImageViewCalendar)

        //오늘 날짜 설정
        setTodayDate()

        binding.homeFullChartImageViewCalendar.setOnClickListener(this)
        binding.homeFullChartBtnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeFullChartBtnBack -> {
                finish()
            }
            binding.homeFullChartImageViewCalendar -> {
                CustomCalendarManager(this).makeDatePickerDialog(setDatePickerDialogListener()).show()
            }
        }
    }

    //초기 오늘 날짜 표시 되도록 설정
    @SuppressLint("SetTextI18n")
    private fun setTodayDate() {
        val gregorianCalendar = GregorianCalendar()
        val year = gregorianCalendar.get(Calendar.YEAR)
        val month = gregorianCalendar.get(Calendar.MONTH)
        val dayOfMonth = gregorianCalendar.get(Calendar.DAY_OF_MONTH)
        binding.homeFullChartTextViewDate.text = "${year}년 ${month + 1}월 ${dayOfMonth}일"
    }

    //데이터피커 리스너 설정
    @SuppressLint("SetTextI18n")
    private fun setDatePickerDialogListener() : DatePickerDialog.OnDateSetListener {
        val datePickerDialogListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            binding.homeFullChartTextViewDate.text = "${year}년 ${month+1}월 ${dayOfMonth}일"
        }
        return datePickerDialogListener
    }

    //체온 차트 설정
    private fun setThermometerLineChart() {
        val lineThermometerDay = binding.homeFullChartLineChart
        //마커 뷰 설정
        val markerView = CustomMarkerViewManager(this, R.layout.custom_marker_view)
        lineThermometerDay.run {
            data = thermometerLineData
            setScaleEnabled(false) //핀치 줌 안되도록
            data = lineData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
            isScaleXEnabled = false //가로 확대 없애기
//            enableScroll()
//            setVisibleXRangeMaximum(6f) //
            setBackgroundColor(ContextCompat.getColor(context, R.color.android_blue_100))
            marker = markerView

            notifyDataSetChanged()  //차트 값 변동을 감지함
            moveViewToX((thermometerLineData.entryCount).toFloat())
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
//                labelCount = 8
//                granularity = 3f  //X축 간격
                textSize = 12f
                textColor = ContextCompat.getColor(context, R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 10F   //최소값
                axisMaximum = 40F   //최대값
                isEnabled = true
                animateX(500)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(context, R.color.toss_black_700)
                gridColor =
                    ContextCompat.getColor(context, R.color.toss_black_150)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
                isEnabled = false  //오른쪽 y축 없애기
            }
            legend.run {
                isEnabled = true //레전드 아이콘 표시
                form = Legend.LegendForm.CIRCLE
                textSize = 16f
                setExtraOffsets(15f, 15f, 15f, 15f)
                textColor = ContextCompat.getColor(context, R.color.toss_black_700)
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }

}