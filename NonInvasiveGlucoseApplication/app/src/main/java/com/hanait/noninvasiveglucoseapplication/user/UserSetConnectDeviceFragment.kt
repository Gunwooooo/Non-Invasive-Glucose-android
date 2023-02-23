package com.hanait.noninvasiveglucoseapplication.user

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.getSystemService
import com.hanait.noninvasiveglucoseapplication.databinding.FragmentUserSetConnectDeviceBinding
import com.hanait.noninvasiveglucoseapplication.home.HomeCropImageActivity
import com.hanait.noninvasiveglucoseapplication.util.BaseFragment
import com.hanait.noninvasiveglucoseapplication.util.CustomBluetoothManager


class UserSetConnectDeviceFragment : BaseFragment<FragmentUserSetConnectDeviceBinding>(FragmentUserSetConnectDeviceBinding::inflate), View.OnClickListener {

    //블루투스 매니저로 어댑터 선언
    //비동기화 환경에서 선언
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    //블루투스 활성화 확인
    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    //블루투스 권한 요청 resultLauncher
    private lateinit var activityResultLauncherBluetooth: ActivityResultLauncher<Intent>

    private val customBluetoothManager by lazy { CustomBluetoothManager(requireContext(), bluetoothAdapter) }

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

        //블루투스 스캔 시작
        customBluetoothManager.scanLeDevice(true)
    }

    override fun onClick(v: View?) {
        when(v){
            binding.homeConnectDeviceLottie -> {
                val mActivity = activity as UserActivity
                mActivity.changeFragmentTransaction(ConnectionLoadingFragment())
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
}