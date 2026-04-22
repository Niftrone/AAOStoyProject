package kr.ac.daelim.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : ViewModel() {

    // 외부에서 함부로 수정하지 못하게 내부 상태(_speed)와 외부 상태(speed)를 분리 (캡슐화)
    private val _speed = MutableStateFlow("속도 로딩 중...")
    private val _gear = MutableStateFlow("기어 확인 중...")
    val speed: StateFlow<String> = _speed.asStateFlow()
    val gear: StateFlow<String> = _gear.asStateFlow()

    fun updateGear(gearValue: Int){
        _gear.value = when (gearValue){
            1 -> "N"
            2 -> "R"
            3 -> "P"
            8 -> "D"
            else -> "?"
        }
    }

    // VHAL에서 값이 들어오면 UI 상태를 업데이트하는 함수
    fun updateSpeed(newSpeed: Float) {
        val speedKm = newSpeed * 3.6f
        _speed.value = "${speedKm.toInt()} km/h"
    }

    fun setError() {
        _speed.value = "에러 발생"
    }

    fun setPermissionDenied() {
        _speed.value = "권한 없음"
        _gear.value = "-"
    }
}