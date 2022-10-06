package com.hanait.noninvasiveglucoseapplication.retrofit

object API {
    const val BASE_URL = "http://192.168.1.40:8000/"

    const val SEND_SMS = "sms/v2/services/{serviceId}/messages"
}

enum class CompletionResponse {
    OK,
    FAIL
}
