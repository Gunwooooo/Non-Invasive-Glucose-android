package com.hanait.noninvasiveglucoseapplication.retrofit

import android.util.Log
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient
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
    fun loginUser(userData: UserData, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.loginUser(userData) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("로그", "RetrofitManager - onResponse : response.header.authorization : ${response.headers()["Authorization"]}")
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }


    //회원 로그인
    fun checkJoinedUser(userData: UserData, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.checkJoinedUser(userData) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("로그", "RetrofitManager - onResponse : ${response.body()}")
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //로그인 유저 데이터 가져오기
    fun infoLoginedUser(completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.infoLoginedUser(LoginedUserClient.authorization) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //회원 탈퇴
    fun deleteLoginedUser(completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.deleteLoginedUser(LoginedUserClient.authorization) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //회원 정보 수정
    fun editLoginedUser(userData: UserData, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.editLoginedUser(LoginedUserClient.authorization, userData) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //로그아웃
    fun logoutUser(completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.logoutUser(LoginedUserClient.authorization) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //비밀번호 변경 시 현재 비밀번호 체크
    fun checkAndModifyCurrentPassword(password: String, newPassword: String, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.checkAndModifyCurrentPassword(LoginedUserClient.authorization, password, newPassword) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //잊어버린 비밀번호 재설정
    fun modifyForgottenPassword(phoneNumber: String, password: String, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.modifyForgottenPassword(phoneNumber, password) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    //보호자 조회
    fun checkJoinedProtector(phoneNumber: String, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.checkJoinedProtector(LoginedUserClient.authorization, phoneNumber) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //보호자 등록
    fun addProtector(userData: UserData, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.addProtector(LoginedUserClient.authorization, userData) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //모든 유저 정보 조회
    fun infoAllUserList(completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.infoAllUserList(LoginedUserClient.authorization) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }
}