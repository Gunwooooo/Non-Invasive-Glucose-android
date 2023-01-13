package com.hanait.noninvasiveglucoseapplication.retrofit

import android.util.Log
import com.hanait.noninvasiveglucoseapplication.util.LoginedUserClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    //네이버 클라우드 서비스 클라이언트
    fun getNaverCloudServiceClient(baseUrl: String): Retrofit?{
        var retrofitClient: Retrofit? = null
        val client = OkHttpClient.Builder()

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(
                "로그",
                "RetrofitClient - log : $message"
            )
        }

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        client.addInterceptor(loggingInterceptor)

        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        if(retrofitClient == null){
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
        }
        return retrofitClient
    }

    //phr 서비스 클라이언트
    fun getPHRServiceClient(baseUrl: String): Retrofit?{
        var retrofitClient: Retrofit? = null
        val client = OkHttpClient.Builder()

        //로깅 인터셉터 설정
        val loggingInterceptor = HttpLoggingInterceptor { message ->
//            //토큰 시간 만료 체크
//            if(LoginedUserClient.exp != null && LoginedUserClient.exp != 0L) {
//                Log.d("로그", "RetrofitClient - getPHRServiceClient : ${LoginedUserClient.exp}  ㅡ  ${System.currentTimeMillis()}")
//                if(LoginedUserClient.exp!! >= System.currentTimeMillis()) {
//                    Log.d("로그", "RetrofitClient - log : 토큰 시간이 만료됐습니다!")
//                    //refresh token 전송 통신 필요
//                }
//            }
        }
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        client.addInterceptor(loggingInterceptor)

//        val interceptor = Interceptor { chain ->
//            val accessToken = LoginedUserClient.authorization
//            val newRequest: Request
//
//            newRequest = chain.request().newBuilder().addHeader()
//            chain.proceed(newRequest)
//        }

//        client.interceptors().add(interceptor)
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        if(retrofitClient == null){
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
        }
        return retrofitClient
    }

}