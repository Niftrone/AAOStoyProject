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

    // ViewModel 초기화
    private val viewModel: DashboardViewModel by viewModels()

    private val requiredPermissions = arrayOf(
        android.car.Car.PERMISSION_SPEED,
        android.car.Car.PERMISSION_POWERTRAIN,       // RPM
        android.car.Car.PERMISSION_ENERGY,           // 연료
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
            //[최신 방식] 런처를 통해 권한 요청 팝업 띄우기
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    private val carPropertyCallback = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>) {
            val floatVal = value.value as? Float
            val intVal = value.value as? Int

            when (value.propertyId){
                VehiclePropertyIds.PERF_VEHICLE_SPEED ->
                    floatVal?.let { viewModel.updateSpeed(it) }
                VehiclePropertyIds.GEAR_SELECTION ->
                    intVal?.let { viewModel.updateGear(it) }
                VehiclePropertyIds.ENGINE_RPM ->
                    floatVal?.let { viewModel.updateRpm(it) }

            }
        }
        override fun onErrorEvent(propId: Int, zone: Int) {
            viewModel.setError()
        }
    }


    // 콜백 등록 함수
    private fun registerCarCallbacks() {
        carPropertyManager?.let { manager ->

            // 지원하는 Property 목록 가져오기
            val supportedProps = manager.propertyList.map { it.propertyId }

            // 지원 여부 확인 후 등록
            fun safeRegister(propertyId: Int, rate: Float) {
                if (supportedProps.contains(propertyId)) {
                    manager.registerCallback(carPropertyCallback, propertyId, rate)
                }
            }

            safeRegister(VehiclePropertyIds.PERF_VEHICLE_SPEED, CarPropertyManager.SENSOR_RATE_NORMAL)
            safeRegister(VehiclePropertyIds.GEAR_SELECTION, SENSOR_RATE_ONCHANGE)
            safeRegister(VehiclePropertyIds.FUEL_LEVEL, SENSOR_RATE_ONCHANGE)
            safeRegister(VehiclePropertyIds.ENGINE_RPM, CarPropertyManager.SENSOR_RATE_NORMAL)
            safeRegister(VehiclePropertyIds.ENGINE_OIL_TEMP, SENSOR_RATE_ONCHANGE)
            safeRegister(VehiclePropertyIds.ENGINE_COOLANT_TEMP, SENSOR_RATE_ONCHANGE)
        }
    }

    // 앱 종료 시 메모리 누수 방지
    override fun onDestroy() {
        super.onDestroy()
        carPropertyManager?.unregisterCallback(carPropertyCallback)
        car?.disconnect()
    }
}