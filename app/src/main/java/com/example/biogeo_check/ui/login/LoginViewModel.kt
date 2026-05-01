package com.example.biogeo_check.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel para la pantalla de Login
 */
class LoginViewModel : ViewModel() {

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _loginSuccess = MutableLiveData(false)
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    fun setEmail(value: String) { _email.value = value }
    fun setPassword(value: String) { _password.value = value }

    fun validateAndLogin() {
        val emailVal = _email.value ?: ""
        val passVal = _password.value ?: ""

        // Validación
        when {
            emailVal.isBlank() -> {
                _errorMessage.value = "Introduce tu correo electrónico"
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(emailVal).matches() -> {
                _errorMessage.value = "Correo electrónico no válido"
                return
            }
            passVal.isBlank() -> {
                _errorMessage.value = "Introduce tu contraseña"
                return
            }
            passVal.length < 6 -> {
                _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
                return
            }
        }

        _errorMessage.value = null
        _isLoading.value = true

        // TODO: Conectar con AuthRepository real
        // Por ahora simula éxito
        _isLoading.value = false
        _loginSuccess.value = true
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
