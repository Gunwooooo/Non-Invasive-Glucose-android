package com.hanait.noninvasiveglucoseapplication.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hanait.noninvasiveglucoseapplication.R


class BottomSheetDialogFragment(val itemClick: (Int) -> Unit) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvRecommentOrder = view.findViewById<TextView>(R.id.tv_recommend_order)
        val tvReviewOrder = view.findViewById<TextView>(R.id.tv_review_order)
        tvRecommentOrder.setOnClickListener {
            itemClick(0)
            dialog?.dismiss()
        }
        tvReviewOrder.setOnClickListener {
            itemClick(1)
            dialog?.dismiss()
        }
    }
}