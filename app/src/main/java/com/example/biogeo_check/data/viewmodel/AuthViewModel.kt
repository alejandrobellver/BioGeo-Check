package com.example.biogeo_check.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biogeo_check.data.model.AuthState
import com.example.biogeo_check.data.repository.AuthRepository // Usamos tu clase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val uuidEmpresaApp: String
) : ViewModel() {

    private val _estado = MutableStateFlow<AuthState>(AuthState.Idle)
    val estado = _estado.asStateFlow()

    fun intentarLogin(email: String) {
        viewModelScope.launch {
            _estado.value = AuthState.Loading // 1. Indicamos que estamos buscando 🔄

            try {
                // 2. Llamamos a tu repositorio con el email y el ID de la empresa
                val trabajador = authRepository.obtenerTrabajadorPorEmail(email, uuidEmpresaApp)

                if (trabajador == null) {
                    // 3. Si el repo devuelve null, es que no existe o hubo error
                    _estado.value = AuthState.Error("Correo no registrado o empresa incorrecta.")
                } else {
                    // 4. Si lo encuentra, ¡éxito! ✅
                    _estado.value = AuthState.Authenticated(trabajador)
                }
            } catch (e: Exception) {
                // 5. Por si acaso hay un fallo de red inesperado
                _estado.value = AuthState.Error("Error de conexión: ${e.localizedMessage}")
            }
        }
    }
}