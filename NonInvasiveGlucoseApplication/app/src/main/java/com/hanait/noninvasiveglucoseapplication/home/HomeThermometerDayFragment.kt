package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeThermometerDayBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomChartManager

class HomeThermometerDayFragment : BaseFragment<FragmentHomeThermometerDayBinding>(FragmentHomeThermometerDayBinding::inflate) {
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
        val lineThermometerHeart = binding.homeThermometerLineChartDay
        lineThermometerHeart.data = lineData
        lineThermometerHeart.setDrawGridBackground(false)
        lineThermometerHeart.setPinchZoom(false)
        lineThermometerHeart.description.isEnabled = false
        lineThermometerHeart.legend.isEnabled = false
        lineThermometerHeart.xAxis.setDrawGridLines(false)
        lineThermometerHeart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineThermometerHeart.isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능

        //가로 세로 꽉채우기
        lineThermometerHeart.setViewPortOffsets(0f, 0f, 0f, 0f)
        lineThermometerHeart.xAxis.axisMaximum = lineThermometerHeart.data.getDataSetByIndex(0).xMax
        lineThermometerHeart.xAxis.axisMinimum = 0f
        //        lineThermometerHeart.setTouchEnabled(false)
//        lineThermometerHeart.xAxis.textSize = 13f 가로출 텍스트 사이즈
        //        lineThermometerHeart.axisLeft.setDrawGridLines(false)
        lineThermometerHeart.axisRight.isEnabled = false
        lineThermometerHeart.axisLeft.isEnabled = false
        lineThermometerHeart.animateXY(1000, 1000)
    }
}