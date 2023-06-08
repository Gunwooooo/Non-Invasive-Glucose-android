package com.example.newnoninvasiveglucoseapplication.retrofit

import android.util.Log
import com.example.newnoninvasiveglucoseapplication.util.LoginedUserClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.math.exp


object RetrofitClient {
    //네이버 클라우드 서비스 클라이언트
    fun getNaverCloudServiceClient(baseUrl: String): Retrofit?{
        var retrofitClient: Retrofit? = null
        val client = OkHttpClient.Builder()

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("로그", "//로깅 인터셉터 >> $message")
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
            Log.d("로그", "로깅 인터셉터 >> $message")
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
                val accessTokenExp = LoginedUserClient.accessTokenExp?.times(1000)
                //만기일 체크
                Log.d("로그", "RetrofitClient - getPHRServiceClient : ${System.currentTimeMillis()}  -  ${accessTokenExp}")
                //AT토큰 만료 체크
                if(System.currentTimeMillis() >= accessTokenExp!!) {
                    Log.d("로그", "RetrofitClient - getPHRServiceClient : AT토큰이 만료되었습니다!!!")
                    //refresh token 만기 시간을 지났는지 체크하기
                    val refreshTokenExp = LoginedUserClient.refreshTokenExp?.times(1000)
                    if(System.currentTimeMillis() >= refreshTokenExp!!) {
                        //로그아웃 하여 초기화면으로 이동
                        val intent =
                        Log.d("로그", "RetrofitClient - getPHRServiceClient : 리프레쉬 토큰 만기됨!!!")
                    } else {
                        //리프레쉬 토큰도 함께 보내기
                        //AT 재발급 받기
                        Log.d("로그", "RetrofitClient - getPHRServiceClient : 리프레쉬 토큰은 만기 안됨!!!")
                    }
//                    LoginedUserClient.authorization = null
//                    newRequest = chain.request().newBuilder().addHeader("Authorization", accessToken).build()
                } else {
                    Log.d("로그", "RetrofitClient - getPHRServiceClient : AT토큰 만료 안됨~~")
                }
            } else {
                //토큰이 없는 경우
                newRequest = chain.request()
            }
            Log.d("로그", "RetrofitClient - getPHRServiceClient : 새로운 리퀘스트 : $newRequest")
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