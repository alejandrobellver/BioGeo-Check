package com.example.biogeo_check.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    private val _name = MutableLiveData("")
    private val _email = MutableLiveData("")
    private val _password = MutableLiveData("")
    private val _confirmPassword = MutableLiveData("")

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    private val _registerSuccess = MutableLiveData(false)
    val registerSuccess: LiveData<Boolean> = _registerSuccess

    fun setName(value: String) { _name.value = value }
    fun setEmail(value: String) { _email.value = value }
    fun setPassword(value: String) { _password.value = value }
    fun setConfirmPassword(value: String) { _confirmPassword.value = value }

    fun validateAndRegister() {
        val nameVal = _name.value ?: ""
        val emailVal = _email.value ?: ""
        val passVal = _password.value ?: ""
        val confirmVal = _confirmPassword.value ?: ""

        when {
            nameVal.isBlank() -> {
                _errorMessage.value = "Introduce tu nombre"
                return
            }
            emailVal.isBlank() -> {
                _errorMessage.value = "Introduce tu correo electrónico"
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(emailVal).matches() -> {
                _errorMessage.value = "Correo electrónico no válido"
                return
            }
            passVal.length < 6 -> {
                _errorMessage.value = "La contraseña debe tener al menos 6 caracteres"
                return
            }
            passVal != confirmVal -> {
                _errorMessage.value = "Las contraseñas no coinciden"
                return
            }
        }

        _errorMessage.value = null
        _isLoading.value = true

        // TODO: Conectar con AuthRepository real
        _isLoading.value = false
        _registerSuccess.value = true
    }
}
