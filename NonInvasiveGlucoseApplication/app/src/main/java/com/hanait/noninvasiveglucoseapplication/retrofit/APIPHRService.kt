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
    fun loginUser(@Body userData: UserData): Call<ResponseBody>

    //회원가입
    @POST("/api/v1/join")
    fun joinUser(@Body userData: UserData): Call<ResponseBody>

    //가입된 유저 체크
    @POST("/api/v1/check")
    fun checkJoinedUser(@Body userData: UserData): Call<ResponseBody>
}