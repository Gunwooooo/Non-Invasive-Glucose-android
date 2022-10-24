package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeAnalysisThermometerBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomChartManager

class HomeAnalysisThermometerFragment : BaseFragment<FragmentHomeAnalysisThermometerBinding>(FragmentHomeAnalysisThermometerBinding::inflate), View.OnClickListener {
    lateinit var customChartManager: CustomChartManager
    private lateinit var textViewList : List<TextView>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setThermometerDayLineChart()

    }
    private fun init() {
        customChartManager = CustomChartManager.getInstance(requireContext())

        textViewList = listOf(binding.homeAnalysisThermometerTextViewDay, binding.homeAnalysisThermometerTextViewMonth, binding.homeAnalysisThermometerTextViewYear)



        binding.homeAnalysisThermometerTextViewDay.setOnClickListener(this)
        binding.homeAnalysisThermometerTextViewMonth.setOnClickListener(this)
        binding.homeAnalysisThermometerTextViewYear.setOnClickListener(this)
    }

    //체온 차트 설정
    private fun setThermometerDayLineChart() {
        val thermometerLineData =  customChartManager.setThermometerDayLineData()
        val thermometerBarData = customChartManager.setThermometerBarData()
        val lineData = LineData(thermometerLineData)
        val barData = BarData(thermometerBarData)
        val lineThermometerDay = binding.homeThermometerLineChartDay
        Log.d("로그", "HomeAnalysisThermometerFragment - setThermometerDayLineChart : ${lineData.dataSets}   ${barData.dataSets}")
        val combinedData = CombinedData()
        combinedData.setData(lineData)
        combinedData.setData(barData)
        lineThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = combinedData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isDragEnabled = true
            isScaleXEnabled = true
            enableScroll()
            setVisibleXRangeMaximum(7f) //
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 16f
//                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 32F   //최소값
                axisMaximum = 42F   //최대값
                isEnabled = true
                animateXY(2000, 1500)
                textSize = 16f
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
        }
    }


    override fun onClick(v: View?) {
        when(v) {
            binding.homeAnalysisThermometerTextViewDay -> {
                changeTextViewBackgroundColor(0)
            }
            binding.homeAnalysisThermometerTextViewMonth -> {
                changeTextViewBackgroundColor(1)
            }
            binding.homeAnalysisThermometerTextViewYear -> {
                changeTextViewBackgroundColor(2)
            }
        }
    }

    private fun changeTextViewBackgroundColor(index : Int) {
        for(i in 0 until 3) {
            if(i == index) {
                textViewList[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.toss_blue_200))
                textViewList[i].setBackgroundResource(R.color.toss_blue_100)
                continue
            }
            textViewList[i].setTextColor(ContextCompat.getColor(requireContext(), R.color.toss_black_500))
            textViewList[i].setBackgroundResource(R.color.white)
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
