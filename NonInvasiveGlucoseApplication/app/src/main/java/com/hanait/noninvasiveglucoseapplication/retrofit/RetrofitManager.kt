package com.hanait.noninvasiveglucoseapplication.retrofit

import com.hanait.noninvasiveglucoseapplication.retrofit.API.BASE_URL
import okhttp3.Callback
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class RetrofitManager {

    companion object{
        val instance = RetrofitManager()
    }

    private val apiNaverCloudService: APINaverCloudService? = RetrofitClient.getClient(BASE_URL)?.create(APINaverCloudService::class.java)

    public fun sendSMS(timestamp: String, accessKey: String, signature: String, serviceId: String, body: RequestBody, completion: (CompletionResponse, String) -> Unit) {
        val call = apiNaverCloudService?.sendSMS(timestamp, accessKey, signature, serviceId, body) ?: return
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