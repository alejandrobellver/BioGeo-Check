package com.example.biogeo_check.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biogeo_check.ui.viewmodel.AuthViewModel

@Composable
fun AuthMasterScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToDashboard: (isJefe: Boolean) -> Unit = {}
) {
    val state by viewModel.authState.collectAsState()
    var currentTab by remember { mutableStateOf(0) }

    val emerald = Color(0xFF10B981)
    val background = Color(0xFF121212)
    val surface = Color(0xFF708090)

    val mainScrollState = rememberScrollState()
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(mainScrollState)
                .padding(bottom = 48.dp)
        ) {

            // TÍTULO
            Text(
                text = "BioGeo-Check Test",
                color = emerald,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 40.dp, bottom = 24.dp)
            )

            // SELECTOR DE PANTALLAS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        currentTab = 0
                        viewModel.resetState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentTab == 0) emerald else Color.DarkGray)
                ) { Text("Login") }

                Button(
                    onClick = {
                        currentTab = 1
                        viewModel.resetState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentTab == 1) emerald else Color.DarkGray)
                ) { Text("Jefe") }

                Button(
                    onClick = {
                        currentTab = 2
                        viewModel.resetState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (currentTab == 2) emerald else Color.DarkGray)
                ) { Text("Trabajador") }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // RENDERIZADO DE LOS FORMULARIOS
            Card(
                colors = CardDefaults.cardColors(containerColor = surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (currentTab) {
                        // 🚀 PASAMOS LA LAMBDA: Al hacer clic, cambia el estado para abrir el Popup
                        0 -> LoginView(viewModel, emerald, onOlvideClick = { showForgotPasswordDialog = true })
                        1 -> RegistroJefeView(viewModel, emerald)
                        2 -> ActivacionTrabajadorView(viewModel, emerald)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CONTROL DE ESTADOS DE AUTENTICACIÓN
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    CircularProgressIndicator(color = emerald, modifier = Modifier.padding(20.dp))
                }

                is AuthViewModel.AuthState.Error -> {
                    val mensajeError = (state as AuthViewModel.AuthState.Error).mensaje
                    Text(
                        text = "❌ Error: $mensajeError",
                        color = Color(0xFFEF4444),
                        modifier = Modifier.padding(20.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                is AuthViewModel.AuthState.Success -> {
                    val trabajador = (state as AuthViewModel.AuthState.Success).trabajador
                    if (trabajador != null) {
                        LaunchedEffect(Unit) {
                            onNavigateToDashboard(trabajador.rol == "JEFE")
                            viewModel.resetState()
                        }
                    } else {
                        Text(
                            text = "✅ ¡Operación Exitosa! Por favor, inicia sesión.",
                            color = emerald,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }

                is AuthViewModel.AuthState.Idle -> {}
            }
        }
    }

    // =============================================================================================
    // 🚀 POPUP DE RECUPERACIÓN DE CONTRASEÑA EN 2 FASES (OTP NATIVO v2.5.0)
    // =============================================================================================
    if (showForgotPasswordDialog) {
        var emailRecuperacion by remember { mutableStateOf("") }
        var codigoOTP by remember { mutableStateOf("") }
        var pass1 by remember { mutableStateOf("") }
        var pass2 by remember { mutableStateOf("") }

        var codigoEnviado by remember { mutableStateOf(false) }
        var feedbackDialog by remember { mutableStateOf("") }
        var esErrorDialog by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(12.dp),
            title = {
                Text(
                    text = if (!codigoEnviado) "Recuperar Contraseña" else "Introduce el Código",
                    color = emerald,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!codigoEnviado) {
                        Text("Introduce tu correo corporativo y te enviaremos un código de seguridad de 8 dígitos.", color = Color.LightGray, fontSize = 14.sp)
                        OutlinedTextField(
                            value = emailRecuperacion,
                            onValueChange = { emailRecuperacion = it },
                            label = { Text("Email registrado") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = emerald, focusedLabelColor = emerald),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("Escribe el código recibido por correo y tus nuevas claves de acceso corporativas.", color = Color.LightGray, fontSize = 14.sp)

                        OutlinedTextField(
                            value = codigoOTP,
                            onValueChange = { if (it.length <= 8 && it.all { c -> c.isDigit() }) codigoOTP = it },
                            label = { Text("Código de 8 dígitos") },

                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = emerald, focusedLabelColor = emerald),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = pass1,
                            onValueChange = { pass1 = it },
                            label = { Text("Nueva Contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = emerald, focusedLabelColor = emerald),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = pass2,
                            onValueChange = { pass2 = it },
                            label = { Text("Repetir Nueva Contraseña") },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = emerald, focusedLabelColor = emerald),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (feedbackDialog.isNotBlank()) {
                        Text(
                            text = feedbackDialog,
                            color = if (esErrorDialog) Color(0xFFEF4444) else emerald,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!codigoEnviado) {
                            viewModel.enviarCorreoRecuperacion(emailRecuperacion) { exito, mensaje ->
                                if (exito) {
                                    codigoEnviado = true
                                    feedbackDialog = "Código enviado a tu correo."
                                    esErrorDialog = false
                                } else {
                                    feedbackDialog = mensaje
                                    esErrorDialog = true
                                }
                            }
                        } else {
                            viewModel.verificarYRestablecerContrasena(
                                email = emailRecuperacion,
                                codigo = codigoOTP,
                                nuevaPass1 = pass1,
                                nuevaPass2 = pass2
                            ) { exito, mensaje ->
                                feedbackDialog = mensaje
                                esErrorDialog = !exito
                                if (exito) {
                                    showForgotPasswordDialog = false
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = emerald)
                ) {
                    Text(if (!codigoEnviado) "Enviar Código" else "Restablecer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun LoginView(vm: AuthViewModel, color: Color, onOlvideClick: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column {
        Text(
            "Acceso Diario",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        // 🚀 NUEVO: Texto clickable que abre el popup de recuperación
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "¿Has olvidado tu contraseña?",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { onOlvideClick() }
                .padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { vm.login(email, pass) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(color)
        ) {
            Text("Iniciar Sesión")
        }
    }
}

@Composable
fun RegistroJefeView(vm: AuthViewModel, color: Color) {
    var nombreEmpresa by remember { mutableStateOf("") }
    var cif by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var cp by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var nombreJefe by remember { mutableStateOf("") }
    var apellidosJefe by remember { mutableStateOf("") }
    var dniJefe by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Registrar Nueva Empresa",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "Datos de la Empresa",
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nombreEmpresa,
            onValueChange = { nombreEmpresa = it },
            label = { Text("Nombre de la Empresa") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cif,
            onValueChange = { cif = it },
            label = { Text("CIF") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cp,
            onValueChange = { cp = it },
            label = { Text("Código Postal") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = ciudad,
            onValueChange = { ciudad = it },
            label = { Text("Ciudad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Datos del Administrador",
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nombreJefe,
            onValueChange = { nombreJefe = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = apellidosJefe,
            onValueChange = { apellidosJefe = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = dniJefe,
            onValueChange = { dniJefe = it },
            label = { Text("DNI / NIE") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Corporativo") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                vm.registrarJefeYEmpresa(
                    email,
                    pass,
                    nombreEmpresa,
                    cif,
                    direccion,
                    cp = cp.toInt(),
                    ciudad,
                    nombreJefe,
                    apellidosJefe,
                    dniJefe
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(color)
        ) { Text("Crear Empresa y Administrador", fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun ActivacionTrabajadorView(vm: AuthViewModel, color: Color) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }

    Column {
        Text(
            "Activar Cuenta de Trabajador",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            "Usa el email con el que tu jefe te registró.",
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email de la empresa") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Crea tu Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre Trabajador") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos Trabajador") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = dni,
            onValueChange = { dni = it },
            label = { Text("DNI Trabajador") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { vm.activarCuentaTrabajador(email, pass, nombre, apellidos, dni) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(color)
        ) {
            Text("Activar mi cuenta")
        }
    }
}