package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeProtectorBinding
import com.hanait.noninvasiveglucoseapplication.model.ProtectorData
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment

class HomeProtectorFragment : BaseFragment<FragmentHomeProtectorBinding>(FragmentHomeProtectorBinding::inflate), View.OnClickListener {
    var protectorList: ArrayList<ProtectorData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        //타이틀 다시 표시
        val mActivity = activity as HomeActivity
        mActivity.setTitleVisible(true, "등록된 보호자")

        protectorList.add(ProtectorData("010-****-7199"))
        protectorList.add(ProtectorData("010-****-1234"))
        protectorList.add(ProtectorData("010-****-5423"))
        protectorList.add(ProtectorData("010-****-3456"))
        protectorList.add(ProtectorData("010-****-2456"))
        protectorList.add(ProtectorData("010-****-7654"))
        protectorList.add(ProtectorData("010-****-4324"))
        protectorList.add(ProtectorData("010-****-6432"))

        recyclerViewCreate()

        binding.homeProtectorFloatingButton.setOnClickListener(this)
    }

    private fun recyclerViewCreate() {
        val protectorRecyclerView = binding.homeProtectorRecyclerView
        val protectorAdapter = ProtectorAdapter(requireContext(), protectorList)
        //클릭 이벤트 리스너 처리
        val layoutManager = LinearLayoutManager(context)
        protectorRecyclerView.layoutManager = layoutManager
        protectorRecyclerView.adapter = protectorAdapter
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeProtectorFloatingButton -> {
                val bottomSheetView = layoutInflater.inflate(R.layout.fragment_bottom_sheet_dialog, null)
                val bottomSheetDialog = BottomSheetDialog(requireContext())
                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetDialog.show()
            }
        }
    }
}