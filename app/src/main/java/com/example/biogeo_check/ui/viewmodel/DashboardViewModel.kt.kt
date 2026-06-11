package com.example.biogeo_check.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biogeo_check.data.model.Departamento
import com.example.biogeo_check.data.model.Fichaje
import com.example.biogeo_check.data.model.TipoContrato
import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.data.repository.FichajeRepository
import kotlinx.coroutines.launch

class `DashboardViewModel.kt`(
    private val fichajeRepository: FichajeRepository
) : ViewModel() {

    // 📋 Estados del Dashboard (Simples y fijos de momento)
    var trabajadorActual by mutableStateOf<Trabajador?>(null)
    var ultimoFichaje by mutableStateOf<Fichaje?>(null)
    var listaContratos by mutableStateOf<List<TipoContrato>>(listOf())
    var contratoSeleccionadoId by mutableStateOf<String?>(null)

    // 👤 Estados de las tablas relacionadas (El Join Lógico)
    var departamento by mutableStateOf<Departamento?>(null)
    var tipoContrato by mutableStateOf<TipoContrato?>(null)
    var listaDepartamentos by mutableStateOf<List<Departamento>>(listOf())

    // 📝 Estados de los campos editables del Perfil
    var emailInput by mutableStateOf("")
    var deptoSeleccionadoId by mutableStateOf<String?>(null)
    var editMode by mutableStateOf(false)

    // 🔍 1. Carga inicial del Dashboard al abrir la App
    fun cargarDatosIniciales() {
        viewModelScope.launch {
            try {
                val userId = fichajeRepository.obtenerIdUsuarioAutenticado() ?: return@launch
                val t = fichajeRepository.obtenerPerfilTrabajador(userId)
                trabajadorActual = t

                trabajadorActual?.let {
                    ultimoFichaje = fichajeRepository.obtenerUltimoFichaje(it.trabajadorId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ⏱️ 2. Acción del botón de fichar Entrada/Salida
    fun alternarFichaje() {
        val trabajador = trabajadorActual ?: return
        viewModelScope.launch {
            try {
                val siguienteAccion = if (ultimoFichaje?.tipoAccion == "ENTRADA") "SALIDA" else "ENTRADA"
                val nuevoLog = fichajeRepository.registrarFichaje(trabajador.trabajadorId, siguienteAccion)
                ultimoFichaje = nuevoLog
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 👤 3. Carga los detalles extra cuando el usuario pulsa en la pestaña de Perfil (El Join)
// 👤 Carga los detalles del perfil de forma aislada y ultra-defensiva
    fun cargarDatosPerfil() {
        viewModelScope.launch {
            // 1. Si no tenemos el trabajador en memoria todavía, lo buscamos
            if (trabajadorActual == null) {
                try {
                    val userId = fichajeRepository.obtenerIdUsuarioAutenticado()
                    if (userId != null) {
                        trabajadorActual = fichajeRepository.obtenerPerfilTrabajador(userId)
                    }
                } catch (e: Exception) {
                    println("❌ Error buscando ID de usuario: ${e.message}")
                }
            }

            val t = trabajadorActual ?: return@launch

            // Inicializamos los inputs de la pantalla de inmediato
            emailInput = t.email
            deptoSeleccionadoId = t.departamentoId
            contratoSeleccionadoId = t.contratoId

            // 🏢 BLOQUE A: Traer el Departamento individual de Santos
            try {
                t.departamentoId?.let { departamento = fichajeRepository.obtenerDepartamento(it) }
            } catch (e: Exception) {
                println("❌ Error en departamento individual: ${e.message}")
            }

            // 📜 BLOQUE B: Traer el Contrato individual de Santos (Primera foto)
            try {
                t.contratoId?.let { tipoContrato = fichajeRepository.obtenerTipoContrato(it) }
            } catch (e: Exception) {
                println("❌ Error en contrato individual: ${e.message}")
            }

            // 🗺️ BLOQUE C: Traer la LISTA completa de departamentos
            try {
                listaDepartamentos = fichajeRepository.obtenerTodosLosDepartamentos()
                println("🏢 LISTA DE DEPARTAMENTOS CARGADA: ${listaDepartamentos.size} encontrados")
            } catch (e: Exception) {
                println("❌ Error cargando LISTA de departamentos: ${e.message}")
            }

            // 📜 BLOQUE D: Traer la LISTA completa de contratos (El desplegable que se resiste)
            try {
                listaContratos = fichajeRepository.obtenerTodosLosContratos()
                println("📜 LISTA DE CONTRATOS CARGADA: ${listaContratos.size} encontrados")
            } catch (e: Exception) {
                println("❌ ERROR CRÍTICO CARGANDO LISTA DE CONTRATOS: El modelo TipoContrato no coincide con Supabase")
                e.printStackTrace() // 👈 Este chivato nos dirá en el Logcat qué columna está fallando
            }
        }
    }

    // 💾 4. Guarda los cambios del perfil en Supabase y actualiza la interfaz
    fun guardarCambiosPerfil() {
        val tId = trabajadorActual?.trabajadorId ?: return
        viewModelScope.launch {
            try {
                // 🚀 El cambio está aquí: antes pasabas 3 datos, ahora le sumas ', contratoSeleccionadoId' al final
                fichajeRepository.actualizarTrabajador(tId, emailInput, deptoSeleccionadoId, contratoSeleccionadoId)

                editMode = false
                val perfilActualizado = fichajeRepository.obtenerPerfilTrabajador(tId)
                trabajadorActual = perfilActualizado
                cargarDatosPerfil()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}