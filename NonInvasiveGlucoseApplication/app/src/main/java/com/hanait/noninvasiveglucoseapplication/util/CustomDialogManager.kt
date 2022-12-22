package com.hanait.noninvasiveglucoseapplication.util

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.hanait.noninvasiveglucoseapplication.R


class CustomDialogManager(private val layout: Int) : DialogFragment(), View.OnClickListener {

    private var stringData = ""
//    //원 버튼 다이어로그
//    private var oneButtonDialogListener: OneButtonDialogListener? = null
//    interface OneButtonDialogListener {
//        fun onPositiveClicked()
//    }
//    fun setOneButtonDialogListener(customDialogListener: OneButtonDialogListener) {
//        this.oneButtonDialogListener = customDialogListener
//    }

    //투 버튼 다이어로그
    private var twoButtonDialogListener: TwoButtonDialogListener? = null
    interface TwoButtonDialogListener {
        fun onPositiveClicked()
        fun onNegativeClicked()
    }
    fun setTwoButtonDialogListener(customDialogListener: TwoButtonDialogListener) {
        this.twoButtonDialogListener = customDialogListener
    }

    //String을 전달하는 투 버튼 다이어로그
    private var twoButtonWithDataDialogListener: TwoButtonWithDataDialogListener? = null
    interface TwoButtonWithDataDialogListener {
        fun onPositiveClicked(data: String)
        fun onNegativeClicked()
    }
    fun setTwoButtonWithDataDialogListener(customDialogListener: TwoButtonWithDataDialogListener) {
        this.twoButtonWithDataDialogListener = customDialogListener
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layout, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return view
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(layout, null)
        builder.setView(view)

        var positiveButton : Button? = null
        var negativeButton : Button? = null
        stringData = ""

        when(layout) {
            /////////////////////////////////////////    protector   ///////////////////////////////
            //보호 대상자 삭제 다이어로그
            R.layout.home_protecting_delete_dialog -> {
                positiveButton = view.findViewById(R.id.homeProtectingDeleteDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeProtectingDeleteDialog_btn_negative) as Button
            }
            //보호자 삭제 다이어로그
            R.layout.home_protector_delete_dialog -> {
                positiveButton = view.findViewById(R.id.homeProtectorDeleteDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeProtectorDeleteDialog_btn_negative) as Button
            }
            //보호자, 보호 대상자 정보 보기 다이어로그
            R.layout.home_protector_info_dialog -> {
                positiveButton = view.findViewById(R.id.homeProtectorInfoDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeProtectorInfoDialog_btn_negative) as Button
            }
            //////////////////////////////////////////    Account   /////////////////////////////////////
            //사용자 성별 수정 다이어로그
            R.layout.home_account_modify_sex_dialog -> {
                positiveButton = view.findViewById(R.id.homeAccountModifySexDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeAccountModifySexDialog_btn_negative) as Button
                val maleButton = view.findViewById(R.id.homeAccountModifySexDialog_btn_male) as Button
                val femaleButton = view.findViewById(R.id.homeAccountModifySexDialog_btn_female) as Button
                //성별 토글 기능 구현
                maleButton.setOnClickListener {
                    maleButton.isEnabled = false
                    maleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    femaleButton.isEnabled = true
                    femaleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.toss_black_500))
                    stringData = "남성"
                }
                femaleButton.setOnClickListener {
                    femaleButton.isEnabled = false
                    femaleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    maleButton.isEnabled = true
                    maleButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.toss_black_500))
                    stringData = "여성"
                }
            }
            //비밀번호 수정 다이어로그
            R.layout.home_account_modify_password_dialog -> {
                positiveButton = view.findViewById(R.id.homeAccountModifyPasswordDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeAccountModifyPasswordDialog_btn_negative) as Button
            }
            //회원탈퇴 다이어로그
            R.layout.home_account_delete_user_dialog -> {
                positiveButton = view.findViewById(R.id.homeAccountDeleteUserDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeAccountDeleteUserDialog_btn_negative) as Button
            }
            //로그아웃 다이어로그
            R.layout.home_account_logout_user_dialog -> {
                positiveButton = view.findViewById(R.id.homeAccountLogoutUserDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeAccountLogoutUserDialog_btn_negative) as Button
            }
            //닉네임 변경 다이어로그
            R.layout.home_account_modify_nickname_dialog -> {
                positiveButton = view.findViewById(R.id.homeAccountModifyNicknameDialog_btn_positive) as Button
                negativeButton = view.findViewById(R.id.homeAccountModifyNicknameDialog_btn_negative) as Button
                val nicknameEditText = view.findViewById(R.id.homeAccountModifyNicknameDialog_editText_nickname) as EditText
                //텍스트 감지 후 데이터 저장
                nicknameEditText.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        stringData = nicknameEditText.text.toString()
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun afterTextChanged(s: Editable?) {}
                })
            }
        }
        positiveButton?.setOnClickListener(this)
        negativeButton?.setOnClickListener(this)
        return builder.create()
    }

    //다이어로그 버튼 별 클릭 리스너 등록
    override fun onClick(v: View?) {
        when(v?.id) {
            //투 버튼 파지티브 리스너 연결
            R.id.homeProtectingDeleteDialog_btn_positive, R.id.homeProtectorDeleteDialog_btn_positive,
            R.id.homeAccountModifyPasswordDialog_btn_positive, R.id.homeAccountDeleteUserDialog_btn_positive,
            R.id.homeProtectorInfoDialog_btn_positive, R.id.homeAccountLogoutUserDialog_btn_positive
            -> twoButtonDialogListener?.onPositiveClicked()

            //투 버튼 네가티브 리스너 연결
            R.id.homeProtectingDeleteDialog_btn_negative, R.id.homeProtectorDeleteDialog_btn_negative,
            R.id.homeAccountModifyPasswordDialog_btn_negative, R.id.homeAccountDeleteUserDialog_btn_negative,
            R.id.homeProtectorInfoDialog_btn_negative, R.id.homeAccountLogoutUserDialog_btn_negative
            -> twoButtonDialogListener?.onNegativeClicked()

            
            //데이터 전달이 있는 투 버튼 리스너 연결
            R.id.homeAccountModifySexDialog_btn_positive, R.id.homeAccountModifyNicknameDialog_btn_positive
            -> twoButtonWithDataDialogListener?.onPositiveClicked(stringData)
            R.id.homeAccountModifySexDialog_btn_negative, R.id.homeAccountModifyNicknameDialog_btn_negative
            -> twoButtonWithDataDialogListener?.onNegativeClicked()
        }
    }
}