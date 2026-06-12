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
    var tiempoTrabajadoHoy by mutableStateOf("00:00")
    var tiempoTrabajadoSemana by mutableStateOf("00:00")
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
                    calcularTiempoTrabajadoHoy()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun calcularTiempoTrabajadoHoy() {
        val tId = trabajadorActual?.trabajadorId ?: return
        viewModelScope.launch {
            try {
                val fichajes = fichajeRepository.obtenerFichajesDeHoy(tId)
                var totalSegundos = 0L
                var entradaParcial: java.time.Instant? = null

                for (f in fichajes) {
                    val hora = f.horaFichaje?.let { java.time.Instant.parse(it) } ?: continue
                    if (f.tipoAccion == "ENTRADA") {
                        entradaParcial = hora
                    } else if (f.tipoAccion == "SALIDA" && entradaParcial != null) {
                        totalSegundos += java.time.Duration.between(entradaParcial, hora).seconds
                        entradaParcial = null
                    }
                }
                
                // Si está trabajando ahora mismo, sumamos el tiempo desde la última entrada hasta ahora
                if (entradaParcial != null) {
                    totalSegundos += java.time.Duration.between(entradaParcial, java.time.Instant.now()).seconds
                }

                val horas = totalSegundos / 3600
                val minutos = (totalSegundos % 3600) / 60
                tiempoTrabajadoHoy = String.format("%02d:%02d", horas, minutos)
                
                // Temporalmente, hacemos que la semana sea igual al día para que no ponga 10.10,
                // idealmente haríamos otra consulta obtenerFichajesDeSemana()
                tiempoTrabajadoSemana = tiempoTrabajadoHoy
            } catch (e: Exception) {
                e.printStackTrace()
            }
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