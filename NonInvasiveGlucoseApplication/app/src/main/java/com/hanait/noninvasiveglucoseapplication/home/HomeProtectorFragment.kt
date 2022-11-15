package com.hanait.noninvasiveglucoseapplication.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeProtectorBinding
import com.hanait.noninvasiveglucoseapplication.model.ProtectorData
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogFragment

class HomeProtectorFragment : BaseFragment<FragmentHomeProtectorBinding>(FragmentHomeProtectorBinding::inflate), View.OnClickListener {
    var protectorList: ArrayList<ProtectorData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        //타이틀 다시 표시
        val mActivity = activity as HomeActivity
        mActivity.setTitleVisible(true, "나의 보호자")

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
                showDialog(protectorAdapter, pos)
            }
        })

        val layoutManager = LinearLayoutManager(context)
        protectorRecyclerView.layoutManager = layoutManager
        protectorRecyclerView.adapter = protectorAdapter
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeProtectorBtnAdd -> {
                val bottomSheetView = layoutInflater.inflate(R.layout.fragment_bottom_sheet_dialog, null)
                val bottomSheetDialog = BottomSheetDialog(requireContext())
                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetDialog.show()
            }
            binding.homeProtectorBtnDelete -> {
                showDeleteProtectorDialog(c)
            }
        }
    }

    //삭제 다이어로그 호출
    fun showDeleteProtectorDialog(protectorAdapter: ProtectorAdapter, pos: Int) {
        val customDialog = CustomDialogFragment(R.layout.home_protector_delete_dialog)
        customDialog.setDialogListener(object : CustomDialogFragment.DeleteDialogListener{
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
        customDialog.show(childFragmentManager, "home_alarm_delete_dialog")
    }

    //삭제 다이어로그 호출
    fun showDeleteProtectingDialog(protectorAdapter: ProtectorAdapter, pos: Int) {
        val customDialog = CustomDialogFragment(R.layout.home_protector_delete_dialog)
        customDialog.setDialogListener(object : CustomDialogFragment.DeleteDialogListener{
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
        customDialog.show(childFragmentManager, "home_alarm_delete_dialog")
    }
}