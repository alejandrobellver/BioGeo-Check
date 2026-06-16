package com.example.biogeo_check.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar la lógica de negocio y el estado de la UI para los procesos
 * de autenticación, registro y gestión de sesiones.
 *
 * Actúa como intermediario entre la vista (Jetpack Compose) y la capa de datos ([AuthRepository]),
 * asegurando que las operaciones pesadas se realicen mediante corrutinas fuera del hilo principal.
 *
 * @property repository Repositorio de autenticación que provee el acceso a los datos.
 */
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    /**
     * Representa de forma cerrada los diferentes estados de la interfaz de usuario (UI)
     * durante el flujo de autenticación controlado por este ViewModel.
     */
    sealed class AuthState {
        /** Estado inicial o de reposo, indicando que no hay ninguna operación activa. */
        object Idle : AuthState()

        /** Estado de carga que indica que se está ejecutando una operación asíncrona en segundo plano. */
        object Loading : AuthState()

        /**
         * Estado de éxito que indica que la operación se completó correctamente.
         *
         * @property trabajador El objeto [Trabajador] autenticado, o null si la operación exitosa no devuelve un usuario.
         */
        data class Success(val trabajador: Trabajador?) : AuthState()

        /**
         * Estado de error que contiene información sobre el fallo ocurrido.
         *
         * @property mensaje Descripción del error destinado a mostrarse en la interfaz de usuario.
         */
        data class Error(val mensaje: String) : AuthState()
    }

    /**
     * Flujo de estado interno y mutable que almacena el estado actual de la autenticación.
     */
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)

    /**
     * Flujo de estado público de solo lectura expuesto a la interfaz de usuario para garantizar la reactividad.
     */
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * Inicia el proceso de inicio de sesión de un usuario de forma asíncrona.
     *
     * @param email Correo electrónico proporcionado por el usuario.
     * @param contrasena Contraseña proporcionada por el usuario.
     */
    fun login(email: String, contrasena: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val trabajador = repository.login(email, contrasena)
                _authState.value = AuthState.Success(trabajador)
            } catch (e: Exception) {
                _authState.value =
                    AuthState.Error(e.message ?: "Error desconocido al iniciar sesión")
            }
        }
    }

    /**
     * Registra simultáneamente una nueva empresa y a su jefe asignado en el sistema de forma asíncrona.
     *
     * @param email Correo electrónico corporativo del jefe.
     * @param contrasena Contraseña para el acceso del jefe.
     * @param nombreEmpresa Nombre comercial o razón social de la empresa.
     * @param cif Código de Identificación Fiscal de la empresa.
     * @param direccion Ubicación física o fiscal de la empresa.
     * @param nombreJefe Nombre de pila del administrador/jefe.
     * @param apellidosJefe Apellidos del administrador/jefe.
     * @param dniJefe Documento Nacional de Identidad del administrador/jefe.
     */
    fun registrarJefeYEmpresa(
        email: String,
        contrasena: String,
        nombreEmpresa: String,
        cif: String,
        direccion: String,
        cp: String,
        ciudad: String,
        nombreJefe: String,
        apellidosJefe: String,
        dniJefe: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.registrarJefeYEmpresa(
                    email = email,
                    contrasena = contrasena,
                    nombreEmpresa = nombreEmpresa,
                    cif = cif,
                    direccion = direccion,
                    cp = cp,
                    ciudad = ciudad,
                    nombreJefe = nombreJefe,
                    apellidosJefe = apellidosJefe,
                    dniJefe = dniJefe
                )
                _authState.value = AuthState.Success(null)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error desconocido al registrar")
            }
        }
    }

    /**
     * Activa la cuenta de un trabajador previamente pre-registrado, estableciendo sus credenciales y datos personales.
     *
     * @param email Correo electrónico del trabajador asignado por la empresa.
     * @param contrasena Nueva contraseña elegida por el trabajador.
     * @param nombre Nombre de pila del trabajador.
     * @param apellidos Apellidos del trabajador.
     * @param dni Documento Nacional de Identidad del trabajador.
     */
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

    /**
     * Cierra la sesión activa del usuario actual y restablece el estado de autenticación.
     */
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

    /**
     * Restablece el flujo de estado de autenticación al estado inicial ([AuthState.Idle]).
     *
     * Útil para limpiar mensajes de error residuales en la pantalla cuando el usuario
     * interactúa de nuevo con los campos de texto.
     */
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}