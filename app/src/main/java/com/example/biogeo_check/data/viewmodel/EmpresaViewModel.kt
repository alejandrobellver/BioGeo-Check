package com.example.biogeo_check.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biogeo_check.data.model.Empresa
import com.example.biogeo_check.data.repository.EmpresaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmpresaViewModel : ViewModel() {

    // 1. Instanciamos a nuestro "gestor"
    private val repository = EmpresaRepository()

    // 2. EL ESTADO DE LOS DATOS (Lo que verá la pantalla)
    // _empresas es PRIVADA. Solo el ViewModel puede meter o sacar empresas de esta lista.
    private val _empresas = MutableStateFlow<List<Empresa>>(emptyList())
    // empresas es PÚBLICA (solo lectura). La pantalla observará esta variable.
    val empresas: StateFlow<List<Empresa>> = _empresas.asStateFlow()

    // Variable extra muy útil para mostrar la típica "ruedita de carga" (Spinner) en pantalla
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()


    // 3. FUNCIONES QUE LLAMARÁ LA INTERFAZ (BOTONES)

    // Esta función la llamará la pantalla nada más abrirse
    fun cargarListaDeEmpresas() {
        // viewModelScope es un hilo especial del ViewModel que se cancela solo si cierras la app
        viewModelScope.launch {
            _cargando.value = true // Avisamos a la pantalla: "¡Pon la ruedita a girar!"

            // Le pedimos los datos al repositorio
            val lista = repository.obtenerTodasLasEmpresas()

            // Actualizamos nuestra variable observable. La pantalla se enterará automáticamente.
            _empresas.value = lista

            _cargando.value = false // Avisamos a la pantalla: "¡Quita la ruedita!"
        }
    }

    // Esta función la llamará el botón "Guardar" de tu pantalla
    fun crearNuevaEmpresa(nombre: String, cif: String) {
        viewModelScope.launch {
            _cargando.value = true

            // Le decimos al repositorio que lo suba a Supabase
            repository.crearEmpresa(nombre, cif)

            // Como hemos añadido una nueva, volvemos a descargar la lista para que se actualice
            cargarListaDeEmpresas()
        }
    }
}