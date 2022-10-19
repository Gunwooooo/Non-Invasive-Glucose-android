package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val thermometerLineData =  customChartManager.setThermometerLineData()
        val lineData = LineData(thermometerLineData)
        val lineThermometerHeart = binding.homeThermometerLineChartDay
        lineThermometerHeart.data = lineData
        lineThermometerHeart.setDrawGridBackground(false)
        lineThermometerHeart.setPinchZoom(false)
        lineThermometerHeart.description.isEnabled = false
        lineThermometerHeart.legend.isEnabled = false
        lineThermometerHeart.setTouchEnabled(false)
//        lineThermometerHeart.xAxis.setDrawGridLines(false)
        //        lineThermometerHeart.axisLeft.setDrawGridLines(false)
//        lineThermometerHeart.axisRight.isEnabled = false
//        lineThermometerHeart.axisLeft.isEnabled = false
        lineThermometerHeart.animateXY(1000, 1000)
    }
}