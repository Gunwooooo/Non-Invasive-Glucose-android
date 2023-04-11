package com.example.newnoninvasiveglucoseapplication.user

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.newnoninvasiveglucoseapplication.R
import com.example.newnoninvasiveglucoseapplication.databinding.FragmentUserSetConnectDeviceBinding
import com.example.newnoninvasiveglucoseapplication.home.HomeActivity
import com.example.newnoninvasiveglucoseapplication.util.BaseFragment
import com.example.newnoninvasiveglucoseapplication.util.Constants.DEVICE_NAME
import com.example.newnoninvasiveglucoseapplication.util.Constants.SCAN_PERIOD
import com.example.newnoninvasiveglucoseapplication.util.Constants._bluetoothResultDevice
import com.example.newnoninvasiveglucoseapplication.util.CustomDialogManager
import java.util.*

class UserSetConnectDeviceFragment : BaseFragment<FragmentUserSetConnectDeviceBinding>(FragmentUserSetConnectDeviceBinding::inflate), View.OnClickListener {
    private val customProgressDialog by lazy { CustomDialogManager(requireContext(), R.layout.common_progress_dialog, null) }
    //블루투스 매니저로 어댑터 선언 / 비동기화 환경에서 선언
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    //블루투스 활성화 확인
    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    //블루투스 권한 요청 resultLauncher
    private lateinit var activityResultLauncherBluetooth: ActivityResultLauncher<Intent>
    //위치 권한 요청 resultLauncher
    private lateinit var locationPermissionRequest : ActivityResultLauncher<Array<String>>

    //기기 연결 확인 변수
    private var findDeviceFlag = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        //액션바 안보이게 하기(뒤로가기)
        val mActivity = activity as UserActivity
        mActivity.setBtnBackVisible(View.INVISIBLE)
        mActivity.setProgressDialogValueAndVisible(99, View.GONE)
        mActivity.setPrevFragment(UserSetAgreementFragment())

        //텍스트에 밑줄 넣기
        binding.userSetConnectDeviceTextViewSkipConnect.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.userSetConnectDeviceLottie.setOnClickListener(this)

        //키보드 넣기
        if (activity != null && requireActivity().currentFocus != null) {
            // 프래그먼트기 때문에 getActivity() 사용
            val inputManager: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

        //블루투스 연결 콜백, 위치 서비스 콜백 초기화
        setActivityResultLauncher()
        setLocationPermissionRequest()

        //블루투스 연결 체크, 위치서비스 체크
        checkBluetoothEnable()

        checkLocationEnable()
    }

    override fun onClick(v: View?) {
        when(v){
            binding.userSetConnectDeviceLottie -> {
                //블루투스 스캔 시작
                scanLeDevice(true)
            }
            binding.userSetConnectDeviceLayoutSkipConnect -> {

            }
        }
    }

    //블루투스 권한 체크
    private fun checkBluetoothEnable() {
        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activityResultLauncherBluetooth.launch(enableBtIntent)
        }
    }

    private fun checkLocationEnable() {
        locationPermissionRequest.launch((arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)))
    }

    //블루투스 설정  resultLauncher 초기화
    private fun setActivityResultLauncher() {
        //갤러리 콜백
        activityResultLauncherBluetooth =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                //블루투스 호출 반환
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    Toast.makeText(requireContext(), "블루투스 ON", Toast.LENGTH_SHORT).show()
                }
            }
    }
    //위치 서비스 콜백 초기화
    private fun setLocationPermissionRequest() {
        locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
                }
            }
        }
    }

    ////////////////////////////////////////////블루투스 스캔////////////////////////////////////////////
    //블루투스 스캔 콜백 함수
    private val mLeScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val deviceName = result.device.name
            //한아아이티 기기 발견 시
            Log.d("로그", "UserSetConnectDeviceFragment - onScanResult : 디바이스 : $deviceName")
            if(deviceName != null && deviceName.equals(DEVICE_NAME)) {
                Log.d("로그", "UserSetConnectDeviceFragment - onScanResult : 장치 발견됨!")
                //디바이스 정보 전역 변수에 넣기
                _bluetoothResultDevice = result.device
                
                //발견됨 플래그
                findDeviceFlag = true
                //스캔 종료
                scanLeDevice(false)

                //홈액티비티 실행
                startActivity(Intent(context, HomeActivity::class.java))
                val mActivity = activity as UserActivity
                mActivity.finish()
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
        //어댑터가 disable 상태이면 재 선언
        if(bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter
        }
        val bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner
        when(enable) {
            true -> {
                //1분동안 스캔하기
                Handler(Looper.getMainLooper()).postDelayed({
                    customProgressDialog.dismiss()

                    bluetoothLeScanner.stopScan(mLeScanCallback)
                    if(!findDeviceFlag) {
                        Toast.makeText(requireContext(), "장치를 발견하지 못했어요.", Toast.LENGTH_SHORT).show()
                    }
                }, SCAN_PERIOD)
                Log.d("로그", "UserSetConnectDeviceFragment - scanLeDevice : 스캔 시작")
                findDeviceFlag = false
                //로딩 다이어로그 출력
                customProgressDialog.show(childFragmentManager, "common_progress_dialog")
                //스캔 시작
                bluetoothLeScanner.startScan(mLeScanCallback)
            }
            else -> {
                Log.d("로그", "UserSetConnectDeviceFragment - scanLeDevice : 스캔 종료")
                customProgressDialog.dismiss()
                bluetoothLeScanner.stopScan(mLeScanCallback)
            }
        }
    }


    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }
}