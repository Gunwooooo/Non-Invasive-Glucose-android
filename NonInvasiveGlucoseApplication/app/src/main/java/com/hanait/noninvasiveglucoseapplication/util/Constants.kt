package com.hanait.noninvasiveglucoseapplication.util

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.hanait.noninvasiveglucoseapplication.db.PreferenceManager
import com.hanait.noninvasiveglucoseapplication.model.BodyData
import com.hanait.noninvasiveglucoseapplication.model.UserData
import java.util.*


object Constants {
    //회원가입 시 데이터 부분 저장
    lateinit var _userData: UserData

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //자동 로그인 변수 저장
    lateinit var _prefs: PreferenceManager

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // 네이버 API 정보
    const val NAVER_SERVICE_ID = "ncp:sms:kr:276214123809:hanait-noti"
    //String NAVER_SERVICE_ID = Uri.encode("ncp:sms:kr:276214123809:hanait-noti");
    val NAVER_SERVICE_SECRET_KEY = "ccf5e31ae7cb497caa96238d5a482425"
//    var NAVER_ACCESS_KEY = "8qM8InIbLpyV8LC5jNaH"
    const val NAVER_ACCESS_KEY = "93D6C30F9DF6A199DD16"
//    var NAVER_SECRET_KEY = "Gc1g48vw82OSiSziZsnHjxPHZSbXDPOxJbwP9v89"
    const val NAVER_SECRET_KEY = "414021434924B06DEA23C608C08258A2E8E523DB"
    const val NAVER_SMS_FROM_NUMBER = "0537453860"

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //프로필 사진 경로
    const val PROFILE_IMAGE_NAME = "_profile.png"

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //가트 연결 UUID
    val UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
    val TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    //장치 이름
    const val DEVICE_NAME = "HANAIT_BPM&TEMP"

    //스캔 시간
    const val SCAN_PERIOD: Long = 10000

    //블루투스 통신 주기 전역 변수
    var _checkBluetoothTimer = false

    //데이터 인덱스 값
    var _entryIndex = 0f

    //블루투스 장치 연결 GATT
    lateinit var bluetoothResultDevice : BluetoothDevice

    //통신으로 실시간으로 받을 badyDataArray
    val _bodyDataArrayList = ArrayList<BodyData>()

    //Gatt 연결 상태 확인 변수
    var _bluetoothGattConnected = false
}

