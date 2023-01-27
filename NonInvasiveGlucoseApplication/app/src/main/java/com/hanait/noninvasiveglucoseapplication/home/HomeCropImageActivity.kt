package com.hanait.noninvasiveglucoseapplication.home

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeCropImageBinding
import com.theartofdev.edmodo.cropper.CropImageView

class HomeCropImageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHomeCropImageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //스테이터스바 색상 변경
        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.black)

        val imageUri = intent.extras?.get("imageUri") as Uri
        Log.d("로그", "CropImageActivity - onCreate : uri : $imageUri")
        val cropImageView = binding.cropImageView
        cropImageView.run {
            setImageUriAsync(imageUri)
            guidelines = CropImageView.Guidelines.ON
            isAutoZoomEnabled = true
            cropShape = CropImageView.CropShape.OVAL
            setFixedAspectRatio(true)
            setBackgroundColor(ContextCompat.getColor(baseContext, R.color.black))
        }

//        val intent = Intent(this, HomeAccountActivity::class.java)
//        setResult(RESULT_OK, intent)
    }
}