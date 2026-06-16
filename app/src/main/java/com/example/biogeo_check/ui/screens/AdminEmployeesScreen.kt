package com.example.biogeo_check.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
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
    val deptoName: String,
    val status: String,
    val todayHours: String,
    val lastEventTime: String
)

@Composable
fun AdminDashboardScreen(
    vm: DashboardViewModel = viewModel(),
    onNavigate: (NavScreen) -> Unit
) {
    LaunchedEffect(Unit) {
        vm.cargarTrabajadoresDeLaEmpresa()
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
                MetricCard(title = "Horas\nTotales", value = vm.totalHorasEquipo, modifier = Modifier.weight(1f))
                MetricCard(title = "Activos\nHoy", value = vm.activosHoy, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado de Empleados",
                    color = PrimaryTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif
                )

                Button(
                    onClick = { 
                        vm.inviteError = null
                        vm.showInviteDialog = true 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir", tint = PrimaryTextWhite)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invitar", color = PrimaryTextWhite, fontWeight = FontWeight.Bold)
                }
            }

            // Employee List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                itemsIndexed(vm.teamStats) { index, stat ->
                    val bgColor = if (index % 2 == 0) BlackBackground else AlternatingRowDark
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stat.name,
                                    color = PrimaryTextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = stat.deptoName,
                                    color = EmeraldGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(Color(0xFF00C853).copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val statusColor = if (stat.status == "Fichado") EmeraldGreen else SecondaryTextGray
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(statusColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${stat.status} a las ${stat.lastEventTime} · Hoy: ${stat.todayHours}h",
                                    color = SecondaryTextGray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        BottomNavBar(
            currentScreen = NavScreen.EMPLOYEES,
            isJefe = vm.trabajadorActual?.rol == "JEFE",
            onNavigate = onNavigate
        )

        // Success Dialog
        vm.inviteSuccessMessage?.let { msg ->
            AlertDialog(
                onDismissRequest = { vm.inviteSuccessMessage = null },
                containerColor = DarkGrayCard,
                title = { Text("Éxito", color = EmeraldGreen) },
                text = { Text(msg, color = PrimaryTextWhite) },
                confirmButton = {
                    Button(
                        onClick = { vm.inviteSuccessMessage = null },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text("Aceptar", color = PrimaryTextWhite)
                    }
                }
            )
        }

        // Invite Dialog
        if (vm.showInviteDialog) {
            AlertDialog(
                onDismissRequest = { 
                    vm.inviteError = null
                    vm.showInviteDialog = false 
                },
                containerColor = DarkGrayCard,
                title = { Text("Invitar Empleado", color = PrimaryTextWhite) },
                text = {
                    Column {
                        vm.inviteError?.let {
                            Text(text = it, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                        }
                        TextField(
                            value = vm.inviteEmail,
                            onValueChange = { vm.inviteEmail = it },
                            placeholder = { Text("Email del empleado") },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = PrimaryTextWhite,
                                unfocusedTextColor = PrimaryTextWhite,
                                focusedContainerColor = BlackBackground,
                                unfocusedContainerColor = BlackBackground
                            ),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                        
                        var deptoExpanded by remember { mutableStateOf(false) }
                        val deptoName = vm.listaDepartamentos.find { it.departamentoId == vm.inviteDeptoId }?.nombreDepartamento ?: "Seleccionar Departamento"
                        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            OutlinedButton(onClick = { deptoExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                                Text(deptoName, color = PrimaryTextWhite)
                            }
                            DropdownMenu(expanded = deptoExpanded, onDismissRequest = { deptoExpanded = false }) {
                                vm.listaDepartamentos.forEach { depto ->
                                    DropdownMenuItem(
                                        text = { Text(depto.nombreDepartamento) },
                                        onClick = {
                                            vm.inviteDeptoId = depto.departamentoId
                                            deptoExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        var contratoExpanded by remember { mutableStateOf(false) }
                        val contratoName = vm.listaContratos.find { it.contratoId == vm.inviteContratoId }?.nombreContrato ?: "Seleccionar Contrato"
                        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            OutlinedButton(onClick = { contratoExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                                Text(contratoName, color = PrimaryTextWhite)
                            }
                            DropdownMenu(expanded = contratoExpanded, onDismissRequest = { contratoExpanded = false }) {
                                vm.listaContratos.forEach { contrato ->
                                    DropdownMenuItem(
                                        text = { Text("${contrato.nombreContrato} (${contrato.horasSemanales}h)") },
                                        onClick = {
                                            vm.inviteContratoId = contrato.contratoId
                                            contratoExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { vm.invitarEmpleado() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text("Enviar Invitación", color = PrimaryTextWhite)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { 
                        vm.inviteError = null
                        vm.showInviteDialog = false 
                    }) {
                        Text("Cancelar", color = PrimaryTextWhite)
                    }
                }
            )
        }
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
