package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeAnalysisHeartBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomChartManager
import com.hanait.noninvasiveglucoseapplication.util.CustomMarkerViewManager


class HomeAnalysisHeartFragment : BaseFragment<FragmentHomeAnalysisHeartBinding>(FragmentHomeAnalysisHeartBinding::inflate),OnChartValueSelectedListener {
    lateinit var customChartManager: CustomChartManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init()

        setHeart7DayLineChart()
    }

    private fun init() {
        customChartManager = CustomChartManager.getInstance(requireContext())
    }

    //체온 차트 설정
    private fun setHeart7DayLineChart() {
        val heartCandleData = customChartManager.setHeart7DayCandleData()
        val candleData = CandleData(heartCandleData)
        val candleHeartDay = binding.homeHeartCandleChartDay
//        val combinedData = LineData()
//        lineThermometerDay.setData(lineData)

        //마커 뷰 설정
        val markerView = CustomMarkerViewManager(context, R.layout.custom_marker_view)
        candleHeartDay.setOnChartValueSelectedListener(this)
        candleHeartDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = candleData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
            isScaleXEnabled = false //가로 확대 없애기
            enableScroll()
            setVisibleXRangeMaximum(7f) //

            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = CustomChartManager.CustomDateXAxisFormatter()
//                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 32F   //최소값
                axisMaximum = 42F   //최대값
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
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
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            }
            invalidate()
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e is CandleEntry) {
            val minVal = Math.min(e.open, e.close)
            val maxVal = Math.max(e.open, e.close)
            binding.homeAnalysisHeartTextViewMinValue.text = "$minVal"
            binding.homeAnalysisHeartTextViewMaxValue.text = "$maxVal"
        }
    }

    override fun onNothingSelected() {
    }
}