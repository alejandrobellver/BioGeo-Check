package com.example.biogeo_check.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
// Importamos tus nuevas clases
import com.example.biogeo_check.data.model.AuthState
import com.example.biogeo_check.data.repository.AuthRepository
import com.example.biogeo_check.data.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializamos el Repositorio y el ViewModel
        val authRepository = AuthRepository()

        // AQUÍ pones el UUID que insertaste manualmente en tu base de datos para la empresa
        val uuidEmpresaManual = "d437ee78-ae12-420a-bce8-bcf070fa031e"

        val viewModel = AuthViewModel(authRepository, uuidEmpresaManual)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaLoginManual(viewModel)
                }
            }
        }
    }
}

@Composable
fun PantallaLoginManual(viewModel: AuthViewModel) {
    // Observamos el estado único de autenticación 🔭
    val estado by viewModel.estado.collectAsState()

    // Estado local para el texto del campo
    var emailInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login Manual", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para el correo 📧
        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Correo del empleado") },
            modifier = Modifier.fillMaxWidth(),
            enabled = estado !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de envío
        Button(
            onClick = { viewModel.intentarLogin(emailInput) },
            modifier = Modifier.fillMaxWidth(),
            enabled = emailInput.isNotEmpty() && estado !is AuthState.Loading
        ) {
            Text("Comprobar Acceso")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🧠 Lógica de UI basada en el ESTADO
        when (val s = estado) {
            is AuthState.Loading -> CircularProgressIndicator()

            is AuthState.Error -> {
                Text("❌ ${s.mensaje}", color = MaterialTheme.colorScheme.error)
            }

            is AuthState.Authenticated -> {
                Text("✅ ¡Bienvenido, ${s.trabajador.nombreCompleto}!",
                    color = MaterialTheme.colorScheme.primary)
                Text("Rol: ${s.trabajador.rolTrabajador}")
                // Aquí podrías llamar a una función para navegar a la pantalla de Fichaje
            }

            else -> { /* No mostramos nada en Idle */ }
        }
    }
}