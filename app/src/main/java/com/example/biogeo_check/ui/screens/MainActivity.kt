package com.example.biogeo_check.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel


 import com.example.biogeo_check.data.network.SupabaseClient
 import com.example.biogeo_check.data.repository.AuthRepository
 import com.example.biogeo_check.ui.viewmodel.AuthViewModel
 import com.example.biogeo_check.ui.screens.AuthMasterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Preparamos el Repositorio pasándole nuestro cliente de Supabase
        val authRepository = AuthRepository(SupabaseClient.client)

        setContent {
            // Usamos el tema por defecto de Material 3
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    // 2. Creamos una "fábrica" para enseñarle a Compose cómo construir el AuthViewModel
                    val viewModelFactory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AuthViewModel(authRepository) as T
                        }
                    }

                    // 3. Instanciamos el ViewModel usando la fábrica
                    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)

                    // 4. Llamamos a nuestra pantalla de pruebas
                    AuthMasterScreen(viewModel = authViewModel)
                }
            }
        }
    }
}