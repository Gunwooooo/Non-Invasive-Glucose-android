package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeProtectorBinding
import com.hanait.noninvasiveglucoseapplication.model.ProtectorData
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogManager

class HomeProtectorFragment : BaseFragment<FragmentHomeProtectorBinding>(FragmentHomeProtectorBinding::inflate), View.OnClickListener {
    var protectorList: ArrayList<ProtectorData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        protectorList.add(ProtectorData("010-****-7199"))
        protectorList.add(ProtectorData("010-****-1234"))
        protectorList.add(ProtectorData("010-****-5423"))
        protectorList.add(ProtectorData("010-****-3456"))
        protectorList.add(ProtectorData("010-****-2456"))
        protectorList.add(ProtectorData("010-****-7654"))
        protectorList.add(ProtectorData("010-****-4324"))
        protectorList.add(ProtectorData("010-****-6432"))

        recyclerViewCreate()

        binding.homeProtectorBtnAdd.setOnClickListener(this)
        binding.homeProtectorBtnDelete.setOnClickListener(this)
    }

    private fun recyclerViewCreate() {
        val protectorRecyclerView = binding.homeProtectorRecyclerView
        val protectorAdapter = ProtectorAdapter(requireContext(), protectorList)
        //클릭 이벤트 리스너 처리
        protectorAdapter.setOnItemClickListener(object : ProtectorAdapter.OnItemClickListener {
            override fun onDeleteItem(v: View, pos: Int) {
                showDeleteProtectingDialog(protectorAdapter, pos)
            }
        })

        val layoutManager = LinearLayoutManager(context)
        protectorRecyclerView.layoutManager = layoutManager
        protectorRecyclerView.adapter = protectorAdapter
    }

    override fun onClick(v: View?) {
        when(v) {
            //바텀 시트 다이어로그 호출
            binding.homeProtectorBtnAdd -> {
                val bottomSheetView = layoutInflater.inflate(R.layout.fragment_bottom_sheet_dialog, null)
                val bottomSheetDialog = BottomSheetDialog(requireContext())
                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetDialog.show()
            }
            binding.homeProtectorBtnDelete -> {
                showDeleteProtectorDialog()
            }
        }
    }

    //보호자 삭제 다이어로그 호출
    private fun showDeleteProtectorDialog() {
        val customDialog = CustomDialogManager(R.layout.home_protector_delete_dialog)
        customDialog.setDialogListener(object : CustomDialogManager.DeleteDialogListener{
            override fun onPositiveClicked() {
                Log.d("로그", "HomeProtectorFragment - onPositiveClicked : 예 버튼 클릭")
                customDialog.dismiss()
            }

            override fun onNegativeClicked() {
                Log.d("로그", "HomeProtectorFragment - onNegativeClicked : 아니오 버튼 클릭")
                customDialog.dismiss()
            }
        })
        customDialog.show(childFragmentManager, "home_protector_delete_dialog")
    }

    //보호 대상자 삭제 다이어로그 호출
    fun showDeleteProtectingDialog(protectorAdapter: ProtectorAdapter, pos: Int) {
        val customDialog = CustomDialogManager(R.layout.home_protecting_delete_dialog)
        customDialog.setDialogListener(object : CustomDialogManager.DeleteDialogListener{
            override fun onPositiveClicked() {
                protectorList.removeAt(pos - 1)
                protectorAdapter.notifyItemRemoved(pos - 1)
                protectorAdapter.notifyItemRangeChanged(pos -1, protectorList.size)
                customDialog.dismiss()
            }

            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(childFragmentManager, "home_protecting_delete_dialog")
    }
}