package com.hanait.noninvasiveglucoseapplication.retrofit

import com.google.gson.JsonElement
import com.hanait.noninvasiveglucoseapplication.model.UserData
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
        @Header("Authorization") authorization : String?
    ): Call<ResponseBody>

    //회원 탈퇴
    @POST("/wellink/user/delete")
    fun deleteLoginedUser(
        @Header("Authorization") authorization : String?
    ): Call<ResponseBody>

    @PUT("/wellink/user/edit")
    fun editLoginedUser(
        @Header("Authorization") authorization: String?,
        @Body userData: UserData
    ): Call<ResponseBody>

    @POST("/logout")
    fun logoutUser(
        @Header("Authorization") authorization: String?
    ) : Call<ResponseBody>

    //현재 비밀번호 확인
    @FormUrlEncoded
    @POST("/wellink/user/checkpw")
    fun checkCurrentPassword(
        @Header("Authorization") authorization: String?,
        @Field("password") password: String?,
        @Field("newPassword") newPassword: String?
    ) : Call<ResponseBody>
}