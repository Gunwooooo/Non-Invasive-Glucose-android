package com.hanait.noninvasiveglucoseapplication.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeFullChartBinding
import com.hanait.noninvasiveglucoseapplication.util.CustomChartManager
import com.hanait.noninvasiveglucoseapplication.util.CustomMarkerViewManager

class HomeFullChartActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeFullChartBinding.inflate(layoutInflater) }
    lateinit var customChartManager: CustomChartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        customChartManager = CustomChartManager.getInstance(applicationContext)

        setThermometerLineChart()

        binding.homeFullChartBtnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeFullChartBtnBack -> {
                finish()
            }
        }
    }

    //체온 차트 설정
    private fun setThermometerLineChart() {
        val thermometerLineData =  customChartManager.setThermometerDashboardLineData()
        val lineData = LineData(thermometerLineData)
        val lineThermometerDay = binding.homeFullChartLineChart
        //마커 뷰 설정
        val markerView = CustomMarkerViewManager(this, R.layout.custom_marker_view)
        lineThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = lineData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
            isScaleXEnabled = false //가로 확대 없애기
//            enableScroll()
            setVisibleXRangeMaximum(6f) //
            setBackgroundColor(ContextCompat.getColor(context, R.color.android_blue_100))
            marker = markerView
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
                axisMinimum = 32F   //최소값
                axisMaximum = 42F   //최대값
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