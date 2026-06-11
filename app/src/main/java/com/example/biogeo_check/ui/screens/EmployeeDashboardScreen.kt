package com.example.biogeo_check.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biogeo_check.ui.components.BottomNavBar
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.theme.*

@Composable
fun EmployeeDashboardScreen(
    employeeName: String,
    onNavigate: (NavScreen) -> Unit
) {
    var isClockedIn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Bienvenido/a, $employeeName",
                color = PrimaryTextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Clock In/Out Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { isClockedIn = !isClockedIn },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isClockedIn) DarkRed else EmeraldGreen,
                        contentColor = PrimaryTextWhite
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = if (isClockedIn) "FICHAR SALIDA" else "FICHAR ENTRADA",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))

            // Card 1: Today's Worked Time
            DashboardCard(
                title = "Tiempo Trabajado Hoy",
                value = "06:32:15"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: This Week's Total
            DashboardCard(
                title = "Total de esta Semana",
                value = "32h 15m"
            )
        }

        // Bottom Navigation
        BottomNavBar(
            currentScreen = NavScreen.HOME,
            onNavigate = onNavigate
        )
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkGrayCard)
            .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Text(
            text = title,
            color = PrimaryTextWhite,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = value,
            color = EmeraldGreen,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
    }
}
