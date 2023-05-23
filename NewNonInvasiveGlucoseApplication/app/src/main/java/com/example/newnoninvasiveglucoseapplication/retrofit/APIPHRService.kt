package com.example.newnoninvasiveglucoseapplication.retrofit

import com.google.gson.JsonElement
import com.example.newnoninvasiveglucoseapplication.model.BodyData
import com.example.newnoninvasiveglucoseapplication.model.UserData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface APIPHRService {

    //로그인
    @POST("/login")
    fun loginUser(
        @Body userData: UserData
    ): Call<ResponseBody>

    //회원가입
    @POST("/wellink/user/join")
    fun joinUser(
        @Body userData: UserData
    ): Call<ResponseBody>

    //가입된 유저 체크(처음에 등록된 전화번호인지 확인)
    @POST("/wellink/user/check")
    fun checkJoinedUser(
        @Body userData: UserData
    ): Call<ResponseBody>

    //로그인 된 회원 정보 조회
    @POST("/wellink/user/info")
    fun infoLoginedUser(
        @Header("refresh_token") refreshToken : String?,
    ): Call<ResponseBody>

    //회원 탈퇴
    @POST("/wellink/user/delete")
    fun deleteLoginedUser(
    ): Call<ResponseBody>

    //회원정보 수정
    @PUT("/wellink/user/edit")
    fun modifyLoginedUser(
        @Body userData: UserData
    ): Call<ResponseBody>

    //현재 비밀번호 확인 및 변경
    @FormUrlEncoded
    @POST("/wellink/user/checkpw")
    fun checkAndModifyCurrentPassword(
        @Field("password") password: String?,
        @Field("newPassword") newPassword: String?
    ) : Call<ResponseBody>

    //현재 비밀번호 잊어버렸을 경우 변경
    @FormUrlEncoded
    @POST("/wellink/user/findpw")
    fun modifyForgottenPassword(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("password") password: String?
    ) : Call<ResponseBody>

    //프로필 사진 등록
    @Multipart
    @POST("/wellink/user/profileImg")
    fun modifyProfileImage(
        @Part profileImage: MultipartBody.Part
    ) : Call<ResponseBody>
    ////////////////////////////////////////////////////////////////////////////////////

    //보호자 조회
    @GET("/wellink/caregiver/check/{phoneNumber}")
    fun checkJoinedProtector(
        @Path("phoneNumber") phoneNumber: String?
    ): Call<ResponseBody>

    //보호자 등록
    @POST("/wellink/caregiver/join")
    fun addProtector(
        @Body userData: UserData?
    ): Call<ResponseBody>

    //보호자 리스트 조회
    @POST("/wellink/caregiver/caregiverList")
    fun getProtectorList(
    ) : Call<ResponseBody>

    //보호 대상자 리스트 조회
    @POST("/wellink/caregiver/userList")
    fun getProtectingList(
    ): Call<ResponseBody>

    //보호자 삭제
    @FormUrlEncoded
    @POST("/wellink/caregiver/caregiverDelete")
    fun deleteProtector(
        @Field("id") id: Int?
    ): Call<ResponseBody>    
    
    //보호 대상자 삭제
    @FormUrlEncoded
    @POST("/wellink/caregiver/userCaregiverDelete")
    fun deleteProtecting(
        @Field("id") id: Int?
    ): Call<ResponseBody>

    ///////////////////////////////////////////////////////////////////////////////////////////////

    //건강 데이터 추가
    @POST("/wellink/bodydata")
    fun addBodyData(
        @Body bodyDataArrayList: ArrayList<BodyData>
    ): Call<ResponseBody>

    //건강 데이터 가져오기
    @FormUrlEncoded
    @POST("/wellink/calendar")
    fun getBodyDataAsDate(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("year") year: Int?,
        @Field("month") month: Int?,
        @Field("day") day: Int?,
    ): Call<ResponseBody>

    //분석 평균 체온 데이터 가져오기
    @FormUrlEncoded
    @POST("/wellink/bodydata/thermometer/avg")
    fun getAnalysisThermometerAverage(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("day") day: Int?,
    ): Call<ResponseBody>

    //정상 범위 체온 빈도수 가져오기
    @FormUrlEncoded
    @POST("/wellink/bodydata/thermometer/count")
    fun getAnalysisThermometerNormal(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("day") day: Int?,
    ): Call<ResponseBody>

    //비정상 범위 체온 빈도수 가져오기
    @FormUrlEncoded
    @POST("/wellink/bodydata/thermometer/countOther")
    fun getAnalysisThermometerAbnormal(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("day") day: Int?,
    ): Call<ResponseBody>

    //분석 평균 심박수 데이터 가져오기
    @FormUrlEncoded
    @POST("/wellink/bodydata/heart/avg")
    fun getAnalysisHeartAverage(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("day") day: Int?,
    ): Call<ResponseBody>

    //정상 범위 심박수 빈도수 가져오기
    @FormUrlEncoded
    @POST("/wellink/bodydata/heart/count")
    fun getAnalysisHeartNormal(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("day") day: Int?,
    ): Call<ResponseBody>
    
    //비정상 범위 심박수 빈도수 가져오기
    @FormUrlEncoded
    @POST("/wellink/bodydata/heart/countOther")
    fun getAnalysisHeartAbnormal(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("day") day: Int?,
    ): Call<ResponseBody>

    //분석 평균 혈당 데이터 가져오기
    @FormUrlEncoded
    @POST("/wellink/bodydata/glucose/avg")
    fun getAnalysisGlucoseAverage(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("day") day: Int?,
    ): Call<ResponseBody>

    //정상 범위 혈당 빈도수 가져오기
    @FormUrlEncoded
    @POST("/wellink/bodydata/glucose/count")
    fun getAnalysisGlucoseNormal(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("day") day: Int?,
    ): Call<ResponseBody>

    //비정상 범위 혈당 빈도수 가져오기
    @FormUrlEncoded
    @POST("/wellink/bodydata/glucose/countOther")
    fun getAnalysisGlucoseAbnormal(
        @Field("phoneNumber") phoneNumber: String?,
        @Field("day") day: Int?,
    ): Call<ResponseBody>

    //달력에 데이터 있는 날짜를 표시하기 위해 데이터 있는 날짜 가져오기
    @FormUrlEncoded
    @POST("/wellink/calendar/hasDataMonth")
    fun getDataExistDates(
        @Field("phoneNumber") phoneNumber: String?,
    ): Call<ResponseBody>
}