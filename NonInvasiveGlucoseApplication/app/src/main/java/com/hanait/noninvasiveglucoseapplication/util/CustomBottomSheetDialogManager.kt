package com.hanait.noninvasiveglucoseapplication.util

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hanait.noninvasiveglucoseapplication.R

class CustomBottomSheetDialogManager: BottomSheetDialogFragment() {

    //투 버튼 다이어로그
    private var bottomSheetDialogListener: BottomSheetDialogListener? = null
    interface BottomSheetDialogListener {
        fun onSearchClicked(phoneNumber: String)
    }
    fun setBottomSheetDialogListener(customDialogListener: BottomSheetDialogListener) {
        this.bottomSheetDialogListener = customDialogListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        //키보드 올리기

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.home_protector_search_bottom_sheet_dialog, container, false)
        val editText = view?.findViewById(R.id.homeProtectorSearchBottomSheetDialog_editText) as EditText

        //키보드 올리기
        if (editText.requestFocus()) {
            // 프래그먼트기 때문에 getActivity() 사용
            val inputManager: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }

        editText.setOnEditorActionListener(object: TextView.OnEditorActionListener {
            //에딧 텍스트 검색 아이콘 클릭 이벤트 처리
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    bottomSheetDialogListener?.onSearchClicked(editText.text.toString())
                    editText.setText("")
                    return true
                }
                return false
            }
        })
        return view
    }
}