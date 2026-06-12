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
import com.example.biogeo_check.data.repository.FichajeRepository
import com.example.biogeo_check.ui.components.NavScreen
import com.example.biogeo_check.ui.screens.AdminDashboardScreen
import com.example.biogeo_check.ui.screens.AdminDepartmentsScreen
import com.example.biogeo_check.ui.screens.AuthMasterScreen
import com.example.biogeo_check.ui.screens.EmployeeDashboardScreen
import com.example.biogeo_check.ui.screens.LegalScreen
import com.example.biogeo_check.ui.screens.UserProfileScreen
import com.example.biogeo_check.ui.viewmodel.AuthViewModel
import com.example.biogeo_check.ui.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepository(SupabaseClient.client)
        val fichajeRepository = FichajeRepository(SupabaseClient.client)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModelFactory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AuthViewModel(authRepository) as T
                        }
                    }
                    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)

                    val dashboardViewModelFactory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return DashboardViewModel(fichajeRepository) as T
                        }
                    }
                    val dashboardViewModel: DashboardViewModel = viewModel(factory = dashboardViewModelFactory)

                    AppNavigation(authViewModel = authViewModel, dashboardViewModel = dashboardViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel
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
                vm = dashboardViewModel,
                onNavigate = { screen ->
                    when (screen) {
                        NavScreen.HOME -> navController.navigate("employee_dashboard")
                        NavScreen.HISTORY -> { }
                        NavScreen.PROFILE -> navController.navigate("user_profile")
                    }
                }
            )
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(
                vm = dashboardViewModel,
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
                vm = dashboardViewModel,
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
                vm = dashboardViewModel,
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