package com.hanait.noninvasiveglucoseapplication.retrofit

import com.hanait.noninvasiveglucoseapplication.retrofit.API.BASE_URL
import com.hanait.noninvasiveglucoseapplication.retrofit.API.URL_NAVER_SMS
import com.hanait.noninvasiveglucoseapplication.util.Constants
import com.hanait.noninvasiveglucoseapplication.util.Constants.NAVER_ACCESS_KEY
import com.hanait.noninvasiveglucoseapplication.util.Constants.NAVER_SERVICE_ID
import okhttp3.Callback
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {

    companion object{
        val instance = RetrofitManager()
    }

    private val apiNaverCloudService: APINaverCloudService? = RetrofitClient.getClient(URL_NAVER_SMS)?.create(APINaverCloudService::class.java)

    //인증번호 전송
    public fun sendSMS(timestamp: String, signature: String, body: RequestBody, completion: (CompletionResponse, String) -> Unit) {
        val call = apiNaverCloudService?.sendSMS(timestamp, NAVER_ACCESS_KEY, signature, NAVER_SERVICE_ID, body) ?: return
        call.enqueue(object: retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response.body().toString())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, t.toString())
            }
        })
    }

//    fun joinUser(user: UserData, completion: (CompletionResponse, String) -> Unit){
//        val call = iRetrofit?.joinUser(user.uid, user.password, user.name, user.protector_check, user.protector_id) ?: return
//
//        call.enqueue(object: Callback<JsonElement>{
//            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
//                completion(CompletionResponse.FAIL, t.toString())
//            }
//
//            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
//                if(response.code() != 201){
//                    completion(CompletionResponse.FAIL, response.body().toString())
//                }else{
//                    completion(CompletionResponse.OK, response.body().toString())
//                }
//            }
//        })
//    }
}