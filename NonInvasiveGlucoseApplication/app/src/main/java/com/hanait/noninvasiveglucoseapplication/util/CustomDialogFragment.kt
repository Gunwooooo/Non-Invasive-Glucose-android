package com.hanait.noninvasiveglucoseapplication.util

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.hanait.noninvasiveglucoseapplication.R


class CustomDialogFragment(private val layout: Int) : DialogFragment(), View.OnClickListener {

    //보호자 삭제 다이어로그
    private var protectingDeleteListener: DeleteDialogListener? = null
    interface DeleteDialogListener {
        fun onPositiveClicked()
        fun onNegativeClicked()
    }
    fun setDialogListener(customDialogListener: DeleteDialogListener) {
        this.protectingDeleteListener = customDialogListener
    }

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
        when(layout) {
            //보호 대상자 삭제 다이어로그
            R.layout.home_protecting_delete_dialog -> {
                val positiveButton = view.findViewById(R.id.homeProtectingDeleteDialog_btn_positive) as Button
                val negativeButton = view.findViewById(R.id.homeProtectingDeleteDialog_btn_negative) as Button
                positiveButton.setOnClickListener(this)
                negativeButton.setOnClickListener(this)
            }
            ////////////////////////////////////////////////////////
            //보호자 삭제 다이어로그
            R.layout.home_protector_delete_dialog -> {
                val positiveButton = view.findViewById(R.id.homeProtectorDeleteDialog_btn_positive) as Button
                val negativeButton = view.findViewById(R.id.homeProtectorDeleteDialog_btn_negative) as Button
                positiveButton.setOnClickListener(this)
                negativeButton.setOnClickListener(this)
            }
        }
        return builder.create()
    }

    //다이어로그 버튼 별 클릭 리스너 등록
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.homeProtectingDeleteDialog_btn_positive, R.id.homeProtectorDeleteDialog_btn_positive ->
                protectingDeleteListener?.onPositiveClicked()
            R.id.homeProtectingDeleteDialog_btn_negative, R.id.homeProtectorDeleteDialog_btn_negative -> 
                protectingDeleteListener?.onNegativeClicked()
        }
    }

}