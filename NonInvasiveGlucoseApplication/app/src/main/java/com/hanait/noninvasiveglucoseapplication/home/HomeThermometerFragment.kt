package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
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
    private val customChartManager by lazy { CustomChartManager.getInstance(requireContext())}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        binding.homeThermometerNtsAverageChart.setTabIndex(0, true)
        binding.homeThermometerNtsAbnormalChart.setTabIndex(0, true)
        binding.homeThermometerNtsNormalChart.setTabIndex(0, true)

        //초기 글라이드로 이미지 불러오기
        setImageViewWithGlide()

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

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(requireContext())
        glide.load(R.drawable.background_image_detail).into(binding.homeThermometerImageViewDetailBackground)
        glide.load(R.drawable.ic_baseline_arrow_back_24).into(binding.homeThermometerImageViewBack)
    }

    //체온 차트 설정
    private fun setThermometer7DayAverageChart() {
        val thermometerCandleLineData =  customChartManager.setThermometer7DayCandleLineData()
        val candleData = CandleData(thermometerCandleLineData.candleDataSet)
        val candleThermometerDay = binding.homeThermometerAverageChart

        val lineData = LineData(thermometerCandleLineData.lineDataSet)
        val combinedData = CombinedData()
        combinedData.setData(candleData)
        combinedData.setData(lineData)

        //마커 뷰 설정
        val markerView = CustomMarkerViewManager(context, R.layout.custom_marker_view)

        //클릭 리스너 설정
        candleThermometerDay.setOnChartValueSelectedListener(this)
        candleThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = combinedData
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
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
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
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y그리드 색깔 변경
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

    //체온 차트 설정
    private fun setThermometer7DayAbnormalChart() {
        val thermometerBarData =  customChartManager.setThermometerAbnormalData()
        val barData = BarData(thermometerBarData)
        val barThermometerDay = binding.homeThermometerAbnormalChart


        //마커 뷰 설정
        val markerView = CustomMarkerViewManager(context, R.layout.custom_marker_view)

        barThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = barData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isScaleXEnabled = false //가로 확대 없애기
//            isDragEnabled = true    //드래그 가능
//            enableScroll()
//            setVisibleXRangeMaximum(7f)
            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 0F   //최소값
                axisMaximum = 20F   //최대값
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y그리드 색깔 변경
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

    //체온 차트 설정
    private fun setThermometer7DayNormalChart() {
        val thermometerBarData =  customChartManager.setThermometerNormalData()
        val barData = BarData(thermometerBarData)
        val barThermometerDay = binding.homeThermometerNormalChart
//        val combinedData = LineData()
//        lineThermometerDay.setData(lineData)

        //마커 뷰 설정
        val markerView = CustomMarkerViewManager(context, R.layout.custom_marker_view)

        barThermometerDay.run {
            setScaleEnabled(false) //핀치 줌 안되도록
            data = barData
            description.isEnabled = false
            isDoubleTapToZoomEnabled = false   //더블 탭 줌 불가능
            isScaleXEnabled = false //가로 확대 없애기
//            isDragEnabled = true    //드래그 가능
//            enableScroll()
//            setVisibleXRangeMaximum(7f)
            marker = markerView
//            moveViewToX(3f);
            xAxis.run { //아래 라벨 X축
                setDrawGridLines(false)   //배경 그리드 추가
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                valueFormatter = CustomChartManager.CustomTimeXAxisFormatter()
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
//                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_100)  //x그리그 색깔 변경
//                animateXY(1000, 1000)
            }
            axisLeft.run { //왼쪽 Y축
                setDrawAxisLine(false)  //좌측 선 없애기
                axisMinimum = 0F   //최소값
                axisMaximum = 20F   //최대값
                isEnabled = true
                animateX(1000)
                animateY(1000)
                textSize = 12f
                textColor = ContextCompat.getColor(requireContext(), R.color.toss_black_700)
                gridColor = ContextCompat.getColor(requireContext(), R.color.toss_black_150)    //y그리드 색깔 변경
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

    //그래프 터치시 값 변경 리스너
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