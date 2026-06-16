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

    var listaTrabajadores by mutableStateOf<List<Trabajador>>(listOf())
    var teamStats by mutableStateOf<List<com.example.biogeo_check.ui.screens.EmployeeStat>>(listOf())
    var totalHorasEquipo by mutableStateOf("0h")
    var activosHoy by mutableStateOf("0/0")

    // Estados para el diálogo de invitación
    var showInviteDialog by mutableStateOf(false)
    var inviteEmail by mutableStateOf("")
    var inviteDeptoId by mutableStateOf<String?>(null)
    var inviteContratoId by mutableStateOf<String?>(null)
    var inviteError by mutableStateOf<String?>(null)
    var inviteSuccessMessage by mutableStateOf<String?>(null)

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

    fun cargarTrabajadoresDeLaEmpresa() {
        viewModelScope.launch {
            try {
                val empresaId = trabajadorActual?.empresaId ?: return@launch
                listaTrabajadores = fichajeRepository.obtenerTrabajadoresPorEmpresa(empresaId)
                
                val trabajadorIds = listaTrabajadores.map { it.trabajadorId }
                val fichajesHoy = fichajeRepository.obtenerFichajesDeTrabajadoresHoy(trabajadorIds)
                
                var activos = 0
                var minTotalesEquipo = 0L

                val stats = listaTrabajadores.map { empleado ->
                    val susFichajes = fichajesHoy.filter { it.trabajadorId == empleado.trabajadorId }
                    
                    var estado = "Ausente"
                    if (susFichajes.isNotEmpty()) {
                        val ultimo = susFichajes.last().tipoAccion
                        if (ultimo == "ENTRADA" || ultimo == "VUELTA") {
                            estado = "Fichado"
                            activos++
                        }
                    }

                    // Función local para parsear la fecha de forma segura
                    fun parseSupabaseDate(dateStr: String): java.util.Date? {
                        try {
                            var cleanStr = dateStr.replace(" ", "T") // Cambiar espacio por 'T'
                            if (cleanStr.contains(".")) {
                                cleanStr = cleanStr.substringBefore(".") + "Z"
                            } else if (cleanStr.contains("+")) {
                                cleanStr = cleanStr.substringBefore("+") + "Z"
                            }
                            if (!cleanStr.endsWith("Z")) {
                                cleanStr += "Z"
                            }
                            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
                            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                            return sdf.parse(cleanStr)
                        } catch (e: Exception) {
                            return null
                        }
                    }

                    // Calcular minutos trabajados hoy
                    var minutosTrabajados = 0L
                    var ultimaEntrada: java.util.Date? = null

                    for (f in susFichajes) {
                        try {
                            val fechaObj = parseSupabaseDate(f.horaFichaje ?: continue) ?: continue
                            if (f.tipoAccion == "ENTRADA" || f.tipoAccion == "VUELTA") {
                                ultimaEntrada = fechaObj
                            } else if (f.tipoAccion == "SALIDA" || f.tipoAccion == "PAUSA") {
                                if (ultimaEntrada != null) {
                                    val diffMs = fechaObj.time - ultimaEntrada.time
                                    minutosTrabajados += diffMs / 60000
                                    ultimaEntrada = null
                                }
                            }
                        } catch (e: Exception) { e.printStackTrace() }
                    }

                    if (ultimaEntrada != null && estado == "Fichado") {
                        val ahora = java.util.Date()
                        val diffMs = ahora.time - ultimaEntrada.time
                        minutosTrabajados += diffMs / 60000
                    }

                    minTotalesEquipo += minutosTrabajados
                    val h = minutosTrabajados / 60
                    val m = minutosTrabajados % 60
                    val horasTexto = String.format(java.util.Locale.getDefault(), "%02d:%02d", h, m)

                    val deptoName = listaDepartamentos.find { it.departamentoId == empleado.departamentoId }?.nombreDepartamento ?: "Sin departamento"
                    
                    var lastTimeStr = "--:--"
                    if (susFichajes.isNotEmpty()) {
                        val ultimoFichajeHora = susFichajes.last().horaFichaje
                        if (ultimoFichajeHora != null) {
                            try {
                                val date = parseSupabaseDate(ultimoFichajeHora)
                                if (date != null) {
                                    val sdfLocal = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                    lastTimeStr = sdfLocal.format(date)
                                }
                            } catch (e: Exception) { e.printStackTrace() }
                        }
                    }

                    com.example.biogeo_check.ui.screens.EmployeeStat(
                        name = "${empleado.nombre} ${empleado.apellidos}",
                        deptoName = deptoName,
                        status = estado,
                        todayHours = horasTexto,
                        lastEventTime = lastTimeStr
                    )
                }

                teamStats = stats
                activosHoy = "$activos/${listaTrabajadores.size}"
                val hTotal = minTotalesEquipo / 60
                val mTotal = minTotalesEquipo % 60
                totalHorasEquipo = "${hTotal}h"

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun invitarEmpleado() {
        viewModelScope.launch {
            if (inviteEmail.isBlank()) {
                inviteError = "El email no puede estar vacío"
                return@launch
            }
            if (inviteDeptoId == null || inviteContratoId == null) {
                inviteError = "Selecciona un departamento y un contrato"
                return@launch
            }
            
            val empresaId = trabajadorActual?.empresaId
            if (empresaId == null) {
                inviteError = "Error: no se encontró tu ID de empresa"
                return@launch
            }

            try {
                val invitacion = com.example.biogeo_check.data.model.Invitacion(
                    email = inviteEmail,
                    empresaId = empresaId,
                    rol = "TRABAJADOR",
                    departamentoId = inviteDeptoId,
                    contratoId = inviteContratoId
                )
                fichajeRepository.crearInvitacion(invitacion)
                showInviteDialog = false
                inviteEmail = ""
                inviteDeptoId = null
                inviteContratoId = null
                inviteError = null
                inviteSuccessMessage = "Invitación enviada correctamente."
            } catch (e: Exception) {
                e.printStackTrace()
                inviteError = "Error: ${e.message}"
            }
        }
    }
}