package com.example.newnoninvasiveglucoseapplication.user

import android.os.Bundle
import android.view.View
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentAgreement1Binding
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import java.io.IOException
import java.io.InputStream


class Agreement1Fragment : BaseFragment<FragmentAgreement1Binding>(FragmentAgreement1Binding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        try {
            val agreement1: InputStream = resources.openRawResource(R.raw.term)
            val b = ByteArray(agreement1.available())
            agreement1.read(b)
            val s = String(b)
            binding.agreement1TextView.text = s
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun init() {
        val mActivity = activity as UserActivity
        mActivity.setProgressDialogValueAndVisible(100, View.GONE)
        mActivity.setPrevFragment(UserSetAgreementFragment())
    }
}