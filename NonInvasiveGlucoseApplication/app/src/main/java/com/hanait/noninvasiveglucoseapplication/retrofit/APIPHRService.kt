package com.hanait.noninvasiveglucoseapplication.retrofit

import com.google.gson.JsonElement
import com.hanait.noninvasiveglucoseapplication.model.UserData
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
    @POST(".")
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
}