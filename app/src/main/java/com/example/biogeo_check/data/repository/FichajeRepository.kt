package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.Departamento
import com.example.biogeo_check.data.model.Empresa
import com.example.biogeo_check.data.model.Fichaje
import com.example.biogeo_check.data.model.TipoContrato
import com.example.biogeo_check.data.model.Trabajador
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.statement.bodyAsText

class FichajeRepository(private val supabase: SupabaseClient) {

    // =============================================================================================
    // AUTENTICACIÓN
    // =============================================================================================

    /**
     * Recupera el identificador único (UUID) del usuario actualmente autenticado en Supabase Auth.
     *
     * @return El ID del usuario en formato String, o null si no hay ninguna sesión activa en la app.
     */
    fun obtenerIdUsuarioAutenticado(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }

    // =============================================================================================
    // TRABAJADORES
    // =============================================================================================

    /**
     * Consulta y extrae el perfil completo de un trabajador de la tabla "trabajador" en base a su ID.
     *
     * Este metodo realiza una petición select filtrando por la clave primaria para obtener la fila
     * correspondiente al empleado. Permite acceder a toda su información vinculada, como su rol de
     * permisos dentro del sistema (JEFE/EMPLEADO) o el identificador de la empresa a la que pertenece.
     *
     * @param trabajadorId El identificador único del trabajador que se desea consultar.
     * @return Un objeto [Trabajador] con los datos mapeados desde la base de datos, o null si el registro no existe.
     */
    suspend fun obtenerPerfilTrabajador(trabajadorId: String): Trabajador? {
        return supabase.postgrest["trabajador"]
            .select { filter { eq("trabajador_id", trabajadorId) } }
            .decodeSingleOrNull<Trabajador>()
    }

    /**
     * Obtiene la lista completa de todos los trabajadores registrados en el sistema.
     *
     * @return Una lista [List] que contiene a todos los objetos [Trabajador] de la base de datos.
     */
    suspend fun obtenerTodosLosTrabajadores(): List<Trabajador> {
        return supabase.postgrest["trabajador"]
            .select()
            .decodeList<Trabajador>()
    }

    /**
     * Actualiza la información crítica de contacto y asignación estructural de un trabajador.
     *
     * @param trabajadorId El identificador único del trabajador objetivo.
     * @param nuevoEmail La nueva dirección de correo electrónico que se va a guardar.
     * @param nuevoDeptoId El identificador del nuevo departamento asignado (puede ser null).
     * @param nuevoContratoId El identificador del nuevo tipo de contrato asignado (puede ser null).
     */
    suspend fun actualizarTrabajador(
        trabajadorId: String,
        nuevoEmail: String,
        nuevoDeptoId: String?,
        nuevoContratoId: String?
    ) {
        supabase.postgrest["trabajador"].update(
            {
                set("email", nuevoEmail)
                set("departamento_id", nuevoDeptoId)
                set("contrato_id", nuevoContratoId)
            }
        ) {
            filter { eq("trabajador_id", trabajadorId) }
        }
    }

    /**
     * Modifica exclusivamente la relación de departamento de un trabajador específico.
     *
     * Permite asociar a un empleado a una nueva área o desvincularlo por completo pasándole un valor nulo.
     *
     * @param trabajadorId El identificador único del trabajador que se va a actualizar.
     * @param departamentoId El UUID del departamento destino, o null para dejar al empleado sin departamento asignado.
     */
    suspend fun actualizarDepartamentoDeTrabajador(trabajadorId: String, departamentoId: String?) {
        supabase.postgrest["trabajador"]
            .update(mapOf("departamento_id" to departamentoId)) {
                filter {
                    eq("trabajador_id", trabajadorId)
                }
            }
    }

    // =============================================================================================
    // DEPARTAMENTOS
    // =============================================================================================

    /**
     * Obtiene los detalles de un departamento específico utilizando su identificador.
     *
     * @param deptoId El identificador único del departamento que se desea buscar.
     * @return El objeto [Departamento] correspondiente, o null si no se encuentra en la tabla.
     */
    suspend fun obtenerDepartamento(deptoId: String): Departamento? {
        return supabase.postgrest["departamento"]
            .select { filter { eq("departamento_id", deptoId) } }
            .decodeSingleOrNull<Departamento>()
    }

    /**
     * Recupera el catálogo completo de departamentos registrados en la base de datos de la empresa.
     *
     * @return Una lista [List] con todos los objetos [Departamento] existentes.
     */
    suspend fun obtenerTodosLosDepartamentos(): List<Departamento> {
        return supabase.postgrest["departamento"].select().decodeList<Departamento>()
    }

    /**
     * Registra un nuevo departamento en el sistema vinculándolo automáticamente a la corporación correspondiente.
     *
     * El metodo resuelve de forma dinámica la identidad de la empresa basándose en la sesión del usuario
     * que realiza la operación, omite el identificador para delegar la generación del UUID al motor de la
     * base de datos y retorna el registro definitivo creado en el servidor.
     *
     * @param departamento Un objeto temporal de tipo [Departamento] que contiene los parámetros rellenados en el formulario de la interfaz.
     * @return El objeto [Departamento] definitivo, el cual ya incorpora el UUID asignado por el servidor.
     * @throws Exception Si no se detecta una sesión de autenticación activa o si el usuario no tiene una empresa asignada.
     */
    suspend fun insertarDepartamento(departamento: Departamento): Departamento {
        val userId = obtenerIdUsuarioAutenticado()
            ?: throw Exception("Error: No hay sesión activa.")

        val trabajador = obtenerPerfilTrabajador(userId)
        val uuidEmpresaDinamico = trabajador?.empresaId
            ?: throw Exception("Error: Este trabajador no tiene empresa asignada.")

        val registroFinal = Departamento(
            departamentoId = null,
            empresaId = uuidEmpresaDinamico,
            nombreDepartamento = departamento.nombreDepartamento,
            horaEntrada = departamento.horaEntrada,
            horaSalida = departamento.horaSalida,
            ubicacionDepartamento = departamento.ubicacionDepartamento,
            turno = departamento.turno
        )

        return supabase.postgrest["departamento"].insert(registroFinal) {
            select()
        }.decodeSingle<Departamento>()
    }

    // =============================================================================================
    // CONTRATOS
    // =============================================================================================

    /**
     * Obtiene la configuración de un tipo de contrato específico filtrando por su identificador.
     *
     * @param contratoId El identificador único del contrato a consultar.
     * @return El objeto [TipoContrato] asociado, o null si no se localiza el registro.
     */
    suspend fun obtenerTipoContrato(contratoId: String): TipoContrato? {
        return supabase.postgrest["tipo_contrato"]
            .select { filter { eq("contrato_id", contratoId) } }
            .decodeSingleOrNull<TipoContrato>()
    }

    /**
     * Recupera todos los tipos de contratos disponibles en el sistema.
     *
     * @return Una lista [List] con todos los objetos de tipo [TipoContrato].
     */
    suspend fun obtenerTodosLosContratos(): List<TipoContrato> {
        return supabase.postgrest["tipo_contrato"]
            .select()
            .decodeList<TipoContrato>()
    }

    // =============================================================================================
    // FICHAJES
    // =============================================================================================

    /**
     * Realiza una consulta para encontrar el último fichaje cronológico realizado por un empleado.
     *
     * @param trabajadorId El identificador único del trabajador del cual se quiere conocer el estado.
     * @return El objeto [Fichaje] más reciente en el tiempo, o null si el empleado nunca ha registrado una acción.
     */
    suspend fun obtenerUltimoFichaje(trabajadorId: String): Fichaje? {
        return supabase.postgrest["fichaje"]
            .select {
                filter { eq("trabajador_id", trabajadorId) }
                order(column = "hora_fichaje", order = Order.DESCENDING)
                limit(1)
            }.decodeList<Fichaje>().firstOrNull()
    }

    /**
     * Registra un nuevo evento de fichaje en el sistema (Entrada, Salida, etc.).
     *
     * El metodo formatea la marca de tiempo actual bajo el estándar ISO UTC, adjunta las coordenadas de geolocalización
     * fijas y guarda la operación, retornando la estructura final confirmada por el servidor de Supabase.
     *
     * @param trabajadorId El identificador del empleado que ejecuta la acción.
     * @param tipo La etiqueta de la acción realizada (por ejemplo, "ENTRADA" o "SALIDA").
     * @return El objeto [Fichaje] completo persistido en la base de datos.
     */
    suspend fun registrarFichaje(trabajadorId: String, tipo: String): Fichaje {
        val sdfIso =
            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
        sdfIso.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val timestampIso = sdfIso.format(java.util.Date())

        val nuevoRegistro = Fichaje(
            trabajadorId = trabajadorId,
            horaFichaje = timestampIso,
            tipoAccion = tipo,
            latitud = 40.416775,
            longitud = -3.703790
        )
        return supabase.postgrest["fichaje"].insert(nuevoRegistro) {
            select()
        }.decodeSingle<Fichaje>()
    }

    suspend fun crearInvitacion(invitacion: com.example.biogeo_check.data.model.Invitacion) {
        val response = supabase.functions.invoke("invite-employee") {
            contentType(ContentType.Application.Json)
            setBody(invitacion)
        }
        if (response.status.value !in 200..299) {
            val errorBody = try { response.bodyAsText() } catch(e:Exception) { "Error desconocido" }
            throw Exception("Error del servidor: ${response.status.value} - $errorBody")
        }
    }

    suspend fun obtenerTrabajadoresPorEmpresa(empresaId: String): List<Trabajador> {
        return supabase.postgrest["trabajador"]
            .select { filter { eq("empresa_id", empresaId) } }
            .decodeList<Trabajador>()
    }

    suspend fun obtenerFichajesDeTrabajadoresHoy(trabajadorIds: List<String>): List<Fichaje> {
        if (trabajadorIds.isEmpty()) return emptyList()

        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)

        val sdfIso =
            java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
        sdfIso.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val startOfDayUtc = sdfIso.format(cal.time)

        return supabase.postgrest["fichaje"]
            .select {
                filter {
                    isIn("trabajador_id", trabajadorIds)
                    gte("hora_fichaje", startOfDayUtc)
                }
                order(column = "hora_fichaje", order = Order.ASCENDING)
            }
            .decodeList<Fichaje>()
    }

    /**
     * Recupera los datos de una empresa de Supabase por su ID único.
     */
    suspend fun obtenerEmpresaPorId(empresaId: String): Empresa {
        return supabase.postgrest["empresa"]
            .select { filter { eq("id", empresaId) } }
            .decodeSingle<Empresa>()
    }
}