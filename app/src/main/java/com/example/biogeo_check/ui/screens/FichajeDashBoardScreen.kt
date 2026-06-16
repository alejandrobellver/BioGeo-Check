package com.example.biogeo_check.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biogeo_check.ui.components.BottomNavBar
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.theme.BlackBackground
import com.example.biogeo_check.ui.theme.DarkGrayCard
import com.example.biogeo_check.ui.theme.DarkRed
import com.example.biogeo_check.ui.theme.EmeraldGreen
import com.example.biogeo_check.ui.theme.PrimaryTextWhite
import com.example.biogeo_check.ui.viewmodel.DashboardViewModel
import com.example.biogeo_check.util.BiometricHelper

@Composable
fun FichajeDashboardScreen(
    vm: DashboardViewModel = viewModel(),
    onNavigate: (NavScreen) -> Unit
) {
    val trabajador = vm.trabajadorActual
    val isClockedIn = vm.ultimoFichaje?.tipoAccion == "ENTRADA"
    val activity = LocalActivity.current as? FragmentActivity

    LaunchedEffect(Unit) {
        // 🚀 LA CLAVE: Cargamos los datos del perfil que sabemos que bajan el contrato perfecto
        vm.cargarDatosIniciales()
        vm.cargarDatosPerfil()
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
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))


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
                    onClick = {
                        if (activity != null) {
                            BiometricHelper.authenticate(
                                activity = activity,
                                onSuccess = {
                                    vm.alternarFichaje()
                                },
                                onError = { errorMsg ->
                                    vm.errorMessage = errorMsg
                                }
                            )
                        } else {
                            vm.errorMessage = "Error: La actividad no es compatible con biometría."
                        }
                    },
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

            val minutosDinamicos =
                vm.listaContratos.find { it.contratoId == trabajador?.contratoId }?.descanso
                    ?: vm.listaContratos.firstOrNull()?.descanso
                    ?: 30

            // TARJETA 1
            DashboardCard(
                title = "Hora de Entrada",
                value = vm.horaFichajeTexto
            )

            Spacer(modifier = Modifier.height(16.dp))


            // TARJETA 2
            DashboardCard(
                title = "Hora de Salida",
                value = vm.horaSiguienteEventoTexto
            )

            if (isClockedIn) {

                Spacer(modifier = Modifier.height(16.dp))

                DashboardCard(
                    title = "Tiempo de Descanso",
                    value = "$minutosDinamicos minutos"
                )
            }
        }



        BottomNavBar(
            currentScreen = NavScreen.HOME,
            isJefe = trabajador?.rol == "JEFE",
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