package com.example.biogeo_check.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biogeo_check.ui.viewmodel.AuthState
import com.example.biogeo_check.ui.viewmodel.AuthViewModel

@Composable
fun AuthMasterScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToDashboard: (isJefe: Boolean) -> Unit = {}
) {
    // 1. Observamos el estado del semáforo desde el ViewModel
    val state by viewModel.authState.collectAsState()

    // Estado local para navegar entre las 3 pantallas de prueba
    var currentTab by remember { mutableStateOf(0) } // 0: Login, 1: Registro Jefe, 2: Activación

    // Colores Emerald & Dark
    val emerald = Color(0xFF10B981)
    val background = Color(0xFF121212)
    val surface = Color(0xFF708090)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
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
                        viewModel.resetState() // Limpiamos errores al cambiar de pestaña
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if(currentTab == 0) emerald else Color.DarkGray)
                ) { Text("Login") }

                Button(
                    onClick = {
                        currentTab = 1
                        viewModel.resetState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if(currentTab == 1) emerald else Color.DarkGray)
                ) { Text("Jefe") }

                Button(
                    onClick = {
                        currentTab = 2
                        viewModel.resetState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if(currentTab == 2) emerald else Color.DarkGray)
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
                        0 -> LoginView(viewModel, emerald)
                        1 -> RegistroJefeView(viewModel, emerald)
                        2 -> ActivacionTrabajadorView(viewModel, emerald)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // GESTIÓN VISUAL DEL ESTADO (UI REACCIONANDO AL VIEWMODEL)
            when (state) {
                is AuthState.Loading -> {
                    CircularProgressIndicator(color = emerald, modifier = Modifier.padding(20.dp))
                }
                is AuthState.Error -> {
                    val mensajeError = (state as AuthState.Error).mensaje
                    Text(text = "❌ Error: $mensajeError", color = Color(0xFFEF4444), modifier = Modifier.padding(20.dp))
                }
                is AuthState.Success -> {
                    val trabajador = (state as AuthState.Success).trabajador
                    if (trabajador != null) {
                        LaunchedEffect(Unit) {
                            onNavigateToDashboard(trabajador.rol == "JEFE")
                            viewModel.resetState()
                        }
                    } else {
                        Text(text = "✅ ¡Operación Exitosa! Por favor, inicia sesión.", color = emerald, fontWeight = FontWeight.Bold, modifier = Modifier.padding(20.dp))
                    }
                }
                is AuthState.Idle -> {
                    // No mostramos nada, esperando a que el usuario haga algo
                }
            }
        }
    }
}

// --- SUB-VISTAS DE LOS FORMULARIOS ---

@Composable
fun LoginView(vm: AuthViewModel, color: Color) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column {
        Text("Acceso Diario", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

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
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var nombreEmpresa by remember { mutableStateOf("") }
    var cif by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    Column {
        Text("Registrar Nueva Empresa", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(value = nombreEmpresa, onValueChange = { nombreEmpresa = it }, label = { Text("Nombre de la Empresa") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = cif, onValueChange = { cif = it }, label = { Text("CIF") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email del Jefe") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), singleLine = true)

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { vm.registrarJefeYEmpresa(email, pass, nombreEmpresa, cif, direccion) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(color)
        ) {
            Text("Crear Empresa y Administrador")
        }
    }
}

@Composable
fun ActivacionTrabajadorView(vm: AuthViewModel, color: Color) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column {
        Text("Activar Cuenta de Trabajador", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        Text("Usa el email con el que tu jefe te registró.", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email de la empresa") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Crea tu Contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), singleLine = true)

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { vm.activarCuentaTrabajador(email, pass) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(color)
        ) {
            Text("Activar mi cuenta")
        }
    }
}