package com.example.newnoninvasiveglucoseapplication.util

import com.example.newnoninvasiveglucoseapplication.util.Constants

//로그인 된 유저 토큰
object LoginedUserClient {
    var authorization: String? = ""
    var refreshToken: String? = ""
    var accessTokenExp: Long? = 0
    var refreshTokenExp: Long? = 0
//    var nickname: String? = ""
    var phoneNumber: String? = ""
//    var sex: String? = ""
//    var birthDay : String? = ""
//    var createdDate: String? = ""
}