package com.example.biogeo_check.ui.screens

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
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Supabase Auth Repository
        val authRepository = AuthRepository(SupabaseClient.client)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModelFactory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AuthViewModel(authRepository) as T
                        }
                    }
                    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)

                    // Navigation implementation
                    AppNavigation(authViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "legal") {
        composable("legal") {
            LegalScreen(
                onAccept = {
                    // Go to auth after accepting terms
                    navController.navigate("auth") {
                        popUpTo("legal") { inclusive = true }
                    }
                },
                onDecline = {
                    // Handle decline
                }
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
                employeeName = "Usuario",
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
                onNavigate = { screen ->
                    when (screen) {
                        NavScreen.HOME -> navController.popBackStack()
                        NavScreen.HISTORY -> navController.navigate("admin_departments") // Esto asume Jefe. Podría optimizarse.
                        NavScreen.PROFILE -> navController.navigate("user_profile")
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("auth") {
                        popUpTo(0) // clear all backstack
                    }
                }
            )
        }
    }
}