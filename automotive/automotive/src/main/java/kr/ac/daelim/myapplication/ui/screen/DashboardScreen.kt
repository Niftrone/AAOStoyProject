package kr.ac.daelim.myapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
    // ViewModel의 StateFlow를 관찰하여 상태가 변할 때마다 UI 재렌더링
    val speedText by viewModel.speed.collectAsState()
    val gearText by viewModel.gear.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Text(
                text = speedText,
                color = Color.Green,
                fontSize =  72.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = gearText,
                color = Color.White,
                fontSize = 55.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}