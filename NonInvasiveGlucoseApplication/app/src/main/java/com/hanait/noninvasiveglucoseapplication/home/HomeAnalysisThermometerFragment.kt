package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeAnalysisThermometerBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomChartManager
import com.hanait.noninvasiveglucoseapplication.util.CustomMarkerView

class HomeAnalysisThermometerFragment : BaseFragment<FragmentHomeAnalysisThermometerBinding>(FragmentHomeAnalysisThermometerBinding::inflate), OnChartValueSelectedListener {
    lateinit var customChartManager: CustomChartManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setThermometer7DayLineChart()

    }
    private fun init() {
        customChartManager = CustomChartManager.getInstance(requireContext())
        
    }

    //체온 차트 설정
    private fun setThermometer7DayLineChart() {
        val thermometerCandleData =  customChartManager.setThermometer7DayCandleData()
        val candleData = CandleData(thermometerCandleData)
        val candleThermometerDay = binding.homeThermometerCandleChartDay
//        val combinedData = LineData()
//        lineThermometerDay.setData(lineData)

        //마커 뷰 설정
        val markerView = CustomMarkerView(context, R.layout.custom_marker_view)

        //클릭 리스너 설정
        candleThermometerDay.setOnChartValueSelectedListener(this)
        candleThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = candleData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true    //드래그 가능
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
                axisMaximum = 50F   //최대값
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y그리드 색깔 변경
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
        Log.d("로그", "HomeAnalysisThermometerFragment - onValueSelected : ${e?.y}  ${e?.x}   ${e?.data}")
//        binding.homeAnalysisThermometerTextViewMinValue.text = "${e?.}"
    }

    override fun onNothingSelected() {
    }
}


//        lineThermometerHeart.setDrawGridBackground(true)    //배경 색 추가하기

//가로 세로 꽉채우기
//        lineThermometerDay.setViewPortOffsets(0f, 0f, 0f, 0f)
//        lineThermometerDay.xAxis.axisMaximum = lineThermometerDay.data.getDataSetByIndex(0).xMax
//        lineThermometerDay.xAxis.axisMinimum = 0f

//        lineThermometerHeart.setTouchEnabled(false)
//        lineThermometerHeart.xAxis.textSize = 13f 가로출 텍스트 사이즈
//        lineThermometerHeart.axisLeft.setDrawGridLines(false)
