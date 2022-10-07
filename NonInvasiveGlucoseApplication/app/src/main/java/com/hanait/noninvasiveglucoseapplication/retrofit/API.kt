package com.hanait.noninvasiveglucoseapplication.retrofit

object API {
    const val BASE_URL = "http://192.168.1.40:8000/"

    //네이버 클라우스 서비스 url
    const val URL_NAVER_SMS = "https://sens.apigw.ntruss.com/"

    const val SEND_SMS = "sms/v2/services/{serviceId}/messages"
}

enum class CompletionResponse {
    OK,
    FAIL
}
