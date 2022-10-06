package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.hanait.noninvasiveglucoseapplication.util.Constants.NAVER_SERVICE_ID
import com.hanait.noninvasiveglucoseapplication.util.Constants.NAVER_SMS_FROM_NUMBER
import okhttp3.MediaType
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun makeSignature(): String {
        val timestamp = System.currentTimeMillis()
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

    fun makeBodyRequest(phoneNumber: String, content: String) : RequestBody {
        val bodyJson = JSONObject()
        try {
            bodyJson.put("type", "SMS")
            bodyJson.put("from", NAVER_SMS_FROM_NUMBER)
            bodyJson.put("content", content)
            val message = JSONArray()
            val attributes = JSONObject()
            attributes.put("to", phoneNumber)
            message.put(attributes)
            bodyJson.put("messages", message)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val bodyJsonString = bodyJson.toString()
        return RequestBody.create("application/json".toMediaTypeOrNull(), bodyJsonString)
    }
}