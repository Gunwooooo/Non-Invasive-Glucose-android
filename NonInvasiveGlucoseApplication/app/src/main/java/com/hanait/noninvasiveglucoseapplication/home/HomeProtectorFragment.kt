package com.hanait.noninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeProtectorBinding
import com.hanait.noninvasiveglucoseapplication.model.ProtectorData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogManager

class HomeProtectorFragment : BaseFragment<FragmentHomeProtectorBinding>(FragmentHomeProtectorBinding::inflate), View.OnClickListener {
    var protectingList: ArrayList<ProtectorData> = ArrayList()
    var protectorList: ArrayList<ProtectorData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        protectingList.add(ProtectorData("010-****-7199", "김건우"))
        protectingList.add(ProtectorData("010-****-1234", "손흥민"))
        protectingList.add(ProtectorData("010-****-5423", "황의조"))
        protectingList.add(ProtectorData("010-****-3456", "황희찬"))
        protectingList.add(ProtectorData("010-****-2456", "백승호"))
        protectingList.add(ProtectorData("010-****-7654", "이강인"))
        protectingList.add(ProtectorData("010-****-4324", "김진수"))
        protectingList.add(ProtectorData("010-****-6432", "권창훈"))

        protectorList.add(ProtectorData("010-****-7199", "김건우"))
        protectorList.add(ProtectorData("010-****-1234", "손흥민"))
        protectorList.add(ProtectorData("010-****-5423", "황의조"))
        protectorList.add(ProtectorData("010-****-3456", "황희찬"))

        binding.homeProtectorTextViewProtectingCount.text = "${protectingList.size}명"
        binding.homeProtectorTextViewProtectorCount.text = "${protectorList.size}명"

        recyclerViewCreate()

//        binding.homeProtectorBtnAdd.setOnClickListener(this)
        binding.homeProtectorBtnDelete.setOnClickListener(this)
        binding.homeProtectorLayoutProtectorInfo.setOnClickListener(this)
        binding.homeProtectorLayoutProtectorAdd.setOnClickListener(this)
    }

    private fun recyclerViewCreate() {
        val protectingRecyclerView = binding.homeProtectorRecyclerViewProtecting
        val protectingAdapter = ProtectorAdapter(requireContext(), protectingList)
        //클릭 이벤트 리스너 처리
        protectingAdapter.setOnItemClickListener(object : ProtectorAdapter.OnItemClickListener {
            override fun onInfoItem(v: View, pos: Int) {
                showInfoProtectorDialog()
            }

            override fun onDeleteItem(v: View, pos: Int) {
                showDeleteProtectingDialog(protectingAdapter, pos, false)
            }
        })

        val layoutManagerProtecting = LinearLayoutManager(context)
        protectingRecyclerView.layoutManager = layoutManagerProtecting
        protectingRecyclerView.adapter = protectingAdapter

        //보호자 리스트 생성
        val protectorRecyclerView = binding.homeProtectorRecyclerViewProtector
        val protectorAdapter = ProtectorAdapter(requireContext(), protectorList)
        //클릭 이벤트 리스너 처리
        protectorAdapter.setOnItemClickListener(object : ProtectorAdapter.OnItemClickListener {
            override fun onInfoItem(v: View, pos: Int) {
                showInfoProtectorDialog()
            }

            override fun onDeleteItem(v: View, pos: Int) {
                showDeleteProtectingDialog(protectorAdapter, pos, true)
            }
        })
        val layoutManagerProtector = LinearLayoutManager(context)
        protectorRecyclerView.layoutManager = layoutManagerProtector
        protectorRecyclerView.adapter = protectorAdapter
    }

    override fun onClick(v: View?) {
        when(v) {
            //바텀 시트 다이어로그 호출
            binding.homeProtectorLayoutProtectorAdd -> {
                showBottomSheetDialog()
            }
            binding.homeProtectorBtnDelete -> {
                showDeleteProtectorDialog()
            }
            binding.homeProtectorLayoutProtectorInfo -> {
                showInfoProtectorDialog()
            }
        }
    }

    //바텀 시트 다이어로그 호출(보호자 조회)
    private fun showBottomSheetDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.home_protector_search_bottom_sheet_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        //검색 클릭 이벤트 설정
        val editText = bottomSheetView.findViewById(R.id.homeProtectorSearchBottomSheetDialog_editText) as EditText
        editText.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            //데잇 텍스트 검색 아이콘 클릭 이벤트 처리
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                bottomSheetDialog.dismiss()
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Toast.makeText(requireContext(), "클릭됨", Toast.LENGTH_SHORT).show()

                    //보호자 조회 retrofit 통신 호출
                    retrofitCheckJoinedProtector(editText.text.toString())
                    return true
                }
                return false
            }
        })

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()
    }

    //보호자, 보호 대상자 정보 다이어로그 호출
    private fun showInfoProtectorDialog() {
        val customDialog = CustomDialogManager(R.layout.home_protector_info_dialog)
        customDialog.setTwoButtonDialogListener(object : CustomDialogManager.TwoButtonDialogListener{
            override fun onPositiveClicked() {
                Log.d("로그", "HomeProtectorFragment - onPositiveClicked : 사용자 건강정보 조회 버튼 클릭")
                customDialog.dismiss()
            }
            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(childFragmentManager, "home_protector_info_dialog")
    }

    //보호자 삭제 다이어로그 호출
    private fun showDeleteProtectorDialog() {
        val customDialog = CustomDialogManager(R.layout.home_protector_delete_dialog)
        customDialog.setTwoButtonDialogListener(object : CustomDialogManager.TwoButtonDialogListener{
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
    fun showDeleteProtectingDialog(protectorAdapter: ProtectorAdapter, pos: Int, isProtector: Boolean) {
        val customDialog = CustomDialogManager(R.layout.home_protecting_delete_dialog)
        customDialog.setTwoButtonDialogListener(object : CustomDialogManager.TwoButtonDialogListener{
            @SuppressLint("SetTextI18n")
            //보호 대상자 리스트에서 삭제
            override fun onPositiveClicked() {
                if(isProtector) {
                    protectorList.removeAt(pos - 1)
                    protectorAdapter.notifyItemRemoved(pos - 1)
                    protectorAdapter.notifyItemRangeChanged(pos -1, protectorList.size)
                    binding.homeProtectorTextViewProtectorCount.text = "${protectorList.size}명"
                }
                else {
                    protectingList.removeAt(pos - 1)
                    protectorAdapter.notifyItemRemoved(pos - 1)
                    protectorAdapter.notifyItemRangeChanged(pos -1, protectingList.size)
                    binding.homeProtectorTextViewProtectingCount.text = "${protectingList.size}명"
                }
                customDialog.dismiss()
            }

            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(childFragmentManager, "home_protecting_delete_dialog")
    }
    //////////////////////////////////////////////////////////////////////////////////////////////
    
    //보호자 조회 retrofit 통신
    private fun retrofitCheckJoinedProtector(phoneNumber: String) {
        RetrofitManager.instance.checkJoinedProtector(phoneNumber, completion = {
            completionResponse, response -> 
            when(completionResponse) {
                CompletionResponse.OK -> {
                    Log.d("로그", "HomeProtectorFragment - retrofitCheckJoinedProtector : $response")
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeProtectorFragment - retrofitCheckJoinedProtector : 통신 실패")
                }
            }
        })
    }
}