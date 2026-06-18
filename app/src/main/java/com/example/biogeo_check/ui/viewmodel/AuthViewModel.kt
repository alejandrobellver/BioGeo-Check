package com.example.biogeo_check.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.data.repository.AuthRepository
import com.example.biogeo_check.util.LocationHelper.obtenerCoordenadasDesdeDireccion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar la lógica de negocio y el estado de la UI para los procesos
 * de autenticación, registro y gestión de sesiones.
 */
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val trabajador: Trabajador?, val mensajeExito: String? = null) :
            AuthState()

        data class Error(val mensaje: String) : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, contrasena: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val trabajador = repository.login(email.trim().lowercase(), contrasena)
                _authState.value = AuthState.Success(trabajador)
            } catch (e: Exception) {
                _authState.value =
                    AuthState.Error(e.message ?: "Error desconocido al iniciar sesión")
            }
        }
    }

    fun registrarJefeYEmpresa(
        context: android.content.Context,
        email: String,
        contrasena: String,
        nombreEmpresa: String,
        cif: String,
        direccion: String,
        cp: Int,
        ciudad: String,
        nombreJefe: String,
        apellidosJefe: String,
        dniJefe: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            try {
                val direccionCompleta = "$direccion, $cp, $ciudad, España"

                val coordenadas = obtenerCoordenadasDesdeDireccion(context, direccionCompleta)

                // 2. Pasar los datos calculados al repositorio de Supabase
                repository.registrarJefeYEmpresa(
                    email = email.trim().lowercase(),
                    contrasena = contrasena,
                    nombreEmpresa = nombreEmpresa,
                    cif = cif,
                    direccion = direccion,
                    cp = cp,
                    ciudad = ciudad,
                    nombreJefe = nombreJefe,
                    apellidosJefe = apellidosJefe,
                    dniJefe = dniJefe,
                    latitudCalculada = coordenadas?.first,
                    longitudCalculada = coordenadas?.second
                )
                _authState.value = AuthState.Success(null)
            } catch (e: Exception) {
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
                val emailNormalizado = email.trim().lowercase()
                repository.activarCuentaTrabajador(
                    emailNormalizado,
                    contrasena,
                    nombre,
                    apellidos,
                    dni
                )
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

    fun cambiarContrasenaSegura(
        emailActual: String,
        contrasenaVieja: String,
        contrasenaNueva1: String,
        contrasenaNueva2: String,
        onResultado: (exito: Boolean, mensaje: String) -> Unit
    ) {
        viewModelScope.launch {
            if (contrasenaVieja.isBlank() || contrasenaNueva1.isBlank() || contrasenaNueva2.isBlank()) {
                onResultado(false, "Todos los campos son obligatorios.")
                return@launch
            }
            if (contrasenaNueva1 != contrasenaNueva2) {
                onResultado(false, "Las nuevas contraseñas no coinciden.")
                return@launch
            }
            if (contrasenaNueva1.length < 6) {
                onResultado(false, "La nueva contraseña debe tener al menos 6 caracteres.")
                return@launch
            }

            try {
                repository.login(emailActual, contrasenaVieja)
                repository.cambiarContrasena(contrasenaNueva1)
                onResultado(true, "¡Contraseña actualizada con éxito!")
            } catch (e: Exception) {
                onResultado(false, "La contraseña actual no es correcta o ha ocurrido un error.")
            }
        }
    }

    // =============================================================================================
    // 🚀 NUEVA: FUNCIÓN CORREGIDA Y CONECTADA CON TU PANTALLA MASTER
    // =============================================================================================
    /**
     * Lanza la petición de envío del código OTP de recuperación al correo indicado.
     */
    fun enviarCorreoRecuperacion(
        email: String,
        onResultado: (exito: Boolean, mensaje: String) -> Unit
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                if (email.isBlank()) {
                    throw Exception("El correo electrónico es obligatorio.")
                }

                // Llama al metodo exacto de tu AuthRepository
                repository.enviarCodigoRecuperacion(email.trim().lowercase())

                _authState.value = AuthState.Idle
                onResultado(true, "¡Código enviado con éxito!")
            } catch (e: Exception) {
                _authState.value =
                    AuthState.Error(e.message ?: "Error al enviar el código de recuperación")
                onResultado(false, e.message ?: "Error desconocido")
            }
        }
    }

    fun verificarYRestablecerContrasena(
        email: String,
        codigo: String,
        nuevaPass1: String,
        nuevaPass2: String,
        onResultado: (exito: Boolean, mensaje: String) -> Unit
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                if (codigo.isBlank() || nuevaPass1.isBlank() || nuevaPass2.isBlank()) {
                    throw Exception("Todos los campos son obligatorios.")
                }
                if (nuevaPass1 != nuevaPass2) {
                    throw Exception("Las nuevas contraseñas no coinciden.")
                }
                if (nuevaPass1.length < 6) {
                    throw Exception("La contraseña debe tener al menos 6 caracteres.")
                }

                repository.verificarCodigoOTP(email, codigo)
                try {
                    repository.actualizarContrasenaOlvidada(nuevaPass1)
                } catch (e: Exception) {
                    val msg = e.message ?: ""
                    if (msg.contains("different from the old password", ignoreCase = true) ||
                        msg.contains("same password", ignoreCase = true) ||
                        msg.contains("misma contraseña", ignoreCase = true)
                    ) {
                        throw Exception("No puede ser tu contraseña antigua")
                    }
                    throw e
                }

                _authState.value = AuthState.Success(
                    null,
                    "✅ ¡Contraseña cambiada con éxito! Ya puedes iniciar sesión."
                )
                onResultado(true, "¡Contraseña cambiada con éxito! Ya puedes iniciar sesión.")
            } catch (e: Exception) {
                _authState.value = AuthState.Idle
                onResultado(false, e.message ?: "Error en la verificación")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}