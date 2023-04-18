package com.example.newnoninvasiveglucoseapplication.retrofit

object API {

    const val PHR_BASE_URL = "https://hanait.kro.kr:8888/"
    const val PHR_PROFILE_BASE_URL = "https://hanait.kro.kr:8888/wellink/images/"
    const val NAVER_SMS_URL = "https://sens.apigw.ntruss.com/"

    const val SEND_SMS = "sms/v2/services/{serviceId}/messages"
}

enum class CompletionResponse {
    OK,
    FAIL 
}
