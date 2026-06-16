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

class DashboardViewModel(
    private val fichajeRepository: FichajeRepository
) : ViewModel() {

    var trabajadorActual by mutableStateOf<Trabajador?>(null)
    var ultimoFichaje by mutableStateOf<Fichaje?>(null)
    var listaContratos by mutableStateOf<List<TipoContrato>>(listOf())
    var contratoSeleccionadoId by mutableStateOf<String?>(null)

    var horaFichajeTexto by mutableStateOf("")
    var horaSiguienteEventoTexto by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)

    var departamento by mutableStateOf<Departamento?>(null)
    var tipoContrato by mutableStateOf<TipoContrato?>(null)
    var listaDepartamentos by mutableStateOf<List<Departamento>>(listOf())

    var emailInput by mutableStateOf("")
    var deptoSeleccionadoId by mutableStateOf<String?>(null)
    var editMode by mutableStateOf(false)

    fun cargarDatosIniciales() {
        viewModelScope.launch {
            try {
                val userId = fichajeRepository.obtenerIdUsuarioAutenticado() ?: return@launch
                val t = fichajeRepository.obtenerPerfilTrabajador(userId)
                trabajadorActual = t

                trabajadorActual?.let {
                    ultimoFichaje = fichajeRepository.obtenerUltimoFichaje(it.trabajadorId)

                    // 🚀 1. Nos traemos el contrato de Supabase PRIMERO de forma síncrona
                    it.contratoId?.let { cId ->
                        tipoContrato = fichajeRepository.obtenerTipoContrato(cId)
                    }

                    // 🚀 2. AHORA SÍ, con el contrato ya guardado en memoria, calculamos las horas
                    calcularTiempoTrabajadoHoy()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun calcularTiempoTrabajadoHoy() {
        val trabajador = trabajadorActual ?: return

        val cal = java.util.Calendar.getInstance()
        val horaActual = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val minutoActual = cal.get(java.util.Calendar.MINUTE)

        val horasSemanalesCelda = tipoContrato?.horasSemanales ?: 40

        val horasJornadaDiaria = horasSemanalesCelda / 5

        val esEntrada = ultimoFichaje?.tipoAccion == "ENTRADA"

        if (esEntrada) {
            val horaEntradaTexto = String.format(java.util.Locale.getDefault(), "%02d:%02d", horaActual, minutoActual)

            val horaSalidaCalculada = (horaActual + horasJornadaDiaria) % 24
            val horaSalidaTexto = String.format(java.util.Locale.getDefault(), "%02d:%02d", horaSalidaCalculada, minutoActual)

            // Asignamos fijas a las variables del Dashboard
            horaFichajeTexto = horaEntradaTexto
            horaSiguienteEventoTexto = horaSalidaTexto
        } else {
            val horaEntradaTexto = String.format(java.util.Locale.getDefault(), "%02d:%02d", horaActual, minutoActual)

            val horaSalidaCalculada = (horaActual + horasJornadaDiaria) % 24
            val horaSalidaTexto = String.format(java.util.Locale.getDefault(), "%02d:%02d", horaSalidaCalculada, minutoActual)

            // Asignamos fijas a las variables del Dashboard
            horaFichajeTexto = horaEntradaTexto
            horaSiguienteEventoTexto = horaSalidaTexto
        }
    }
    fun alternarFichaje() {
        val trabajador = trabajadorActual
        if (trabajador == null) {
            errorMessage = "Error Crítico: No hay usuario en memoria."
            return
        }
        viewModelScope.launch {
            try {
                val siguienteAccion = if (ultimoFichaje?.tipoAccion == "ENTRADA") "SALIDA" else "ENTRADA"
                val nuevoLog = fichajeRepository.registrarFichaje(trabajador.trabajadorId, siguienteAccion)
                ultimoFichaje = nuevoLog

                // Recargamos el contrato por si acaso ha cambiado en el perfil
                trabajador.contratoId?.let { cId ->
                    tipoContrato = fichajeRepository.obtenerTipoContrato(cId)
                }

                calcularTiempoTrabajadoHoy()
                errorMessage = null
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Error BD: ${e.message}"
            }
        }
    }

    fun cargarDatosPerfil() {
        viewModelScope.launch {
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

            emailInput = t.email
            deptoSeleccionadoId = t.departamentoId
            contratoSeleccionadoId = t.contratoId

            try {
                t.departamentoId?.let { departamento = fichajeRepository.obtenerDepartamento(it) }
            } catch (e: Exception) {
                println("❌ Error en departamento individual: ${e.message}")
            }

            try {
                t.contratoId?.let { tipoContrato = fichajeRepository.obtenerTipoContrato(it) }
            } catch (e: Exception) {
                println("❌ Error en contrato individual: ${e.message}")
            }

            try {
                listaDepartamentos = fichajeRepository.obtenerTodosLosDepartamentos()
                println("🏢 LISTA DE DEPARTAMENTOS CARGADA: ${listaDepartamentos.size} encontrados")
            } catch (e: Exception) {
                println("❌ Error cargando LISTA de departamentos: ${e.message}")
            }

            try {
                listaContratos = fichajeRepository.obtenerTodosLosContratos()
                println("📜 LISTA DE CONTRATOS CARGADA: ${listaContratos.size} encontrados")
            } catch (e: Exception) {
                println("❌ ERROR CRÍTICO CARGANDO LISTA DE CONTRATOS: El modelo TipoContrato no coincide con Supabase")
                e.printStackTrace()
            }
        }
    }

    fun guardarCambiosPerfil() {
        val tId = trabajadorActual?.trabajadorId ?: return
        viewModelScope.launch {
            try {
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