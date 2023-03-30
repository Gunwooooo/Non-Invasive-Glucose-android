package com.example.newnoninvasiveglucoseapplication.user

import android.os.Bundle
import android.view.View
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentAgreement2Binding
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import java.io.IOException
import java.io.InputStream


class Agreement2Fragment : BaseFragment<FragmentAgreement2Binding>(FragmentAgreement2Binding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        try {
            val agreement1: InputStream = resources.openRawResource(R.raw.privacy)
            val b = ByteArray(agreement1.available())
            agreement1.read(b)
            val s = String(b)
            binding.agreement2TextView.text = s
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