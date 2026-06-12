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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biogeo_check.ui.components.BottomNavBar
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.theme.*
import com.example.biogeo_check.ui.viewmodel.DashboardViewModel

@Composable
fun EmployeeDashboardScreen(
    vm: DashboardViewModel = viewModel(),
    onNavigate: (NavScreen) -> Unit
) {
    val trabajador = vm.trabajadorActual
    val isClockedIn = vm.ultimoFichaje?.tipoAccion == "ENTRADA"


    LaunchedEffect(Unit) {
        vm.cargarDatosIniciales()
    }


    LaunchedEffect(trabajador) {
        if (trabajador != null && trabajador.departamentoId == null) {
            onNavigate(NavScreen.PROFILE)
        }
    }

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
                text = "Bienvenido/a, ${trabajador?.nombre ?: "Usuario"}",
                color = PrimaryTextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            vm.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFFF5555),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }


            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { vm.alternarFichaje() },
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


            DashboardCard(
                title = "Tiempo Trabajado Hoy",
                value = vm.tiempoTrabajadoHoy
            )

            Spacer(modifier = Modifier.height(16.dp))


            DashboardCard(
                title = "Total de esta Semana",
                value = vm.tiempoTrabajadoSemana
            )
        }


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