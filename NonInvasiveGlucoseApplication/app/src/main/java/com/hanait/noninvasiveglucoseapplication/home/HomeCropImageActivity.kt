package com.hanait.noninvasiveglucoseapplication.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.ActivityHomeCropImageBinding
import com.hanait.noninvasiveglucoseapplication.util.Constants.PROFILE_IMAGE_NAME
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

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

            val croppedImage = resizeBitmapImage(result.bitmap)

            //이미지 캐쉬 폴더에 쓰기
            val fileName = LoginedUserClient.phoneNumber + PROFILE_IMAGE_NAME
            //파일명 : 전화번호_시간_profile.png
            //이미지 저장 시 시간이 걸리므로 코루틴을 이용
            CoroutineScope(Dispatchers.IO).launch {
                saveImageToFile(croppedImage, fileName)
                //캐쉬 파일 전달하기
                intent.putExtra("imageName", fileName)
                setResult(RESULT_OK, intent)
                finish()
            }
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

    //이미지 크기 resize
    private fun resizeBitmapImage(bitmapImage: Bitmap): Bitmap {
        val width = bitmapImage.width
        val height = bitmapImage.height
        val scaleWidth = 224f / width
        val scaleHeight = 224f / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmapImage, 0, 0, width, height, matrix, false)
    }

    //이미지 파일로 쓰기
    private suspend fun saveImageToFile(croppedImage : Bitmap, fileName : String) {
        //캐쉬 비우기
        val mCacheFiles = cacheDir.listFiles()
        for (cacheFile in mCacheFiles!!) {
            if (!cacheFile.isDirectory) {
                cacheFile.delete()
            }
        }
        val mFile = File(cacheDir, fileName)
        try {
            mFile.createNewFile()
            val fileOutputStream = FileOutputStream(mFile)
            croppedImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()
        } catch (e: Exception) {
            Log.d("로그", "HomeCropImageActivity - saveImageToFile : 파일 쓰기 에러")
        }
    }
}