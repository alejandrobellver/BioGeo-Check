package com.example.biogeo_check.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biogeo_check.data.network.SupabaseClient
import com.example.biogeo_check.data.repository.AuthRepository
import com.example.biogeo_check.data.repository.FichajeRepository // 🚀 IMPORTANTE: Asegúrate de tener este import
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.screens.AdminDashboardScreen
import com.example.biogeo_check.ui.screens.AdminDepartmentsScreen
import com.example.biogeo_check.ui.screens.AuthMasterScreen
import com.example.biogeo_check.ui.screens.EmployeeDashboardScreen
import com.example.biogeo_check.ui.screens.LegalScreen
import com.example.biogeo_check.ui.screens.UserProfileScreen
import com.example.biogeo_check.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos los dos repositorios compartidos en la raíz
        val authRepository = AuthRepository(SupabaseClient.client)
        val fichajeRepository = FichajeRepository(SupabaseClient.client) // 🚀 Creado aquí

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Fabricamos el AuthViewModel (Tu código)
                    val authViewModelFactory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AuthViewModel(authRepository) as T
                        }
                    }
                    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

                    // 🚀 2. Fabricamos el DashboardViewModel unificado al mismo nivel
                    val `dashboardViewModel.ktFactory` = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return `DashboardViewModel.kt`(fichajeRepository) as T
                        }
                    }
                    val `dashboardViewModel.kt`: `DashboardViewModel.kt` = viewModel(factory = `dashboardViewModel.ktFactory`)

                    // 🚀 3. Le pasamos AMBOS ViewModels a la navegación
                    AppNavigation(authViewModel = authViewModel, `dashboardViewModel.kt` = `dashboardViewModel.kt`)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    `dashboardViewModel.kt`: `DashboardViewModel.kt` // 🚀 4. Al añadirlo aquí como parámetro, el error desaparece por completo
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "legal") {
        composable("legal") {
            LegalScreen(
                onAccept = {
                    navController.navigate("auth") {
                        popUpTo("legal") { inclusive = true }
                    }
                },
                onDecline = {}
            )
        }

        composable("auth") {
            AuthMasterScreen(
                viewModel = authViewModel,
                onNavigateToDashboard = { isJefe ->
                    val destination = if (isJefe) "admin_dashboard" else "employee_dashboard"
                    navController.navigate(destination) {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("employee_dashboard") {
            EmployeeDashboardScreen(
                vm = `dashboardViewModel.kt`, // 🚀 Ya no sale en rojo porque ya sabe qué es
                onNavigate = { screen ->
                    when (screen) {
                        NavScreen.HOME -> navController.navigate("employee_dashboard")
                        NavScreen.HISTORY -> { /* Empleados no tienen departamentos */ }
                        NavScreen.PROFILE -> navController.navigate("user_profile")
                    }
                }
            )
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(
                onNavigate = { screen ->
                    when (screen) {
                        NavScreen.HOME -> navController.navigate("admin_dashboard")
                        NavScreen.HISTORY -> navController.navigate("admin_departments")
                        NavScreen.PROFILE -> navController.navigate("user_profile")
                    }
                }
            )
        }

        composable("admin_departments") {
            AdminDepartmentsScreen(
                onNavigate = { screen ->
                    when (screen) {
                        NavScreen.HOME -> navController.navigate("admin_dashboard")
                        NavScreen.HISTORY -> navController.navigate("admin_departments")
                        NavScreen.PROFILE -> navController.navigate("user_profile")
                    }
                }
            )
        }

        composable("user_profile") {
            UserProfileScreen(
                vm = `dashboardViewModel.kt`,
                onNavigate = { screen ->
                    when (screen) {
                        NavScreen.HOME -> navController.popBackStack()
                        NavScreen.HISTORY -> navController.navigate("admin_departments")
                        NavScreen.PROFILE -> navController.navigate("user_profile")
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("auth") {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}