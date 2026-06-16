package com.example.biogeo_check.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// 1. Definimos los estados, los primeros dos son para la UI para que sepa en que momento estamos
// Los segundos los data class es un objeto sitodo va bien o mal, devuelve unos elementos u otros
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val trabajador: Trabajador?) : AuthState()
    data class Error(val mensaje: String) : AuthState()
}

// 2. Lo mismo que hacemos en acceso a datos, pero con los estados
//  Instanciamos que atraves del viewmodel accedemos al repositorio
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // "_" indica es mutable, aqui se va a guardar el estado de la autenticación
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)

    // este es el estado que sube al UI, no se puede modificar desde fuera
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Son las mismas funciones que el repositorio, pero con el estado para notificar al UI

    fun login(email: String, contrasena: String) {
        // IMPORTANTE CORRUTINA, para que no estes esperando en el hilo principal
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Llamamos al repositorio. Si va bien, nos devuelve el trabajador
                val trabajador = repository.login(email, contrasena)
                _authState.value = AuthState.Success(trabajador)
            } catch (e: Exception) {
                // Si falla, atrapamos el error y lo enviamos a la UI
                _authState.value =
                    AuthState.Error(e.message ?: "Error desconocido al iniciar sesión")
            }
        }
    }

    fun registrarJefeYEmpresa(
        email: String,
        contrasena: String,
        nombreEmpresa: String,
        cif: String,
        direccion: String,
        nombreJefe: String,
        apellidosJefe: String,
        dniJefe: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // 🚀 Se los pasamos todos al repositorio optimizado
                repository.registrarJefeYEmpresa(
                    email = email,
                    contrasena = contrasena,
                    nombreEmpresa = nombreEmpresa,
                    cif = cif,
                    direccion = direccion,
                    nombreJefe = nombreJefe,
                    apellidosJefe = apellidosJefe,
                    dniJefe = dniJefe
                )

                // Si todo va bien, pasamos al estado de éxito
                _authState.value = AuthState.Success(null)

            } catch (e: Exception) {
                // Si algo falla (ej: el CIF ya existe o el correo está mal), capturamos el error
                _authState.value = AuthState.Error(e.message ?: "Error desconocido al registrar")
            }
        }
    }

    fun activarCuentaTrabajador(
        email: String,
        contrasena: String,
        nombre: String,
        apellidos: String,
        dni: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.activarCuentaTrabajador(email, contrasena, nombre, apellidos, dni)
                _authState.value = AuthState.Success(null)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al activar la cuenta")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.logout()
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al cerrar sesión")
            }
        }
    }

    // Función complementaría para limpiar el error de la pantalla si el usuario empieza a escribir de nuevo
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}