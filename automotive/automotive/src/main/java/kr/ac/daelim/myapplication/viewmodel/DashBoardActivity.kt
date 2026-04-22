package kr.ac.daelim.myapplication.viewmodel

import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import kr.ac.daelim.myapplication.DashboardViewModel
import kr.ac.daelim.myapplication.ui.screen.DashboardScreen
import android.car.hardware.property.CarPropertyManager.SENSOR_RATE_ONCHANGE

class DashBoardActivity : ComponentActivity() {

    private var car: Car? = null
    private var carPropertyManager: CarPropertyManager? = null

    // ✅ ViewModel 초기화
    private val viewModel: DashboardViewModel by viewModels()

    private val requiredPermissions = arrayOf(
        android.car.Car.PERMISSION_SPEED
    )

    // 🚀 [최신 방식] 권한 요청 런처 선언 (구형 onRequestPermissionsResult 대체)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all {it.value}
        if (allGranted){
            registerCarCallbacks()
        } else {
            viewModel.setPermissionDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Car API 연결
        car = Car.createCar(this)
        carPropertyManager = car?.getCarManager(Car.PROPERTY_SERVICE) as? CarPropertyManager

        // 2. Compose UI 연결
        setContent {
            DashboardScreen(viewModel = viewModel)
        }

        val hasAllPermission = requiredPermissions.all{
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
        // 3. 권한 확인 및 요청
        if (hasAllPermission) {
            // 이미 권한이 있다면 바로 콜백 등록
            registerCarCallbacks()
        } else {
            // 🚀 [최신 방식] 런처를 통해 권한 요청 팝업 띄우기
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    private val carPropertyCallback = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>) {
            when (value.propertyId){
                VehiclePropertyIds.PERF_VEHICLE_SPEED -> {
                    val speed = value.value as Float
                    viewModel.updateSpeed(speed)
                }
                VehiclePropertyIds.GEAR_SELECTION -> {
                    val gear = value.value as Int
                    viewModel.updateGear(gear)
                }
            }
        }
        override fun onErrorEvent(propId: Int, zone: Int) {
            viewModel.setError()
        }
    }


    // 콜백 등록 함수
    private fun registerCarCallbacks() {
        carPropertyManager?.let { manager ->
            manager.registerCallback(
                carPropertyCallback,
                VehiclePropertyIds.PERF_VEHICLE_SPEED,
                CarPropertyManager.SENSOR_RATE_NORMAL
            )
            manager.registerCallback(
                carPropertyCallback,
                VehiclePropertyIds.GEAR_SELECTION,
                SENSOR_RATE_ONCHANGE
            )
        }


    }

    // 앱 종료 시 메모리 누수 방지
    override fun onDestroy() {
        super.onDestroy()
        carPropertyManager?.unregisterCallback(carPropertyCallback)
        car?.disconnect()
    }
}