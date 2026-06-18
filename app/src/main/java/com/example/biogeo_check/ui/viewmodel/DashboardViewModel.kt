package com.example.biogeo_check.ui.viewmodel

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.biogeo_check.data.model.Departamento
import com.example.biogeo_check.data.model.Fichaje
import com.example.biogeo_check.data.model.TipoContrato
import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.data.network.SupabaseClient
import com.example.biogeo_check.data.repository.FichajeRepository
import com.example.biogeo_check.ui.screens.EmployeeStat
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de centralizar y gestionar las operaciones del panel principal (Dashboard).
 * * Suministra de forma reactiva los datos del estado de la jornada del trabajador activo, la gestión
 * del perfil del empleado, la visualización de métricas en tiempo real del equipo de la empresa
 * y el flujo de envío de invitaciones para nuevos miembros.
 *
 * @property fichajeRepository Repositorio que unifica las consultas y mutaciones de datos relacionados con fichajes, perfiles, contratos y departamentos.
 */
class DashboardViewModel(
    private val fichajeRepository: FichajeRepository
) : ViewModel() {

    // ==========================================
    // ESTADOS REACTIVOS (TRABAJADOR ACTUAL)
    // ==========================================

    /** Datos del perfil del trabajador autenticado en la sesión actual. */
    var trabajadorActual by mutableStateOf<Trabajador?>(null)

    /** Registro del último fichaje efectuado por el usuario (ENTRADA/SALIDA). */
    var ultimoFichaje by mutableStateOf<Fichaje?>(null)

    /** Catálogo completo de modalidades de contratación disponibles en la aplicación. */
    var listaContratos by mutableStateOf<List<TipoContrato>>(listOf())

    /** Identificador único del contrato seleccionado actualmente por el usuario. */
    var contratoSeleccionadoId by mutableStateOf<String?>(null)

    /** Representación en texto formateado ("HH:mm") del tiempo total laborado durante el día de hoy. */
    var tiempoTrabajadoHoy by mutableStateOf("00:00")


    // ==========================================
    // ESTADOS REACTIVOS (PANEL DE EQUIPO / JEFE)
    // ==========================================

    /** Lista global de todos los trabajadores pertenecientes a la misma empresa. */
    var listaTrabajadores by mutableStateOf<List<Trabajador>>(listOf())

    /** Estadísticas procesadas de los empleados para su renderizado en la interfaz. */
    var teamStats by mutableStateOf<List<EmployeeStat>>(listOf())

    /** Computo global de horas acumuladas portodo el equipo durante la jornada actual. */
    var totalHorasEquipo by mutableStateOf("0h")

    /** Ratio en formato texto que indica cuántos empleados están activos respecto al total (ej. "3/10"). */
    var activosHoy by mutableStateOf("0/0")


    // ==========================================
    // ESTADOS REACTIVOS (FORMULARIO DE INVITACIÓN)
    // ==========================================

    /** Controla la visibilidad del cuadro de diálogo para invitar a nuevos empleados. */
    var showInviteDialog by mutableStateOf(false)

    /** Dirección de correo electrónico del destinatario de la invitación. */
    var inviteEmail by mutableStateOf("")

    /** ID del departamento al cual se asignará al futuro empleado. */
    var inviteDeptoId by mutableStateOf<String?>(null)

    /** ID del tipo de contrato que se le asociará al futuro empleado. */
    var inviteContratoId by mutableStateOf<String?>(null)

    /** Mensaje de error específico para el flujo de invitaciones. */
    var inviteError by mutableStateOf<String?>(null)

    /** Mensaje de confirmación tras haber enviado una invitación de forma exitosa. */
    var inviteSuccessMessage by mutableStateOf<String?>(null)


    // ==========================================
    // ESTADOS REACTIVOS (INTERFAZ GENERAL Y PERFIL)
    // ==========================================

    /** Texto descriptivo de la hora en la que se realizó el fichaje actual. */
    var horaFichajeTexto by mutableStateOf("")

    /** Texto predictivo de la hora estimada de salida o del siguiente hito laboral. */
    var horaSiguienteEventoTexto by mutableStateOf("")

    /** Mensaje global de error de la pantalla mapeado para comprensión del usuario. */
    var errorMessage by mutableStateOf<String?>(null)

    /** Información detallada del departamento al que pertenece el usuario autenticado. */
    var departamento by mutableStateOf<Departamento?>(null)

    /** Información del contrato específico asignado al usuario. */
    var tipoContrato by mutableStateOf<TipoContrato?>(null)

    /** Catálogo completo de departamentos creados en la organización. */
    var listaDepartamentos by mutableStateOf<List<Departamento>>(listOf())

    /** Campo de entrada para la edición del correo electrónico en la sección de perfil. */
    var emailInput by mutableStateOf("")

    /** ID del departamento seleccionado en los menús desplegables de configuración. */
    var deptoSeleccionadoId by mutableStateOf<String?>(null)

    /** Determina si el usuario se encuentra actualmente en modo de edición de sus datos de perfil. */
    var editMode by mutableStateOf(false)


    /**
     * Consulta y descarga de forma asíncrona la información requerida al iniciar la pantalla del Dashboard.
     * * Resuelve el ID del usuario en sesión, descarga su perfil, localiza su último marcaje
     * y obtiene los detalles de su contrato para calcular las horas de la jornada.
     */
    fun cargarDatosIniciales() {
        viewModelScope.launch {
            try {
                if (!SupabaseClient.checkSession()) {
                    trabajadorActual = null
                    return@launch
                }
                val userId = fichajeRepository.obtenerIdUsuarioAutenticado() ?: return@launch
                val t = fichajeRepository.obtenerPerfilTrabajador(userId)
                trabajadorActual = t

                trabajadorActual?.let {
                    ultimoFichaje = fichajeRepository.obtenerUltimoFichaje(it.trabajadorId)

                    it.contratoId?.let { cId ->
                        tipoContrato = fichajeRepository.obtenerTipoContrato(cId)
                    }

                    calcularTiempoTrabajadoHoy()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = obtenerMensajeErrorHumano(e)
            }
        }
    }

    private fun calcularTiempoTrabajadoHoy() {
        val sdfLocal = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        sdfLocal.timeZone = java.util.TimeZone.getDefault()

        val horasSemanalesCelda = tipoContrato?.horasSemanales ?: 40
        val horasJornadaDiaria = horasSemanalesCelda / 5

        val ultimo = ultimoFichaje
        if (ultimo?.horaFichaje != null) {
            val parsed = parseSupabaseDate(ultimo.horaFichaje!!)
            if (parsed != null) {
                horaFichajeTexto = sdfLocal.format(parsed)

                val calInicio = java.util.Calendar.getInstance()
                calInicio.time = parsed
                val hInicio = calInicio.get(java.util.Calendar.HOUR_OF_DAY)
                val mInicio = calInicio.get(java.util.Calendar.MINUTE)
                val hSalidaCalc = (hInicio + horasJornadaDiaria.toInt()) % 24
                horaSiguienteEventoTexto = String.format(
                    java.util.Locale.getDefault(), "%02d:%02d", hSalidaCalc, mInicio
                )

                if (ultimo.tipoAccion in listOf("ENTRADA", "VUELTA", "PAUSA")) {
                    val ahora = java.util.Date()
                    val diffMs = ahora.time - parsed.time
                    val mins = diffMs / 60000
                    val h = mins / 60
                    val m = mins % 60
                    tiempoTrabajadoHoy = String.format(
                        java.util.Locale.getDefault(), "%02d:%02d", h, m
                    )
                } else {
                    tiempoTrabajadoHoy = "--:--"
                }
            } else {
                val cal = java.util.Calendar.getInstance()
                horaFichajeTexto = String.format(
                    java.util.Locale.getDefault(), "%02d:%02d",
                    cal.get(java.util.Calendar.HOUR_OF_DAY),
                    cal.get(java.util.Calendar.MINUTE)
                )
                horaSiguienteEventoTexto = "--:--"
                tiempoTrabajadoHoy = "--:--"
            }
        } else {
            val cal = java.util.Calendar.getInstance()
            horaFichajeTexto = String.format(
                java.util.Locale.getDefault(), "%02d:%02d",
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE)
            )
            horaSiguienteEventoTexto = "--:--"
            tiempoTrabajadoHoy = "--:--"
        }
    }

    private fun parseSupabaseDate(dateStr: String): java.util.Date? {
        try {
            val cleaned = dateStr.replace(" ", "T")
                .replaceFirst(Regex("\\.\\d+"), "")
                .replaceFirst(Regex("[+-]\\d{2}:\\d{2}$"), "Z")
                .let { if (!it.endsWith("Z")) it + "Z" else it }
            val sdf = java.text.SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault()
            )
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            return sdf.parse(cleaned)
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Alterna de forma segura el estado de la jornada laboral del usuario (conmutación entre ENTRADA y SALIDA).
     * * Inserta un nuevo registro en el historial de eventos, invalida la caché del contrato actual,
     * recalcula las métricas diarias y maneja las excepciones de red de forma temporal visualizándolas en la UI.
     */
    fun alternarFichaje(latitud: Double = 0.0, longitud: Double = 0.0) {
        val trabajador = trabajadorActual
        if (trabajador == null) {
            errorMessage = "Error Crítico: No hay usuario en memoria."
            return
        }
        viewModelScope.launch {
            try {
                val siguienteAccion = when (ultimoFichaje?.tipoAccion) {
                    "ENTRADA", "VUELTA" -> "SALIDA"
                    "PAUSA" -> "VUELTA"
                    else -> "ENTRADA"
                }
                val nuevoLog =
                    fichajeRepository.registrarFichaje(trabajador.trabajadorId, siguienteAccion, latitud, longitud)
                ultimoFichaje = nuevoLog

                trabajador.contratoId?.let { cId ->
                    tipoContrato = fichajeRepository.obtenerTipoContrato(cId)
                }

                calcularTiempoTrabajadoHoy()
                errorMessage = null
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = obtenerMensajeErrorHumano(e)
                kotlinx.coroutines.delay(5000)
                errorMessage = null
            }
        }
    }

    /**
     * Descarga y enlaza todas las propiedades relativas al perfil privado del trabajador.
     * * Carga de forma masiva los catálogos auxiliares de contratos y departamentos requeridos
     * para alimentar los componentes de selección de la vista de configuración de usuario.
     */
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
            } catch (e: Exception) {
                println("❌ Error cargando LISTA de departamentos: ${e.message}")
            }

            try {
                listaContratos = fichajeRepository.obtenerTodosLosContratos()
            } catch (e: Exception) {
                println("❌ ERROR CRÍTICO CARGANDO LISTA DE CONTRATOS: El modelo TipoContrato no coincide con Supabase")
                e.printStackTrace()
            }
        }
    }

    /**
     * Persiste en la base de datos remota las modificaciones realizadas sobre el perfil del empleado.
     * * Al finalizar la operación de guardado, desactiva el flag [editMode] e invalida el estado
     * local forzando una recarga de sincronización con el servidor.
     */
    fun guardarCambiosPerfil() {
        val tId = trabajadorActual?.trabajadorId ?: return
        viewModelScope.launch {
            try {
                fichajeRepository.actualizarTrabajador(
                    tId,
                    emailInput,
                    deptoSeleccionadoId,
                    contratoSeleccionadoId
                )

                editMode = false
                val perfilActualizado = fichajeRepository.obtenerPerfilTrabajador(tId)
                trabajadorActual = perfilActualizado
                cargarDatosPerfil()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Recupera y computa las métricas de asistencia de todos los subordinados o compañeros de la corporación.
     * * Analiza cronológicamente la secuencia de marcajes diarios de cada trabajador, determina su estado
     * en tiempo real (Ausente/Fichado), calcula las sumas de minutos laborados traduciéndolos a texto legible
     * y actualiza variables de impacto global como [activosHoy] y [totalHorasEquipo].
     */
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
                    val susFichajes =
                        fichajesHoy.filter { it.trabajadorId == empleado.trabajadorId }

                    var estado = "Ausente"
                    if (susFichajes.isNotEmpty()) {
                        val ultimo = susFichajes.last().tipoAccion
                        if (ultimo == "ENTRADA" || ultimo == "VUELTA") {
                            estado = "Fichado"
                            activos++
                        }
                    }

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
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
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

                    val deptoName =
                        listaDepartamentos.find { it.departamentoId == empleado.departamentoId }?.nombreDepartamento
                            ?: "Sin departamento"

                    var lastTimeStr = "--:--"
                    if (susFichajes.isNotEmpty()) {
                        val ultimoFichajeHora = susFichajes.last().horaFichaje
                        if (ultimoFichajeHora != null) {
                            try {
                                val date = parseSupabaseDate(ultimoFichajeHora)
                                if (date != null) {
                                    val sdfLocal = java.text.SimpleDateFormat(
                                        "HH:mm",
                                        java.util.Locale.getDefault()
                                    )
                                    lastTimeStr = sdfLocal.format(date)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    EmployeeStat(
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
                totalHorasEquipo = "${hTotal}h"

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Genera e inserta una nueva invitación para la incorporación de un nuevo trabajador a la empresa.
     * * Valida la integridad del formulario en pantalla. En caso de fallas controladas o respuestas
     * HTTP fallidas (como errores 400 o 500 derivados de restricciones de clave duplicada en la base de datos),
     * intercepta la excepción para imprimir un mensaje comprensible al administrador.
     */
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
                val emailNormalizado = inviteEmail.trim().lowercase()
                val invitacion = com.example.biogeo_check.data.model.Invitacion(
                    email = emailNormalizado,
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
                inviteError = obtenerMensajeErrorHumano(e)
                if (e.message?.contains("500") == true || e.message?.contains("400") == true) {
                    inviteError = "Este correo ya está registrado o hay un error de conexión."
                }

                kotlinx.coroutines.delay(5000)
                inviteError = null
            }
        }
    }

    /**
     * Analiza las excepciones técnicas de red o de base de datos relacional y las traduce
     * a mensajes amigables e intuitivos orientados al usuario final.
     * * @param e Excepción capturada durante la ejecución de las operaciones del repositorio.
     * @return Una cadena de texto con la traducción comprensible del problema.
     */
    private fun obtenerMensajeErrorHumano(e: Exception): String {
        val msg = e.toString()
        return when {
            msg.contains(
                "User already registered",
                ignoreCase = true
            ) || msg.contains("already exists", ignoreCase = true) ->
                "Este correo ya está registrado o invitado en el sistema."

            msg.contains(
                "duplicate key value",
                ignoreCase = true
            ) || msg.contains("invitaciones_email_key", ignoreCase = true) ->
                "Este correo ya ha sido invitado."

            msg.contains("Network", ignoreCase = true) || msg.contains(
                "UnknownHost",
                ignoreCase = true
            ) || msg.contains("ConnectException", ignoreCase = true) ->
                "Comprueba tu conexión a internet."

            msg.contains("timeout", ignoreCase = true) ->
                "La conexión ha tardado demasiado, inténtalo de nuevo."

            msg.contains("invalid email", ignoreCase = true) || msg.contains(
                "Valid email",
                ignoreCase = true
            ) ->
                "El formato del correo no es válido."

            msg.contains("not found", ignoreCase = true) ->
                "No se ha encontrado la información."

            msg.contains("500", ignoreCase = true) || msg.contains(
                "400",
                ignoreCase = true
            ) || msg.contains(
                "ServerResponseException",
                ignoreCase = true
            ) || msg.contains("RestException", ignoreCase = true) ->
                "Este correo ya está registrado o invitado."

            else ->
                "Ha ocurrido un error inesperado. Por favor, inténtalo más tarde."
        }
    }

    /**
     * Valida la posición del GPS recibida desde la vista contra las coordenadas
     * de la empresa registradas en Supabase antes de permitir el marcaje.
     */
    fun intentarFichajeConGPS(latCelular: Double, lonCelular: Double) {
        val trabajador = trabajadorActual
        if (trabajador == null) {
            errorMessage = "Error: No se ha cargado el perfil del trabajador en el sistema."
            return
        }

        viewModelScope.launch {
            try {
                // 1. Descargamos dinámicamente los datos de la empresa de este trabajador desde Supabase
                val datosEmpresa = fichajeRepository.obtenerEmpresaPorId(trabajador.empresaId)

                val latOficina = datosEmpresa.latitud
                val lonOficina = datosEmpresa.longitud

                // Control preventivo por si el jefe no rellenó bien la calle o falló el Geocoder al crearla
                if (latOficina == null || lonOficina == null) {
                    errorMessage =
                        "Error corporativo: Tu empresa no tiene coordenadas de geovalla configuradas."
                    return@launch
                }

                // 2. Medimos la distancia real en metros usando la curvatura de la Tierra
                val resultadoDistancia = FloatArray(1)
                Location.distanceBetween(
                    latCelular, lonCelular,
                    latOficina, lonOficina,
                    resultadoDistancia
                )

                val distanciaMetros = resultadoDistancia[0]

                if (distanciaMetros <= 200f) {
                    errorMessage = null

                    alternarFichaje(latCelular, lonCelular)
                } else {
                    errorMessage =
                        "Estás a ${distanciaMetros.toInt()}m de la oficina. Debes estar a menos de 200m para poder fichar."

                    kotlinx.coroutines.delay(5000)
                    errorMessage = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Error de red al comprobar la geolocalización de la empresa."
            }
        }
    }
}