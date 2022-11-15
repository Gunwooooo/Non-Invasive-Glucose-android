package com.hanait.noninvasiveglucoseapplication.util

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.hanait.noninvasiveglucoseapplication.R


class CustomDialogFragment(private val layout: Int) : DialogFragment(), View.OnClickListener {

    //보호자 삭제 다이어로그
    private var protectorDeleteListener: DeleteDialogListener? = null
    interface DeleteDialogListener {
        fun onPositiveClicked()
        fun onNegativeClicked()
    }
    fun setDialogListener(customDialogListener: DeleteDialogListener) {
        this.protectorDeleteListener = customDialogListener
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
            R.layout.home_protector_delete_dialog -> {
                val positiveButton = view.findViewById(R.id.homeProtectorDeleteDialog_btn_positive) as Button
                val negativeButton = view.findViewById(R.id.homeProtectorDeleteDialog_btn_negative) as Button
                positiveButton.setOnClickListener(this)
                negativeButton.setOnClickListener(this)
            }
        }
        return builder.create()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.homeProtectorDeleteDialog_btn_positive -> protectorDeleteListener?.onPositiveClicked()
            R.id.homeProtectorDeleteDialog_btn_negative -> protectorDeleteListener?.onNegativeClicked()
        }
    }

}