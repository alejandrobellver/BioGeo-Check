package com.example.biogeo_check.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.biogeo_check.data.model.FichajeEvent
import com.example.biogeo_check.data.model.FichajeType

class AdminViewModel : ViewModel() {

    private val _allFichajes = MutableLiveData<List<FichajeEvent>>(emptyList())
    val allFichajes: LiveData<List<FichajeEvent>> = _allFichajes

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadAllFichajes()
    }

    private fun loadAllFichajes() {
        _isLoading.value = true

        // TODO: Cargar fichajes globales desde API
        val sampleData = listOf(
            FichajeEvent("1", "user1", "Juan García", FichajeType.ENTRADA,
                System.currentTimeMillis() - 7200000, 39.47, -0.37, "Sede Principal"),
            FichajeEvent("2", "user2", "María López", FichajeType.ENTRADA,
                System.currentTimeMillis() - 5400000, 39.47, -0.37, "Sede Principal"),
            FichajeEvent("3", "user1", "Juan García", FichajeType.SALIDA,
                System.currentTimeMillis() - 3600000, 39.47, -0.37, "Sede Principal"),
        )

        _allFichajes.value = sampleData
        _isLoading.value = false
    }

    fun exportCsv() {
        // TODO: Implementar exportación CSV
    }

    fun exportPdf() {
        // TODO: Implementar exportación PDF
    }
}
