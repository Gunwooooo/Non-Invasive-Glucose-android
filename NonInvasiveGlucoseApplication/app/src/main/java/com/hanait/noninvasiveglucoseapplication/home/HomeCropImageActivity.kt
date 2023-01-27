package com.hanait.noninvasiveglucoseapplication.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeCropImageBinding
import com.theartofdev.edmodo.cropper.CropImageView

class HomeCropImageActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by lazy { ActivityHomeCropImageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()

    }
    private fun init() {
        //스테이터스바 색상 변경
        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.black)

        //초기 이미지 넣기
        setCropImageView()

        binding.homeCropImageTextViewSelect.setOnClickListener(this)
        binding.homeCropImageTextViewCancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.homeCropImageTextViewSelect -> {
                //이미지 자르기 완료
                val cropImageView = binding.homeCropImageCropImageView
                cropImageView.getCroppedImageAsync()
            }
            binding.homeCropImageTextViewCancel -> {
                finish()
            }
        }
    }

    //초기 이미지 넣기
    private fun setCropImageView() {
        val imageUri = intent.extras?.get("imageUri") as Uri
        val cropImageView = binding.homeCropImageCropImageView

        //자르기 완료 리스너
        cropImageView.setOnCropImageCompleteListener { view, result ->
            val intent = Intent(baseContext, HomeAccountActivity::class.java)
            val croppedImageBitmap = result.bitmap
            Log.d("로그", "HomeCropImageActivity - setCropImageView : ${result.bitmap.byteCount}")
//            intent.putExtra("croppedImage", croppedImageBitmap)
//            setResult(RESULT_OK, intent)
//            finish()
        }

        //크롭이미지뷰 셋팅
        cropImageView.run {
            setImageUriAsync(imageUri)
            guidelines = CropImageView.Guidelines.ON
            isAutoZoomEnabled = true
            isShowProgressBar = true
            cropShape = CropImageView.CropShape.OVAL
            scaleType = CropImageView.ScaleType.FIT_CENTER
            setFixedAspectRatio(true)
            setBackgroundColor(ContextCompat.getColor(baseContext, R.color.black))
        }
    }


}