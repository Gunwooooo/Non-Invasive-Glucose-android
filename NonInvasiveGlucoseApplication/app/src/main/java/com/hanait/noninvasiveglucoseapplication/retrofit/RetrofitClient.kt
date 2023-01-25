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

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(
                "로그",
                "RetrofitClient - log : $message"
            )
        }

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        client.addInterceptor(loggingInterceptor)

        //토큰 체크 인터셉터 추가
        val tokenCheckInterceptor = Interceptor { chain ->
            val accessToken = LoginedUserClient.authorization
            val newRequest: Request
            //토큰이 있는 경우
            if(accessToken != null && accessToken != "") {
                //헤더에 추가
                newRequest = chain.request().newBuilder().addHeader("Authorization", accessToken).build()
            } else {
                //토큰이 없는 경우
                newRequest = chain.request()
            }
            Log.d("로그", "RetrofitClient - getPHRServiceClient : 새로운 리퀘스트 : ${newRequest}")
            chain.proceed(newRequest)
        }

        client.interceptors().add(tokenCheckInterceptor)
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