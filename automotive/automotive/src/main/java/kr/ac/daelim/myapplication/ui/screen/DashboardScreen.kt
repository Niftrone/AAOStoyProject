package kr.ac.daelim.myapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.ac.daelim.myapplication.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val speedText by viewModel.speed.collectAsState()
    val gearText by viewModel.gear.collectAsState()
    // 추가된 State 구독
    val rpmText by viewModel.rpm.collectAsState()
    val fuelText by viewModel.fuel.collectAsState()
    val oilTempText by viewModel.oilTemp.collectAsState()
    val coolantTempText by viewModel.coolantTemp.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                Text(
                    text = speedText,
                    color = Color.Green,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = gearText,
                    color = Color.White,
                    fontSize = 55.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DashItem(label = "RPM", value = rpmText)
                    DashItem(label = "연료 잔량", value = fuelText)
                }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DashItem(label = "오일 온도", value = oilTempText)
                    DashItem(label = "냉각수 온도", value = coolantTempText)
                }
            }
        }
    }
}

// 재사용 가능한 데이터 아이템 Composable
@Composable
fun DashItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}