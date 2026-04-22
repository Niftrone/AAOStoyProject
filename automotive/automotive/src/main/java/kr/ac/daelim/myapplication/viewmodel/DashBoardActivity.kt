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

class DashBoardActivity : ComponentActivity() {

    private var car: Car? = null
    private var carPropertyManager: CarPropertyManager? = null

    // ✅ ViewModel 초기화
    private val viewModel: DashboardViewModel by viewModels()

    // 🚀 [최신 방식] 권한 요청 런처 선언 (구형 onRequestPermissionsResult 대체)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 사용자가 권한을 허용하면 VHAL 데이터 구독 시작
            registerSpeedCallback()
        } else {
            // 권한을 거부하면 ViewModel에 거부 상태 전달
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

        // 3. 권한 확인 및 요청
        if (checkSelfPermission(android.car.Car.PERMISSION_SPEED) == PackageManager.PERMISSION_GRANTED) {
            // 이미 권한이 있다면 바로 콜백 등록
            registerSpeedCallback()
        } else {
            // 🚀 [최신 방식] 런처를 통해 권한 요청 팝업 띄우기
            requestPermissionLauncher.launch(android.car.Car.PERMISSION_SPEED)
        }
    }

    // VHAL 데이터 변경 감지 콜백
    private val speedCallback = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>) {
            val speed = value.value as Float
            viewModel.updateSpeed(speed)
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            viewModel.setError()
        }
    }

    // 콜백 등록 함수
    private fun registerSpeedCallback() {
        carPropertyManager?.registerCallback(
            speedCallback,
            VehiclePropertyIds.PERF_VEHICLE_SPEED,
            CarPropertyManager.SENSOR_RATE_NORMAL
        )
    }

    // 앱 종료 시 메모리 누수 방지
    override fun onDestroy() {
        super.onDestroy()
        carPropertyManager?.unregisterCallback(speedCallback)
        car?.disconnect()
    }
}