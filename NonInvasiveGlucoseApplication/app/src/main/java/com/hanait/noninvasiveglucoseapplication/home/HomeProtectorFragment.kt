package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeProtectorBinding
import com.hanait.noninvasiveglucoseapplication.model.ProtectorData
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment

class HomeProtectorFragment : BaseFragment<FragmentHomeProtectorBinding>(FragmentHomeProtectorBinding::inflate) {
    var protectorList: ArrayList<ProtectorData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        //타이틀 다시 표시
        val mActivity = activity as HomeActivity
        mActivity.setTitleVisible(true, "등록된 번호")

        protectorList.add(ProtectorData("010-4054-7199"))
        protectorList.add(ProtectorData("010-4567-1234"))
        protectorList.add(ProtectorData("010-2307-5423"))
        protectorList.add(ProtectorData("010-2345-3456"))
        protectorList.add(ProtectorData("010-6543-2456"))

        recyclerViewCreate()
    }

    private fun recyclerViewCreate() {
        val protectorRecyclerView = binding.homeProtectorRecyclerView
        val protectorAdapter = ProtectorAdapter(requireContext(), protectorList)
        //클릭 이벤트 리스너 처리
        val layoutManager = LinearLayoutManager(context)
        protectorRecyclerView.layoutManager = layoutManager
        protectorRecyclerView.adapter = protectorAdapter
    }
}