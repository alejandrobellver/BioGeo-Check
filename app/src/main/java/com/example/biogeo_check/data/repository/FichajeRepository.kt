package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.Departamento
import com.example.biogeo_check.data.model.Fichaje
import com.example.biogeo_check.data.model.TipoContrato
import com.example.biogeo_check.data.model.Trabajador
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.functions.functions
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FichajeRepository(private val supabase: SupabaseClient) {

    suspend fun obtenerDepartamento(deptoId: String): Departamento? {
        return supabase.postgrest["departamento"]
            .select { filter { eq("departamento_id", deptoId) } }
            .decodeSingleOrNull<Departamento>()
    }

    suspend fun obtenerTipoContrato(contratoId: String): TipoContrato? {
        return supabase.postgrest["tipo_contrato"]
            .select { filter { eq("contrato_id", contratoId) } }
            .decodeSingleOrNull<TipoContrato>()
    }

    suspend fun obtenerTodosLosDepartamentos(): List<Departamento> {
        return supabase.postgrest["departamento"].select().decodeList<Departamento>()
    }

    suspend fun obtenerTodosLosContratos(): List<TipoContrato> {
        return supabase.postgrest["tipo_contrato"]
            .select()
            .decodeList<TipoContrato>()
    }

    suspend fun actualizarTrabajador(trabajadorId: String, nuevoEmail: String, nuevoDeptoId: String?, nuevoContratoId: String?) {
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

    suspend fun obtenerPerfilTrabajador(trabajadorId: String): Trabajador? {
        return supabase.postgrest["trabajador"]
            .select { filter { eq("trabajador_id", trabajadorId) } }
            .decodeSingleOrNull()
    }

    fun obtenerIdUsuarioAutenticado(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }

    suspend fun obtenerUltimoFichaje(trabajadorId: String): Fichaje? {
        return supabase.postgrest["fichaje"]
            .select {
                filter { eq("trabajador_id", trabajadorId) }
                order(column = "hora_fichaje", order = Order.DESCENDING)
                limit(1)
            }.decodeList<Fichaje>().firstOrNull()
    }

    suspend fun registrarFichaje(trabajadorId: String, tipo: String): Fichaje {
        val sdfIso = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
        sdfIso.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val timestampIso = sdfIso.format(java.util.Date())

        val nuevoRegistro = com.example.biogeo_check.data.model.FichajeInsert(
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
        supabase.functions.invoke("invite-employee") {
            contentType(ContentType.Application.Json)
            setBody(invitacion)
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
        
        val sdfIso = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault())
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
}