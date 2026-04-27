package kr.ac.daelim.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : ViewModel() {

    private val _speed = MutableStateFlow("속도 로딩 중...")
    private val _gear = MutableStateFlow("기어 확인 중...")
    // 추가된 State
    private val _rpm = MutableStateFlow("미지원")
    private val _fuel = MutableStateFlow("-- %")
    private val _oilTemp = MutableStateFlow("미지원")
    private val _coolantTemp = MutableStateFlow("미지원")

    val speed: StateFlow<String> = _speed.asStateFlow()
    val gear: StateFlow<String> = _gear.asStateFlow()
    val fuel: StateFlow<String> = _fuel.asStateFlow()
    val rpm: StateFlow<String> = _rpm.asStateFlow()
    val oilTemp: StateFlow<String> = _oilTemp.asStateFlow()
    val coolantTemp: StateFlow<String> = _coolantTemp.asStateFlow()

    fun updateSpeed(newSpeed: Float) {
        val speedKm = newSpeed * 3.6f
        _speed.value = "${speedKm.toInt()} km/h"
    }

    fun updateGear(gearValue: Int) {
        _gear.value = when (gearValue) {
            1 -> "N"
            2 -> "R"
            4 -> "P"
            8 -> "D"
            else -> "?"
        }
    }

    // 추가된 업데이트 함수들
    fun updateRpm(newRpm: Float) {
        _rpm.value = "${newRpm.toInt()} RPM"
    }

    fun updateFuel(newFuel: Float) {
        _fuel.value = "${newFuel.toInt()} %"
    }

    fun updateOilTemp(newTemp: Float) {
        _oilTemp.value = "${newTemp.toInt()} °C"
    }

    fun updateCoolantTemp(newTemp: Float) {
        _coolantTemp.value = "${newTemp.toInt()} °C"
    }

    fun setError() {
        _speed.value = "에러 발생"
    }

    fun setPermissionDenied() {
        _speed.value = "권한 없음"
        _gear.value = "-"
        _rpm.value = "-"
        _fuel.value = "-"
        _oilTemp.value = "-"
        _coolantTemp.value = "-"
    }
}