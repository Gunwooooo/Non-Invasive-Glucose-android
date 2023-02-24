package com.hanait.noninvasiveglucoseapplication.user

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.hanait.noninvasiveglucoseapplication.R
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetConnectDeviceBinding
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.Constants.CCCD_UUID
import com.hanait.noninvasiveglucoseapplication.util.Constants.DEVICE_NAME
import com.hanait.noninvasiveglucoseapplication.util.Constants.SCAN_PERIOD
import com.hanait.noninvasiveglucoseapplication.util.Constants.TX_CHAR_UUID
import com.hanait.noninvasiveglucoseapplication.util.Constants.UART_UUID
import com.hanait.noninvasiveglucoseapplication.util.CustomDialogManager
class UserSetConnectDeviceFragment : BaseFragment<FragmentUserSetConnectDeviceBinding>(FragmentUserSetConnectDeviceBinding::inflate), View.OnClickListener {
    private val customProgressDialog by lazy { CustomDialogManager(R.layout.common_progress_dialog, null) }
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

    //////////////////////////////////////////////////////////////////////////////////////////////
    //기기 연결 확인 변수
    private var findDeviceFlag = false

    //가트 연결 변수
    var bluetoothGatt: BluetoothGatt? = null

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

        binding.homeConnectDeviceLottie.setOnClickListener(this)

        //키보드 넣기
        if (activity != null && requireActivity().currentFocus != null) {
            // 프래그먼트기 때문에 getActivity() 사용
            val inputManager: InputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

        //블루투스 연결 콜백 초기화
        setActivityResultLauncher()
        
        //연결 체크
        checkBluetoothEnable()
    }

    override fun onClick(v: View?) {
        when(v){
            binding.homeConnectDeviceLottie -> {
                //블루투스 스캔 시작
                scanLeDevice(true)
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

    //블루투스 설정  resultLauncher 초기화
    private fun setActivityResultLauncher() {
        //갤러리 콜백
        activityResultLauncherBluetooth =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                //갤러리 호출 반환
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    Toast.makeText(requireContext(), "블루투스 ON", Toast.LENGTH_SHORT).show()
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
            if(deviceName != null && deviceName.equals(DEVICE_NAME)) {
                Log.d("로그", "UserSetConnectDeviceFragment - onScanResult : 장치 발견됨!")
                findDeviceFlag = true
                //스캔 종료
                scanLeDevice(false)
                //디바이스 연결 / 가트에 정보 넣기
                bluetoothGatt = result.device.connectGatt(requireContext(), false, gattCallback)
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
        val bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner
        when(enable) {
            true -> {
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
                customProgressDialog.dismiss()
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
                BluetoothGatt.STATE_CONNECTED -> {
                    //서비스디스커버 호출하기
                    Log.d("로그", "CustomBluetoothManager - onConnectionStateChange : 가트 서버 연결됨 : ${bluetoothGatt!!.discoverServices()}")
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    Log.d("로그", "CustomBluetoothManager - onConnectionStateChange : 가트 서버에서 연결 해제됨")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            when(status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.d("로그", "CustomBluetoothManager - onServicesDiscovered : 연결 성공함")
//                    Log.d("로그", "CustomBluetoothManager - onServicesDiscovered : ${bluetoothGatt.ge}")
                    val str = gatt!!.getService(UART_UUID).getCharacteristic(TX_CHAR_UUID)
                    Log.d("로그", "CustomBluetoothManager - onServicesDiscovered : $str")
                    if (bluetoothGatt!!.setCharacteristicNotification(str, true)) {
                        val descriptor = str.getDescriptor(CCCD_UUID)
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        bluetoothGatt!!.writeDescriptor(descriptor)
                        broadcastUpdate("커넥티드 한아아이티 기계")
                    }
                }
                BluetoothGatt.GATT_FAILURE -> {
                    broadcastUpdate("커넥티드 실패")
                }
            }
        }

        //기기에서 오는 데이터 처리
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
                    Toast.makeText(requireContext(), str, Toast.LENGTH_SHORT).show()
                    val mActivity = activity as UserActivity
                    mActivity.changeFragmentTransaction(ConnectionLoadingFragment())
                }
            }
            mHandler.obtainMessage().sendToTarget()
        }
    }

}