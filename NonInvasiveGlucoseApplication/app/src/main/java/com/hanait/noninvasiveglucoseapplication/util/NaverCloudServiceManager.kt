package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.hanait.noninvasiveglucoseapplication.user.UserSetAuthorizationFragment.Companion.smsAuthCode
import com.hanait.noninvasiveglucoseapplication.util.Constants.NAVER_SERVICE_ID
import com.hanait.noninvasiveglucoseapplication.util.Constants.NAVER_SMS_FROM_NUMBER
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class NaverCloudServiceManager {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var INSTANCE: NaverCloudServiceManager? = null
        fun getInstance(): NaverCloudServiceManager {
            if (INSTANCE == null) {
                INSTANCE = NaverCloudServiceManager()
            }
            return INSTANCE as NaverCloudServiceManager
        }
    }

    //메시지 전송에 필요한 signature 생성
    @RequiresApi(Build.VERSION_CODES.O)
    fun makeSignature(timestamp: String): String {
        val accessKey = Constants.NAVER_ACCESS_KEY
        val secretKey = Constants.NAVER_SECRET_KEY
        val method = "POST"
        val url = "/sms/v2/services/$NAVER_SERVICE_ID/messages"

        val algorithm = "HmacSHA256"
        val space = " "
        val newLine = "\n"
        val message = method + space + url + newLine + timestamp + newLine + accessKey
        val signingKey = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), algorithm)
        var mac: Mac? = null
        try {
            mac = Mac.getInstance(algorithm)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        try {
            Objects.requireNonNull(mac)?.init(signingKey)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
        val encoder = Base64.getEncoder()
        return encoder.encodeToString(Objects.requireNonNull(mac)?.doFinal(message.toByteArray(StandardCharsets.UTF_8)))
    }

    //인증 번호 생성
    fun makeSMSAuthCode(): String {
        val result = StringBuilder()
        val max = 10
        val length = 6
        val random = Random()
        random.setSeed(Date().time)
        for (i in 0 until length) {
            result.append(random.nextInt(max))
        }
        return result.toString()
    }

    //메시지 body 만들기
    fun makeBodyRequest(phoneNumber: String?, authNumber: String) : RequestBody {
        val bodyJson = JSONObject()
        try {
            bodyJson.put("type", "SMS")
            //bodyJson.put("countryCode", "82");
            bodyJson.put("from", NAVER_SMS_FROM_NUMBER)
            //bodyJson.put("contentType", "COMM");
            bodyJson.put("content", "안녕하세요. 웰링크입니다.\n고객님의 인증번호는 [$authNumber].\n감사합니다.")
            val message = JSONArray()
            val attributes = JSONObject()
            attributes.put("to", phoneNumber)
            //attributes.put("content", content);
            message.put(attributes)
            bodyJson.put("messages", message)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val bodyJsonString = bodyJson.toString()
        return RequestBody.create("application/json".toMediaTypeOrNull(), bodyJsonString)
    }

    //인증 시간 만료 타이머 작동
    fun countDownTimer(millisInFuture: Long, countDownInterval: Long, v: TextView, context: Context) : CountDownTimer {
        var hour : Long = 0
        var minute = ""
        val timer = object : CountDownTimer(millisInFuture, countDownInterval) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val millisInFutureToTime = millisUntilFinished / 1000
                hour = millisInFutureToTime / 60
                minute = String.format("%02d", (millisInFutureToTime % 60))
                v.text = "$hour : $minute"
            }

            override fun onFinish() {
                //결과 인증코드 초기화
                smsAuthCode = ""
                Toast.makeText(context, "인증시간이 만료됐어요. 다시 인증해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        return timer
    }

}