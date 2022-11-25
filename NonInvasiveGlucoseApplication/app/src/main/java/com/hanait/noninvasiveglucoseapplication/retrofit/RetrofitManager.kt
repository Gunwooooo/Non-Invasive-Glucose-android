package com.hanait.noninvasiveglucoseapplication.retrofit

import android.util.Log
import com.hanait.noninvasiveglucoseapplication.model.UserData
import com.hanait.noninvasiveglucoseapplication.retrofit.API.NAVER_SMS_URL
import com.hanait.noninvasiveglucoseapplication.retrofit.API.PHR_BASE_URL
import com.hanait.noninvasiveglucoseapplication.util.Constants.NAVER_ACCESS_KEY
import com.hanait.noninvasiveglucoseapplication.util.Constants.NAVER_SERVICE_ID
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class RetrofitManager {

    companion object{
        val instance = RetrofitManager()
    }

    private val apiNaverCloudService: APINaverCloudService? = RetrofitClient.getNaverCloudServiceClient(NAVER_SMS_URL)?.create(APINaverCloudService::class.java)
    private val apiPHRService: APIPHRService? = RetrofitClient.getPHRServiceClient(PHR_BASE_URL)?.create(APIPHRService::class.java)


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //네이버 노티 서비스 인증번호 전송
    fun sendSMS(timestamp: String, signature: String, body: RequestBody, completion: (CompletionResponse, String) -> Unit) {
        val call = apiNaverCloudService?.sendSMS(timestamp, NAVER_ACCESS_KEY, signature, NAVER_SERVICE_ID, body) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("로그", "RetrofitManager - onResponse : ${response.body()}")
                completion(CompletionResponse.OK, response.body().toString())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("로그", "RetrofitManager - onFailure : ${t}")
                completion(CompletionResponse.FAIL, t.toString())
            }
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //회원 가입
    fun joinUser(userData: UserData, completion: (CompletionResponse, String) -> Unit) {
        val call = apiPHRService?.joinUser(userData) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("로그", "RetrofitManager - onResponse : ${response}")
                Log.d("로그", "RetrofitManager - onResponse : ${response.body().toString()}")
                if(response.code() != 200) {
                    completion(CompletionResponse.FAIL, "잘못된 요청, 에러 코드 확인")
                    return
                }
                completion(CompletionResponse.OK, response.body().toString())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("로그", "RetrofitManager - onFailure : ${t}")
                completion(CompletionResponse.FAIL, "통신 실패")
            }
        })
    }

    //회원 로그인
    fun loginUser(userData: UserData, completion: (CompletionResponse, String) -> Unit) {
        val call = apiPHRService?.loginUser(userData) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code() != 200) {
                    completion(CompletionResponse.FAIL, "잘못된 요청, 에러 코드 확인")
                    return
                }
                completion(CompletionResponse.OK, response.body().toString())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, "통신 실패")
            }
        })
    }


    //회원 로그인
    fun findAllUser(userData: UserData, completion: (CompletionResponse, String) -> Unit) {
        val call = apiPHRService?.findAllUser(userData) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.code() != 200) {
                    completion(CompletionResponse.FAIL, "잘못된 요청, 에러 코드 확인")
                    return
                }
                completion(CompletionResponse.OK, response.body().toString())
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, "통신 실패")
            }
        })
    }
}