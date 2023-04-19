package com.example.newnoninvasiveglucoseapplication.retrofit

import android.util.Log
import com.example.newnoninvasiveglucoseapplication.model.BodyData
import com.example.newnoninvasiveglucoseapplication.model.ProtectorData
import com.example.newnoninvasiveglucoseapplication.util.LoginedUserClient
import com.example.newnoninvasiveglucoseapplication.model.UserData
import com.example.newnoninvasiveglucoseapplication.retrofit.API.NAVER_SMS_URL
import com.example.newnoninvasiveglucoseapplication.retrofit.API.PHR_BASE_URL
import com.example.newnoninvasiveglucoseapplication.util.Constants.NAVER_ACCESS_KEY
import com.example.newnoninvasiveglucoseapplication.util.Constants.NAVER_SERVICE_ID
import okhttp3.MultipartBody
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
    private val apiPHRService: APIPHRService? = RetrofitClient.getPHRServiceClient(PHR_BASE_URL + "connection/")?.create(APIPHRService::class.java)


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
        val call = apiPHRService?.infoLoginedUser() ?: return
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
        val call = apiPHRService?.deleteLoginedUser() ?: return
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
    fun modifyLoginedUser(userData: UserData, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.modifyLoginedUser(userData) ?: return
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
        val call = apiPHRService?.checkAndModifyCurrentPassword(password, newPassword) ?: return
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

    //프로필 이미지 변경
    fun modifyProfileImage(profileImage : MultipartBody.Part, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.modifyProfileImage(profileImage) ?: return
        call.enqueue(object  : Callback<ResponseBody> {
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
        val call = apiPHRService?.checkJoinedProtector(phoneNumber) ?: return
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
        val call = apiPHRService?.addProtector(userData) ?: return
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //보호자 리스트 조회
    fun getProtectorList(completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getProtectorList() ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //보호 대상자 리스트 조회
    fun getProtectingList(completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getProtectingList() ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //호보자 삭제
    fun deleteProtector(id : Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.deleteProtector(id) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //보호 대상자 삭제
    fun deleteProtecting(id : Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.deleteProtecting(id) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //건강 데이터 추가
    fun addBodyData(bodyDataArrayList: ArrayList<BodyData>, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.addBodyData(bodyDataArrayList) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //날짜로 건강 데이터 조회
    fun getBodyDataAsDate(phoneNumber: String, year: Int, month: Int, day: Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getBodyDataAsDate(phoneNumber, year, month, day) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //분석 평균 체온 데이터 가져오기(7, 30, 90)
    fun getAnalysisThermometerAverage(phoneNumber: String, day: Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getAnalysisThermometerAverage(phoneNumber, day - 1) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //정상 범위 체온 빈도수 가져오기
    fun getAnalysisThermometerNormal(phoneNumber: String, day: Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getAnalysisThermometerNormal(phoneNumber, day - 1) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //비정상 범위 체온 빈도수 가져오기
    fun getAnalysisThermometerAbnormal(phoneNumber: String, day: Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getAnalysisThermometerAbnormal(phoneNumber, day - 1) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //분석 평균 심박수 데이터 가져오기(7, 30, 90)
    fun getAnalysisHeartAverage(phoneNumber: String, day: Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getAnalysisHeartAverage(phoneNumber, day - 1) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //정상 범위 심박수 빈도수 가져오기
    fun getAnalysisHeartNormal(phoneNumber: String, day: Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getAnalysisHeartNormal(phoneNumber, day - 1) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //비정상 범위 심박수 빈도수 가져오기
    fun getAnalysisHeartAbnormal(phoneNumber: String, day: Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getAnalysisHeartAbnormal(phoneNumber, day - 1) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //분석 평균 혈당 데이터 가져오기(7, 30, 90)
    fun getAnalysisGlucoseAverage(phoneNumber: String, day: Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getAnalysisGlucoseAverage(phoneNumber, day - 1) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //정상 범위 혈당 빈도수 가져오기
    fun getAnalysisGlucoseNormal(phoneNumber: String, day: Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getAnalysisGlucoseNormal(phoneNumber, day - 1) ?: return
        call.enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                completion(CompletionResponse.OK, response)
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                completion(CompletionResponse.FAIL, null)
            }
        })
    }

    //비정상 범위 혈당 빈도수 가져오기
    fun getAnalysisGlucoseAbnormal(phoneNumber: String, day: Int, completion: (CompletionResponse, Response<ResponseBody>?) -> Unit) {
        val call = apiPHRService?.getAnalysisGlucoseAbnormal(phoneNumber, day - 1) ?: return
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