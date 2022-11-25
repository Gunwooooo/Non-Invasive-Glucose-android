package com.hanait.noninvasiveglucoseapplication.user

import android.os.Bundle
import android.view.View
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentAgreement2Binding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._prevFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants._progressBar
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
        _prevFragment = UserSetAgreementFragment()
        _progressBar.visibility = View.GONE
    }
}