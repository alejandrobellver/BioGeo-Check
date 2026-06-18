package com.example.biogeo_check.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
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
import com.example.biogeo_check.ui.screens.AdminDepartmentsScreen
import com.example.biogeo_check.ui.screens.AuthMasterScreen
import com.example.biogeo_check.ui.screens.FichajeDashboardScreen
import com.example.biogeo_check.ui.screens.LegalScreen
import com.example.biogeo_check.ui.screens.UserProfileScreen
import com.example.biogeo_check.ui.viewmodel.AuthViewModel
import com.example.biogeo_check.ui.viewmodel.DashboardViewModel
import com.example.biogeo_check.ui.viewmodel.DepartmentsViewModel

class MainActivity : FragmentActivity() {
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
                    val dashboardViewModel: DashboardViewModel =
                        viewModel(factory = dashboardViewModelFactory)

                    val departmentsViewModelFactory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return DepartmentsViewModel(fichajeRepository) as T
                        }
                    }
                    val departmentsViewModel: DepartmentsViewModel =
                        viewModel(factory = departmentsViewModelFactory)

                    AppNavigation(
                        authViewModel = authViewModel,
                        dashboardViewModel = dashboardViewModel,
                        departmentsViewModel = departmentsViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    departmentsViewModel: DepartmentsViewModel
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val termsAccepted = sharedPrefs.getBoolean("terms_accepted", false)

    LaunchedEffect(Unit) {
        if (dashboardViewModel.trabajadorActual == null) {
            dashboardViewModel.cargarDatosIniciales()
        }
    }

    val startDest = if (termsAccepted) "auth" else "legal"

    NavHost(navController = navController, startDestination = startDest) {
        composable("legal") {
            LegalScreen(
                onAccept = {
                    sharedPrefs.edit().putBoolean("terms_accepted", true).apply()
                    navController.navigate("auth") {
                        popUpTo("legal") { inclusive = true }
                    }
                },
                onDecline = { (context as? android.app.Activity)?.finish() }
            )
        }

        composable("auth") {
            AuthMasterScreen(
                viewModel = authViewModel,
                onNavigateToDashboard = { _ ->
                    navController.navigate("fichaje_dashboard") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("fichaje_dashboard") {
            FichajeDashboardScreen(
                vm = dashboardViewModel,
                onNavigate = { screen ->
                    val esJefe = dashboardViewModel.trabajadorActual?.rol == "JEFE"
                    when (screen) {
                        NavScreen.HOME -> {}

                        // Nuevos botones para jefes
                        NavScreen.EMPLOYEES -> {
                            if (esJefe) {
                                navController.navigate("admin_employees")
                            }
                        }

                        NavScreen.DEPARTMENTS -> {
                            if (esJefe) {
                                navController.navigate("admin_departments")
                            }
                        }

                        NavScreen.PROFILE -> navController.navigate("user_profile")
                    }
                }
            )
        }

        composable("admin_employees") {
            com.example.biogeo_check.ui.screens.AdminDashboardScreen(
                vm = dashboardViewModel,
                onNavigate = { screen ->
                    val esJefe = dashboardViewModel.trabajadorActual?.rol == "JEFE"
                    when (screen) {
                        NavScreen.HOME -> navController.navigate("fichaje_dashboard") {
                            popUpTo("fichaje_dashboard") { inclusive = true }
                        }
                        NavScreen.EMPLOYEES -> { /* Ya estamos aquí */
                        }

                        NavScreen.DEPARTMENTS -> {
                            if (esJefe) navController.navigate("admin_departments")
                        }

                        NavScreen.PROFILE -> navController.navigate("user_profile")
                    }
                }
            )
        }

        composable("admin_departments") {
            AdminDepartmentsScreen(
                vm = departmentsViewModel,
                isJefe = dashboardViewModel.trabajadorActual?.rol == "JEFE",
                onNavigate = { screen ->
                    when (screen) {
                        NavScreen.HOME -> navController.navigate("fichaje_dashboard") {
                            popUpTo("fichaje_dashboard") { inclusive = true }
                        }

                        NavScreen.EMPLOYEES -> {
                            val esJefe = dashboardViewModel.trabajadorActual?.rol == "JEFE"
                            if (esJefe) navController.navigate("admin_employees")
                        }

                        NavScreen.DEPARTMENTS -> { /* Ya estamos aquí */
                        }

                        NavScreen.PROFILE -> navController.navigate("user_profile")
                    }
                }
            )
        }

        composable("user_profile") {
            UserProfileScreen(
                vm = dashboardViewModel,
                authViewModel = authViewModel,
                onNavigate = { screen ->
                    val esJefe = dashboardViewModel.trabajadorActual?.rol == "JEFE"
                    when (screen) {
                        NavScreen.HOME -> navController.navigate("fichaje_dashboard") {
                            popUpTo("fichaje_dashboard") { inclusive = true }
                        }

                        NavScreen.EMPLOYEES -> {
                            if (esJefe) {
                                navController.navigate("admin_employees")
                            }
                        }

                        NavScreen.DEPARTMENTS -> {
                            if (esJefe) {
                                navController.navigate("admin_departments")
                            }
                        }

                        NavScreen.PROFILE -> { /* Ya estamos aquí */
                        }
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}