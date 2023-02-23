package com.hanait.noninvasiveglucoseapplication.util

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.BluetoothGatt.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.ListFragment
import java.security.Provider
import java.util.*


class CustomBluetoothManager(context: Context, bluetoothAdapter: BluetoothAdapter?) : ListFragment() {
    val mContext = context
    private val mBluetoothAdapter = bluetoothAdapter

    //스캔 시간
    private val SCAN_PERIOD: Long = 10000
    private val DEVICE_NAME = "HANAIT_BPM&TEMP"
    private var mScanning: Boolean = false
    private var findDeviceFlag = false

    //가트 연결 변수
    var bluetoothGatt: BluetoothGatt? = null

    //가트 연결 UUID
    var UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
    var TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    var CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

////////////////////////////////////////////블루투스 스캔////////////////////////////////////////////
    //블루투스 스캔 콜백 함수
    private val mLeScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val deviceName = result.device.name
            //한아아이티 기기 발견 시
            if(deviceName != null && deviceName.equals(DEVICE_NAME)) {
                findDeviceFlag = true
                Log.d("로그", "CustomBluetoothManager - onScanResult : 발견 됨~")
                //스캔 종료
                scanLeDevice(false)
                //디바이스 연결 / 가트에 정보 넣기
                bluetoothGatt = result.device.connectGatt(mContext, false, gattCallback)
            }
        }
        override fun onBatchScanResults(results: List<ScanResult>?) {
            super.onBatchScanResults(results)
            for(i in 0 until results!!.size ) {
                Log.d("로그", "CustomBluetoothManager - onBatchScanResults : $i  :  ${results.get(i)}")
            }
        }
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d("로그", "CustomBluetoothManager - onScanFailed : 스캔 실패")
        }
    }

    @SuppressLint("MissingPermission")
    fun scanLeDevice(enable : Boolean) {
        findDeviceFlag = false
        val bluetoothLeScanner = mBluetoothAdapter!!.bluetoothLeScanner
        when(enable) {
            true -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    mScanning = false
                    bluetoothLeScanner.stopScan(mLeScanCallback)
                    if(!findDeviceFlag) {
                        Toast.makeText(mContext, "장치를 발견하지 못했어요.", Toast.LENGTH_SHORT).show()
                    }
                }, SCAN_PERIOD)
                mScanning = true
                Log.d("로그", "CustomBluetoothManager - scanLeDevice : 스캔 시작")
                bluetoothLeScanner.startScan(mLeScanCallback)
            }
            else -> {
                mScanning = false
                bluetoothLeScanner.stopScan(mLeScanCallback)
            }
        }
    }
///////////////////////////////////////블루투스 GATT 연결////////////////////////////////////////////

    //가트 콜백 생성
    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when(newState) {
                STATE_CONNECTED -> {
                    //서비스디스커버 호출하기
                    Log.d("로그", "CustomBluetoothManager - onConnectionStateChange : 가트 서버 연결됨 : ${bluetoothGatt!!.discoverServices()}")
                }
                STATE_DISCONNECTED -> {
                    Log.d("로그", "CustomBluetoothManager - onConnectionStateChange : 가트 서버에서 연결 해제됨")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            when(status) {
                GATT_SUCCESS -> {
                    Log.d("로그", "CustomBluetoothManager - onServicesDiscovered : 연결 성공함")
//                    Log.d("로그", "CustomBluetoothManager - onServicesDiscovered : ${bluetoothGatt.ge}")
                    var str = gatt!!.getService(UART_UUID).getCharacteristic(TX_CHAR_UUID)
                    Log.d("로그", "CustomBluetoothManager - onServicesDiscovered : $str")
                    if (bluetoothGatt!!.setCharacteristicNotification(str, true)) {
                        val descriptor = str.getDescriptor(CCCD_UUID)
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        bluetoothGatt!!.writeDescriptor(descriptor)
                        broadcastUpdate("커넥티드 한아아이티 기계")
                    }
                }
                GATT_FAILURE -> {
                        broadcastUpdate("커넥티드 실패")
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            val data = characteristic!!.value
            Log.d("로그", "CustomBluetoothManager - onCharacteristicChanged : ${String(data)}")
        }

        private  fun broadcastUpdate(str:String) {
            val mHandler : Handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
                }
            }
            mHandler.obtainMessage().sendToTarget()
        }
    }
}