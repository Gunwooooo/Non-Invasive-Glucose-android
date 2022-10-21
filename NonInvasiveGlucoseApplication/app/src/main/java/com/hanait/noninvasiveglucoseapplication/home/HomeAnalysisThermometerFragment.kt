package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeAnalysisThermometerBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomChartManager

class HomeAnalysisThermometerFragment : BaseFragment<FragmentHomeAnalysisThermometerBinding>(FragmentHomeAnalysisThermometerBinding::inflate) {
    lateinit var customChartManager: CustomChartManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setThermometerDayLineChart()

    }
    private fun init() {
        customChartManager = CustomChartManager.getInstance(requireContext())
    }

    //체온 차트 설정
    private fun setThermometerDayLineChart() {
        val thermometerLineData =  customChartManager.setThermometerDayLineData()
        val lineData = LineData(thermometerLineData)
        val lineThermometerDay = binding.homeThermometerLineChartDay
        lineThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = lineData
            description.isEnabled = false
            legend.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능

            xAxis.run { //아래 라벨 X축
                setDrawGridLines(true)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                isEnabled = true
                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)    //y그리드 색깔 변경
            }
            axisRight.run { //오른쪽 y축축
                isEnabled = false  //오른쪽 y축 없애기
           }
        }
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
