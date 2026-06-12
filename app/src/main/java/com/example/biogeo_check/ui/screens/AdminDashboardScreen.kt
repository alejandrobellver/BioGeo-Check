package com.example.biogeo_check.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biogeo_check.ui.viewmodel.DashboardViewModel
import com.example.biogeo_check.ui.components.BottomNavBar
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.theme.*

data class EmployeeStat(
    val name: String,
    val status: String,
    val todayHours: String
)

@Composable
fun AdminDashboardScreen(
    vm: DashboardViewModel = viewModel(),
    onNavigate: (NavScreen) -> Unit
) {
    val sampleEmployees = listOf(
        EmployeeStat("Maria Garcia", "Fichado", "04:12"),
        EmployeeStat("John Doe", "Ausente", "08:00"),
        EmployeeStat("Alice Smith", "Fichado", "02:45"),
        EmployeeStat("Bob Johnson", "Ausente", "07:30")
    )

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
                text = "Resumen del Equipo",
                color = EmeraldGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Top Row Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(title = "Horas\nTotales", value = "142h", modifier = Modifier.weight(1f))
                MetricCard(title = "Activos\nHoy", value = "12/15", modifier = Modifier.weight(1f))
                MetricCard(title = "Aprobaciones\nPendientes", value = "3", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Estado de Empleados",
                color = PrimaryTextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Employee List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                itemsIndexed(sampleEmployees) { index, employee ->
                    val bgColor = if (index % 2 == 0) BlackBackground else AlternatingRowDark
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = employee.name,
                                color = PrimaryTextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val statusColor = if (employee.status == "Fichado") EmeraldGreen else SecondaryTextGray
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(statusColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${employee.status} • ${employee.todayHours} hoy",
                                    color = SecondaryTextGray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        
                        OutlinedButton(
                            onClick = { /* View Details */ },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = EmeraldGreen,
                                containerColor = Color.Transparent
                            ),
                            border = BorderStroke(1.dp, EmeraldGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Ver", fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        BottomNavBar(
            currentScreen = NavScreen.HOME,
            onNavigate = onNavigate
        )
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkGrayCard)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = EmeraldGreen,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            color = SecondaryTextGray,
            fontSize = 12.sp,
            fontFamily = FontFamily.SansSerif,
            lineHeight = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
