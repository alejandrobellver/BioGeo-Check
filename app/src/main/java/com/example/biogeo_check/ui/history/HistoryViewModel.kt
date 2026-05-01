package com.example.biogeo_check.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biogeo_check.data.model.FichajeEvent
import com.example.biogeo_check.data.model.FichajeType

class HistoryViewModel : ViewModel() {

    private val _fichajes = MutableLiveData<List<FichajeEvent>>(emptyList())
    val fichajes: LiveData<List<FichajeEvent>> = _fichajes

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isEmpty = MutableLiveData(true)
    val isEmpty: LiveData<Boolean> = _isEmpty

    init {
        loadFichajes()
    }

    private fun loadFichajes() {
        _isLoading.value = true

        // TODO: Cargar desde API real
        // Datos de ejemplo por ahora
        val sampleData = listOf(
            FichajeEvent("1", "user1", "Usuario", FichajeType.ENTRADA,
                System.currentTimeMillis() - 3600000, 39.47, -0.37, "Sede Principal"),
            FichajeEvent("2", "user1", "Usuario", FichajeType.SALIDA,
                System.currentTimeMillis() - 1800000, 39.47, -0.37, "Sede Principal"),
            FichajeEvent("3", "user1", "Usuario", FichajeType.ENTRADA,
                System.currentTimeMillis() - 86400000, 39.47, -0.37, "Sede Principal"),
            FichajeEvent("4", "user1", "Usuario", FichajeType.SALIDA,
                System.currentTimeMillis() - 82800000, 39.47, -0.37, "Sede Principal"),
        )

        _fichajes.value = sampleData
        _isEmpty.value = sampleData.isEmpty()
        _isLoading.value = false
    }
}
