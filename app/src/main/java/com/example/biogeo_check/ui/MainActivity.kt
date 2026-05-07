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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biogeo_check.data.viewmodel.EmpresaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // MaterialTheme da los colores y tipografías por defecto de Android
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Aquí iniciamos el "Enrutador" de nuestra app
                    AppNavigation()
                }
            }
        }
    }
}

// =====================================================================
// 1. EL ENRUTADOR (Controla qué pantalla se ve en cada momento)
// =====================================================================
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // NavHost es el contenedor. Le decimos que empiece en la ruta "inicio"
    NavHost(navController = navController, startDestination = "inicio") {

        // Pantalla 1: El Menú Principal
        composable("inicio") {
            PantallaInicio(navController)
        }

        // Pantalla 2: Añadir Empresa (Pasamos el ViewModel aquí)
        composable("crear_empresa") {
            val empresaViewModel: EmpresaViewModel = viewModel()
            PantallaCrearEmpresa(navController, empresaViewModel)
        }

        // Pantalla 3: Iniciar Sesión
        composable("login") {
            PantallaLogin(navController)
        }
    }
}

// =====================================================================
// 2. PANTALLA PRINCIPAL (Los dos botones)
// =====================================================================
@Composable
fun PantallaInicio(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido a BioGeo Check", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("crear_empresa") },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Registrar Nueva Empresa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Iniciar Sesión / Trabajadores")
        }
    }
}

// =====================================================================
// 3. PANTALLA DE CREAR EMPRESA (Conectada a tu ViewModel)
// =====================================================================
@Composable
fun PantallaCrearEmpresa(navController: NavController, viewModel: EmpresaViewModel) {
    // Variables para guardar lo que el usuario escribe en las cajas de texto
    var nombreEmpresa by remember { mutableStateOf("") }
    var cifEmpresa by remember { mutableStateOf("") }

    // Observamos si está cargando para mostrar un texto o ruedita
    val estaCargando by viewModel.cargando.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Alta de Empresa", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // Caja de texto para el Nombre
        OutlinedTextField(
            value = nombreEmpresa,
            onValueChange = { nombreEmpresa = it },
            label = { Text("Nombre de la empresa") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Caja de texto para el CIF
        OutlinedTextField(
            value = cifEmpresa,
            onValueChange = { cifEmpresa = it },
            label = { Text("CIF") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para guardar en Supabase
        Button(
            onClick = {
                if (nombreEmpresa.isNotEmpty()) {
                    viewModel.crearNuevaEmpresa(nombreEmpresa, cifEmpresa)
                    nombreEmpresa = "" // Limpiamos la caja tras guardar
                    cifEmpresa = ""
                }
            },
            enabled = !estaCargando // Desactiva el botón si está cargando
        ) {
            if (estaCargando) {
                Text("Guardando...")
            } else {
                Text("Guardar Empresa")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para volver atrás
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver al Inicio")
        }
    }
}

// =====================================================================
// 4. PANTALLA DE LOGIN (Plantilla Inicial)
// =====================================================================
@Composable
fun PantallaLogin(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Acceso Empleados", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Login Normal
        Button(
            onClick = { /* TODO: Llamaremos a Auth de Supabase aquí */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Primera Vez (Registro)
        OutlinedButton(
            onClick = { /* TODO: Llamaremos a Registro de Supabase aquí */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Primera vez aquí? Activar cuenta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver")
        }
    }
}