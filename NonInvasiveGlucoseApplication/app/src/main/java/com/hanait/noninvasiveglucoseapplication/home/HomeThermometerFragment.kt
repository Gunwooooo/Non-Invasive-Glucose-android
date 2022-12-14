package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeThermometerBinding
import com.hanait.noninvasiveglucoseapplication.user.UserActivity
import com.hanait.noninvasiveglucoseapplication.user.UserSetSexFragment
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomChartManager
import com.hanait.noninvasiveglucoseapplication.util.CustomMarkerViewManager
import kotlin.math.max
import kotlin.math.min


class HomeThermometerFragment : BaseFragment<FragmentHomeThermometerBinding>(FragmentHomeThermometerBinding::inflate), View.OnClickListener, OnChartValueSelectedListener {
    lateinit var customChartManager: CustomChartManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        binding.homeThermometerNtsAverageChart.setTabIndex(0, true)
        binding.homeThermometerNtsAbnormalChart.setTabIndex(0, true)
        binding.homeThermometerNtsNormalChart.setTabIndex(0, true)

        customChartManager = CustomChartManager.getInstance(requireContext())
        setThermometer7DayAverageChart()
        setThermometer7DayAbnormalChart()
        setThermometer7DayNormalChart()

        binding.homeThermometerBtnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeThermometerBtnBack -> {
                val mActivity = activity as HomeActivity
                mActivity.changeFragmentTransactionNoAnimation(HomeDashboardFragment())
            }
        }
    }

    //?????? ?????? ??????
    private fun setThermometer7DayAverageChart() {
        val thermometerCandleLineData =  customChartManager.setThermometer7DayCandleLineData()
        val candleData = CandleData(thermometerCandleLineData.candleDataSet)
        val candleThermometerDay = binding.homeThermometerAverageChart

        val lineData = LineData(thermometerCandleLineData.lineDataSet)
        val combinedData = CombinedData()
        combinedData.setData(candleData)
        combinedData.setData(lineData)

        //?????? ??? ??????
        val markerView = CustomMarkerViewManager(context, R.layout.custom_marker_view)

        //?????? ????????? ??????
        candleThermometerDay.setOnChartValueSelectedListener(this)
        candleThermometerDay.run {
            setScaleEnabled(false) //?????? ??? ????????????
            data = combinedData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //?????? ??? ??? ?????????
            isDragEnabled = true    //????????? ??????
            isScaleXEnabled = false //?????? ?????? ?????????
            enableScroll()
            setVisibleXRangeMaximum(7f) //
            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //?????? ?????? X???
                setDrawGridLines(false)   //?????? ????????? ??????
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = CustomChartManager.CustomDateXAxisFormatter()
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x????????? ?????? ??????
//                animateXY(1000, 1000)
            }
            axisLeft.run { //?????? Y???
                setDrawAxisLine(false)  //?????? ??? ?????????
                axisMinimum = 32F   //?????????
                axisMaximum = 42F   //?????????
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y????????? ?????? ??????
            }
            axisRight.run { //????????? y??????
                isEnabled = false  //????????? y??? ?????????
            }
            legend.run {
                isEnabled = true //????????? ????????? ??????
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

    //?????? ?????? ??????
    private fun setThermometer7DayAbnormalChart() {
        val thermometerBarData =  customChartManager.setThermometerAbnormalData()
        val barData = BarData(thermometerBarData)
        val barThermometerDay = binding.homeThermometerAbnormalChart


        //?????? ??? ??????
        val markerView = CustomMarkerViewManager(context, R.layout.custom_marker_view)

        barThermometerDay.run {
            setScaleEnabled(false) //?????? ??? ????????????
            data = barData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //?????? ??? ??? ?????????
            isScaleXEnabled = false //?????? ?????? ?????????
//            isDragEnabled = true    //????????? ??????
//            enableScroll()
//            setVisibleXRangeMaximum(7f)
            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //?????? ?????? X???
                setDrawGridLines(false)   //?????? ????????? ??????
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x????????? ?????? ??????
//                animateXY(1000, 1000)
            }
            axisLeft.run { //?????? Y???
                setDrawAxisLine(false)  //?????? ??? ?????????
                axisMinimum = 0F   //?????????
                axisMaximum = 20F   //?????????
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y????????? ?????? ??????
            }
            axisRight.run { //????????? y??????
                isEnabled = false  //????????? y??? ?????????
            }
            legend.run {
                isEnabled = true //????????? ????????? ??????
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

    //?????? ?????? ??????
    private fun setThermometer7DayNormalChart() {
        val thermometerBarData =  customChartManager.setThermometerNormalData()
        val barData = BarData(thermometerBarData)
        val barThermometerDay = binding.homeThermometerNormalChart
//        val combinedData = LineData()
//        lineThermometerDay.setData(lineData)

        //?????? ??? ??????
        val markerView = CustomMarkerViewManager(context, R.layout.custom_marker_view)

        barThermometerDay.run {
            setScaleEnabled(false) //?????? ??? ????????????
            data = barData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //?????? ??? ??? ?????????
            isScaleXEnabled = false //?????? ?????? ?????????
//            isDragEnabled = true    //????????? ??????
//            enableScroll()
//            setVisibleXRangeMaximum(7f)
            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //?????? ?????? X???
                setDrawGridLines(false)   //?????? ????????? ??????
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x????????? ?????? ??????
//                animateXY(1000, 1000)
            }
            axisLeft.run { //?????? Y???
                setDrawAxisLine(false)  //?????? ??? ?????????
                axisMinimum = 0F   //?????????
                axisMaximum = 20F   //?????????
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y????????? ?????? ??????
            }
            axisRight.run { //????????? y??????
                isEnabled = false  //????????? y??? ?????????
            }
            legend.run {
                isEnabled = true //????????? ????????? ??????
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

    //????????? ????????? ??? ?????? ?????????
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e is CandleEntry) {
            val minVal = min(e.low, e.high)
            val maxVal = max(e.low, e.high)

            binding.homeThermometerTextViewMinValue.text = "$minVal"
            binding.homeThermometerTextViewMaxValue.text = "$maxVal"

//            binding.homeAnalysisThermometerTextViewMinValue.setTextColor(ContextCompat.getColor(R.color.))
        }
    }

    override fun onNothingSelected() {
    }
}