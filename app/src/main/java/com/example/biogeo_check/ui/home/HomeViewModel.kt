package com.example.biogeo_check.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biogeo_check.domain.model.FichajeState

class HomeViewModel : ViewModel() {

    private val _userName = MutableLiveData("Usuario")
    val userName: LiveData<String> = _userName

    private val _fichajeState = MutableLiveData(FichajeState.SIN_FICHAR)
    val fichajeState: LiveData<FichajeState> = _fichajeState

    private val _isLocationValid = MutableLiveData<Boolean?>(null)
    val isLocationValid: LiveData<Boolean?> = _isLocationValid

    private val _isLocationLoading = MutableLiveData(true)
    val isLocationLoading: LiveData<Boolean> = _isLocationLoading

    private val _lastFichajeTime = MutableLiveData("--:--")
    val lastFichajeTime: LiveData<String> = _lastFichajeTime

    private val _fichajeConfirmation = MutableLiveData<String?>(null)
    val fichajeConfirmation: LiveData<String?> = _fichajeConfirmation

    fun setUserName(name: String) {
        _userName.value = name
    }

    fun onLocationResult(isValid: Boolean) {
        _isLocationLoading.value = false
        _isLocationValid.value = isValid
    }

    fun onLocationError() {
        _isLocationLoading.value = false
        _isLocationValid.value = false
    }

    /**
     * Ejecutar fichaje según estado actual
     */
    fun realizarFichaje() {
        when (_fichajeState.value) {
            FichajeState.SIN_FICHAR -> {
                // TODO: Llamar API + biometría
                _fichajeState.value = FichajeState.ENTRADA_FICHADA
                _lastFichajeTime.value = getCurrentTime()
                _fichajeConfirmation.value = "✅ Entrada registrada correctamente"
            }
            FichajeState.ENTRADA_FICHADA -> {
                // TODO: Llamar API + biometría
                _fichajeState.value = FichajeState.SALIDA_FICHADA
                _lastFichajeTime.value = getCurrentTime()
                _fichajeConfirmation.value = "✅ Salida registrada correctamente"
            }
            FichajeState.SALIDA_FICHADA -> {
                // No se puede fichar más
            }
            null -> {}
        }
    }

    private fun getCurrentTime(): String {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}
