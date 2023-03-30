package com.example.newnoninvasiveglucoseapplication.retrofit

object API {

    const val PHR_BASE_URL = "http://192.168.1.186:8080/"
    const val PHR_PROFILE_BASE_URL = "http://192.168.1.186:8080/wellink/images/"
    const val NAVER_SMS_URL = "https://sens.apigw.ntruss.com/"

    const val SEND_SMS = "sms/v2/services/{serviceId}/messages"
}

enum class CompletionResponse {
    OK,
    FAIL 
}
