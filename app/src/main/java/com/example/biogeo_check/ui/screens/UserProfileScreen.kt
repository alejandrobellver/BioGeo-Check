package com.example.biogeo_check.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biogeo_check.ui.components.BottomNavBar
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.theme.BlackBackground
import com.example.biogeo_check.ui.theme.DarkGrayCard
import com.example.biogeo_check.ui.theme.EmeraldGreen
import com.example.biogeo_check.ui.theme.PrimaryTextWhite
import com.example.biogeo_check.ui.theme.SecondaryTextGray
import com.example.biogeo_check.ui.viewmodel.AuthViewModel
import com.example.biogeo_check.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    vm: DashboardViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onNavigate: (NavScreen) -> Unit,
    onLogout: () -> Unit
) {

    LaunchedEffect(Unit) {
        vm.cargarDatosPerfil()
    }

    val trabajador = vm.trabajadorActual
    val depto = vm.departamento
    val contrato = vm.tipoContrato
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBackground)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF333333)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar Placeholder",
                    tint = PrimaryTextWhite,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${trabajador?.nombre ?: "Cargando..."} ${trabajador?.apellidos ?: ""}",
                color = PrimaryTextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )

            Text(
                text = depto?.nombreDepartamento ?: "Sin departamento asignado",
                color = SecondaryTextGray,
                fontSize = 16.sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rol de acceso: ${trabajador?.rol ?: "DESCONOCIDO"}",
                color = Color(0xFF666666),
                fontSize = 12.sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileCard(
                title = "Información Personal",
                actionIcon = true,
                onActionClick = { vm.editMode = !vm.editMode }
            ) {
                if (vm.editMode) {
                    Column {
                        Text(
                            text = "Correo electrónico",
                            color = SecondaryTextGray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = vm.emailInput,
                            onValueChange = { vm.emailInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF222222),
                                unfocusedContainerColor = Color(0xFF222222),
                                focusedTextColor = PrimaryTextWhite,
                                unfocusedTextColor = PrimaryTextWhite,
                                focusedIndicatorColor = EmeraldGreen
                            )
                        )
                    }
                } else {
                    ProfileInfoRow(label = "Correo electrónico", value = trabajador?.email ?: "-")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (vm.editMode && vm.trabajadorActual?.rol == "JEFE") {
                    var expanded by remember { mutableStateOf(false) }
                    val deptoSeleccionadoTexto =
                        vm.listaDepartamentos.find { it.departamentoId == vm.deptoSeleccionadoId }?.nombreDepartamento
                            ?: "Seleccionar departamento..."

                    Column {
                        Text(text = "Departamento", color = SecondaryTextGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            Text(
                                text = deptoSeleccionadoTexto,
                                color = EmeraldGreen,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable { expanded = true }
                                    .padding(vertical = 4.dp)
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .background(DarkGrayCard)
                                    .border(1.dp, Color(0xFF2A2A2A))
                            ) {
                                vm.listaDepartamentos.forEach { d ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                d.nombreDepartamento,
                                                color = PrimaryTextWhite
                                            )
                                        },
                                        onClick = {
                                            vm.deptoSeleccionadoId = d.departamentoId
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    ProfileInfoRow(
                        label = "Departamento al que pertenece",
                        value = depto?.nombreDepartamento ?: "Ninguno"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileCard(
                title = "Estadísticas de Trabajo",
                actionIcon = false,
                onActionClick = { vm.editMode = !vm.editMode }
            ) {
                if (vm.editMode && vm.trabajadorActual?.rol == "JEFE") {
                    var expandedContrato by remember { mutableStateOf(false) }
                    val contratoSeleccionadoTexto =
                        vm.listaContratos.find { it.contratoId == vm.contratoSeleccionadoId }?.nombreContrato
                            ?: "Seleccionar contrato..."

                    Column {
                        Text(text = "Tipo de Contrato", color = SecondaryTextGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            Text(
                                text = contratoSeleccionadoTexto,
                                color = EmeraldGreen,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable { expandedContrato = true }
                                    .padding(vertical = 4.dp)
                            )
                            DropdownMenu(
                                expanded = expandedContrato,
                                onDismissRequest = { expandedContrato = false },
                                modifier = Modifier
                                    .background(DarkGrayCard)
                                    .border(1.dp, Color(0xFF2A2A2A))
                            ) {
                                vm.listaContratos.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c.nombreContrato, color = PrimaryTextWhite) },
                                        onClick = {
                                            vm.contratoSeleccionadoId = c.contratoId
                                            expandedContrato = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Horas Semanales", color = SecondaryTextGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        val horasDinamicas =
                            vm.listaContratos.find { it.contratoId == vm.contratoSeleccionadoId }?.horasSemanales?.let { "$it horas" }
                                ?: "0h"
                        Text(
                            text = horasDinamicas,
                            color = PrimaryTextWhite,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Tipo de Contrato",
                                color = SecondaryTextGray,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = contrato?.nombreContrato ?: "No definido",
                                color = EmeraldGreen,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Horas Semanales",
                                color = SecondaryTextGray,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = contrato?.horasSemanales?.let { "$it horas" } ?: "0h",
                                color = EmeraldGreen,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Cambiar Contraseña",
                color = EmeraldGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { showChangePasswordDialog = true }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (vm.editMode) {
                Button(
                    onClick = { vm.guardarCambiosPerfil() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Guardar Cambios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PrimaryTextWhite,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, EmeraldGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cerrar Sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        BottomNavBar(
            currentScreen = NavScreen.PROFILE,
            isJefe = trabajador?.rol == "JEFE",
            onNavigate = onNavigate
        )
    }

    if (showChangePasswordDialog) {
        var passVieja by remember { mutableStateOf("") }
        var passNueva1 by remember { mutableStateOf("") }
        var passNueva2 by remember { mutableStateOf("") }
        var mensajeFeedback by remember { mutableStateOf("") }
        var esErrorFeedback by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            containerColor = DarkGrayCard,
            title = {
                Text(
                    "Seguridad: Cambiar Contraseña",
                    color = EmeraldGreen,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Confirma tus credenciales actuales antes de establecer la nueva contraseña.",
                        color = SecondaryTextGray,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = passVieja,
                        onValueChange = { passVieja = it },
                        label = { Text("Contraseña Actual", color = SecondaryTextGray) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PrimaryTextWhite,
                            unfocusedTextColor = PrimaryTextWhite,
                            focusedBorderColor = EmeraldGreen
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(
                        color = Color(0xFF2A2A2A),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    OutlinedTextField(
                        value = passNueva1,
                        onValueChange = { passNueva1 = it },
                        label = { Text("Nueva Contraseña", color = SecondaryTextGray) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PrimaryTextWhite,
                            unfocusedTextColor = PrimaryTextWhite,
                            focusedBorderColor = EmeraldGreen
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = passNueva2,
                        onValueChange = { passNueva2 = it },
                        label = { Text("Repetir Nueva Contraseña", color = SecondaryTextGray) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PrimaryTextWhite,
                            unfocusedTextColor = PrimaryTextWhite,
                            focusedBorderColor = EmeraldGreen
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (mensajeFeedback.isNotBlank()) {
                        Text(
                            text = mensajeFeedback,
                            color = if (esErrorFeedback) Color(0xFFEF4444) else EmeraldGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val emailUsuario = trabajador?.email ?: ""

                        if (emailUsuario.isBlank()) {
                            mensajeFeedback = "Error: Sesión de usuario no válida."
                            esErrorFeedback = true
                            return@Button
                        }

                        authViewModel.cambiarContrasenaSegura(
                            emailActual = emailUsuario,
                            contrasenaVieja = passVieja.trim(),
                            contrasenaNueva1 = passNueva1.trim(),
                            contrasenaNueva2 = passNueva2.trim()
                        ) { exito, mensaje ->
                            mensajeFeedback = mensaje
                            esErrorFeedback = !exito
                            if (exito) {
                                passVieja = ""
                                passNueva1 = ""
                                passNueva2 = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        contentColor = PrimaryTextWhite
                    )
                ) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePasswordDialog = false }) {
                    Text("Cancelar", color = SecondaryTextGray)
                }
            }
        )
    }
}

@Composable
fun ProfileCard(
    title: String,
    actionIcon: Boolean,
    onActionClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkGrayCard)
            .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = PrimaryTextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif
            )
            if (actionIcon) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = EmeraldGreen,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onActionClick() }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(text = label, color = SecondaryTextGray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = PrimaryTextWhite, fontSize = 16.sp)
    }
}