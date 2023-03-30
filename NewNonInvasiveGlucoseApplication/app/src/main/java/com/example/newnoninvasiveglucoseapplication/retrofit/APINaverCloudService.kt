package com.example.newnoninvasiveglucoseapplication.retrofit

import com.google.gson.JsonElement
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface APINaverCloudService {

//    @FormUrlEncoded
//    @POST(API.JOIN_USER)
//    fun joinUser(
//        @Field("uid") uid:String,
//        @Field("password") password:String,
//        @Field("name") name:String,
//        @Field("protector_check") protector_check:Boolean,
//        @Field("protector_id") protector_id:String,
//    ): Call<JsonElement>

    @Headers("Content-Type: application/json; charset=utf-8")
    @POST(API.SEND_SMS)
    fun sendSMS(
        @Header("x-ncp-apigw-timestamp") timestamp: String,
        @Header("x-ncp-iam-access-key") accessKey: String,
        @Header("x-ncp-apigw-signature-v2") signature: String,
        @Path("serviceId") serviceId: String,
        @Body body: RequestBody
    ): Call<ResponseBody>

//    @FormUrlEncoded
//    @POST(API.LOGIN_USER)
//    fun loginUser(
//        @Field("uid") uid:String,
//        @Field("password") password:String,
//    ): Call<TokenUser>

//    @GET(API.INFO_USER)
//    fun infoUser(
//        @Header("token") token:String,
//        @Query("uid") uid:String,
//    ): Call<User>
}