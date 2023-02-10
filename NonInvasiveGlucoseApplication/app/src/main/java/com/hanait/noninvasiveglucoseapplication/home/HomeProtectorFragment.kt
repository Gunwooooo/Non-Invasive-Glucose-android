package com.hanait.noninvasiveglucoseapplication.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentHomeProtectorBinding
import com.hanait.noninvasiveglucoseapplication.model.UserData
import com.hanait.noninvasiveglucoseapplication.retrofit.CompletionResponse
import com.hanait.noninvasiveglucoseapplication.retrofit.RetrofitManager
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomBottomSheetDialogManager
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogManager
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit

class HomeProtectorFragment : BaseFragment<FragmentHomeProtectorBinding>(FragmentHomeProtectorBinding::inflate), View.OnClickListener {
    private val customProgressDialog by lazy { CustomDialogManager(R.layout.common_progress_dialog, null) }
    private val bottomSheetDialog by lazy { CustomBottomSheetDialogManager() }

    private var protectingList: ArrayList<UserData> = ArrayList()
    private var protectorList: ArrayList<UserData> = ArrayList()

    private val protectingAdapter by lazy { ProtectorAdapter(requireContext(), protectingList) }
    private val protectorAdapter by lazy { ProtectorAdapter(requireContext(), protectorList) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {

        //글라이드로 모든 이미지 불러오기
        setImageViewWithGlide()

        //보호자 리스트 가져오기
        retrofitGetProtectorList()

        //보호자 임의 데이터 추가
        protectingList.add(UserData("황의조","010-****-5423","", "2019월 10월 21일", "T"))
        protectingList.add(UserData("황희찬","010-****-3456","", "2019월 10월 21일", "T"))

        binding.homeProtectorTextViewProtectingCount.text = "${protectingList.size}"


        recyclerViewCreate()
        binding.homeProtectorLayoutProtectorAdd.setOnClickListener(this)
    }

    //초기 글라이드로 이미지 불러오기
    private fun setImageViewWithGlide() {
        val glide = Glide.with(requireContext())
        glide.load(R.drawable.background_image_protector).into(binding.homeProtectorImageViewProtectorBackground)
        glide.load(R.drawable.icon_color_add).into(binding.homeProtectorImageViewAdd)
    }

    private fun recyclerViewCreate() {
        val protectingRecyclerView = binding.homeProtectorRecyclerViewProtecting

        //클릭 이벤트 리스너 처리
        protectingAdapter.setOnItemClickListener(object : ProtectorAdapter.OnItemClickListener {
            override fun onInfoItem(v: View, pos: Int) {
                showInfoProtectingDialog(protectingList[pos - 1])
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

        //클릭 이벤트 리스너 처리
        protectorAdapter.setOnItemClickListener(object : ProtectorAdapter.OnItemClickListener {
            override fun onInfoItem(v: View, pos: Int) {
                showInfoProtectorDialog(protectorList[pos - 1])
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
        }
    }

    //바텀 시트 다이어로그 호출(보호자 조회)
    private fun showBottomSheetDialog() {
        //검색 클릭 이벤트 설정
        bottomSheetDialog.setBottomSheetDialogListener(object : CustomBottomSheetDialogManager.BottomSheetDialogListener {
            override fun onSearchClicked(phoneNumber: String) {
                Log.d("로그", "HomeProtectorFragment - onSearchClicked : $phoneNumber")
                //자기 번호인지 체크
                if(LoginedUserClient.phoneNumber.equals(phoneNumber)) {
                    Toast.makeText(requireContext(), "자신을 보호자로 설정할 수 없어요.", Toast.LENGTH_SHORT).show()
                    return
                }
                //보호자 검색 retrofit 통신
                retrofitCheckJoinedProtector(phoneNumber)
            }
        })
        bottomSheetDialog.show(requireFragmentManager(), bottomSheetDialog.tag)
    }

    //보호 대상자 정보 다이어로그 호출
    private fun showInfoProtectingDialog(userData: UserData) {
        val customDialog = CustomDialogManager(R.layout.home_protecting_info_dialog, userData)
        customDialog.setTwoButtonDialogListener(object : CustomDialogManager.TwoButtonDialogListener{
            override fun onPositiveClicked() {
                Log.d("로그", "HomeProtectorFragment - onPositiveClicked : 사용자 건강정보 조회 버튼 클릭")
                customDialog.dismiss()
            }
            override fun onNegativeClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(childFragmentManager, "home_protecting_info_dialog")
    }

    //보호자 정보 다이어로그 호출
    private fun showInfoProtectorDialog(userData: UserData) {
        val customDialog = CustomDialogManager(R.layout.home_protector_info_dialog, userData)
        customDialog.setOneButtonDialogListener(object : CustomDialogManager.OneButtonDialogListener{
            override fun onPositiveClicked() {
                customDialog.dismiss()
            }
        })
        customDialog.show(childFragmentManager, "home_protector_info_dialog")
    }

    //보호 대상자 삭제 다이어로그 호출
    fun showDeleteProtectingDialog(protectorAdapter: ProtectorAdapter, pos: Int, isProtector: Boolean) {
        val customDialog = CustomDialogManager(R.layout.home_protecting_delete_dialog, null)
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

    //보호자 정보 조회 다이어로그 호출
    private fun showProtectorSearchInfoDialog(userData: UserData) {
        val customDialog = CustomDialogManager(R.layout.home_protector_search_info_dialog, userData)
        customDialog.setTwoButtonDialogListener(object : CustomDialogManager.TwoButtonDialogListener{
            override fun onPositiveClicked() {
                Log.d("로그", "HomeProtectorFragment - onPositiveClicked : 예 버튼 클릭")

                //보호자 등록 레트로핏 통신
                retrofitAddProtector(userData)

                customDialog.dismiss()
            }

            override fun onNegativeClicked() {
                Log.d("로그", "HomeProtectorFragment - onNegativeClicked : 아니오 버튼 클릭")
                customDialog.dismiss()
            }
        })
        customDialog.show(childFragmentManager, "home_protector_delete_dialog")
    }


    //////////////////////////////////////////////////////////////////////////////////////////////

    //보호자 조회 retrofit 통신
    private fun retrofitCheckJoinedProtector(phoneNumber: String) {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(childFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.checkJoinedProtector(phoneNumber, completion = {
                completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        200 -> {
                            bottomSheetDialog.dismiss()
                            val str = response.body()?.string()
                            Log.d("로그", "HomeProtectorFragment - retrofitCheckJoinedProtector : $str")
                            val jsonObjectUser = str?.let { JSONObject(it) }
                            val phoneNumber_ = jsonObjectUser?.getString("phoneNumber")
                            val nickname = jsonObjectUser?.getString("nickname")
                            val birthDay = jsonObjectUser?.getString("birthDay")
                            val sex = jsonObjectUser?.getString("sex")
                            val userData = UserData(nickname!!, phoneNumber_!!, "", birthDay!!, sex!!)

                            Log.d("로그", "HomeProtectorFragment - retrofitCheckJoinedProtector : 보호자  : ${userData}")
                            //유저 정보 출력 다이어로그 호출
                            showProtectorSearchInfoDialog(userData)
                        }
                        else -> {
                            Toast.makeText(requireContext(), "등록되지 않은 사용자 입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeProtectorFragment - retrofitCheckJoinedProtector : 통신 실패")
                }
            }
        })
    }

    //호보자 등록 레트로핏 통신
    @SuppressLint("SetTextI18n")
    private fun retrofitAddProtector(userData: UserData) {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(childFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.addProtector(userData, completion = { completionResponse, response ->
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        200 -> {
                            //유저 데이터 리스트에 추가
                            protectorList.add(userData)
                            protectorAdapter.notifyItemInserted(protectorList.size)
                            binding.homeProtectorTextViewProtectorCount.text = "${protectorList.size}"
                        }
                    }

                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeProtectorFragment - retrofitJoinProtector : 통신 실패")
                }
            }
        })
    }

    //모든 유저 정보 조회
    private fun retrofitGetProtectorList() {
        //로딩 프로그레스 바 출력
        customProgressDialog.show(childFragmentManager, "common_progress_dialog")
        RetrofitManager.instance.getProtectorList(completion = {
                completionResponse, response ->
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        200 -> {
                            val str = response.body()?.string()
                            val jsonArray = JSONArray(str)

                            //모든 유저 리스트에 넣기
                            for(i in 0 until jsonArray.length()) {
                                val jsonObjectUser = jsonArray.getJSONObject(i).getJSONObject("caregiver")
                                val phoneNumber = jsonObjectUser.getString("phoneNumber")
                                val nickname = jsonObjectUser.getString("nickname")
                                val birthDay = jsonObjectUser.getString("birthDay")
                                val sex = jsonObjectUser.getString("sex")
                                val userData = UserData(nickname, phoneNumber, "", birthDay, sex)
                                protectorList.add(userData)
                            }
                            protectorAdapter.notifyItemInserted(protectorList.size)
                            binding.homeProtectorTextViewProtectorCount.text = "${protectorList.size}"

                            //보호 대상자 리스트 가져오기 호출
                            retrofitGetProtectingList()
                        }
                    }

                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeProtectorFragment - retrofitJoinProtector : 통신 실패")
                }
            }
        })
    }

    //모든 유저 정보 조회
    private fun retrofitGetProtectingList() {
        RetrofitManager.instance.getProtectingList(completion = {
                completionResponse, response ->
            //프로그레스 다이어로그 없애기
            customProgressDialog.dismiss()
            when(completionResponse) {
                CompletionResponse.OK -> {
                    when(response?.code()) {
                        200 -> {
                            val str = response.body()?.string()
//                    val jsonArray = JSONArray(str)

                            Log.d("로그", "HomeProtectorFragment - retrofitGetProtectingList : $str")
                            //모든 유저 리스트에 넣기
//                    for(i in 0 until jsonArray.length()) {
//                        val jsonObjectUser = jsonArray.getJSONObject(i).getJSONObject("caregiver")
//                        val phoneNumber = jsonObjectUser.getString("phoneNumber")
//                        val nickname = jsonObjectUser.getString("nickname")
//                        val birthDay = jsonObjectUser.getString("birthDay")
//                        val sex = jsonObjectUser.getString("sex")
//                        val userData = UserData(nickname, phoneNumber, "", birthDay, sex)
//                        protectorList.add(userData)
//                    }
//                    protectorAdapter.notifyItemInserted(protectorList.size)
//                    binding.homeProtectorTextViewProtectorCount.text = "${protectorList.size}"
                        }
                    }
                }
                CompletionResponse.FAIL -> {
                    Log.d("로그", "HomeProtectorFragment - retrofitJoinProtector : 통신 실패")
                }
            }
        })
    }
}