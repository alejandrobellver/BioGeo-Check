package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.Departamento
import com.example.biogeo_check.data.model.Fichaje
import com.example.biogeo_check.data.model.TipoContrato
import com.example.biogeo_check.data.model.Trabajador
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class FichajeRepository(private val supabase: SupabaseClient) {

    // 🏢 1. Trae un departamento específico por su ID
    suspend fun obtenerDepartamento(deptoId: String): Departamento? {
        return supabase.postgrest["departamento"]
            .select { filter { eq("departamento_id", deptoId) } }
            .decodeSingleOrNull<Departamento>()
    }

    // 📜 2. Trae un tipo de contrato específico por su ID
    suspend fun obtenerTipoContrato(contratoId: String): TipoContrato? {
        return supabase.postgrest["tipo_contrato"]
            .select { filter { eq("contrato_id", contratoId) } }
            .decodeSingleOrNull<TipoContrato>()
    }

    // 🗺️ 3. Trae TODOS los departamentos (para el menú de selección al editar)
    suspend fun obtenerTodosLosDepartamentos(): List<Departamento> {
        return supabase.postgrest["departamento"].select().decodeList<Departamento>()
    }

    // 📜 3b. Trae TODOS los tipos de contrato (para llenar el menú desplegable al editar)
    suspend fun obtenerTodosLosContratos(): List<TipoContrato> {
        return supabase.postgrest["tipo_contrato"]
            .select()
            .decodeList<TipoContrato>()
    }

    // 💾 4. Actualiza el email y el departamento del trabajador en la base de datos
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

    //  En teoria es por si se suspende la app, esto lo que hace es que se queda ese token autentificado
    // hasta que vuelves a abrirla y no tienes que volver a inicar sesion.
    suspend fun obtenerPerfilTrabajador(trabajadorId: String): Trabajador? {
        return supabase.postgrest["trabajador"]
            .select { filter {eq("trabajador_id", trabajadorId) }}
            .decodeSingleOrNull()
    }
    fun obtenerIdUsuarioAutenticado(): String? {
        // Le pide a la librería de Auth de Supabase el ID del usuario actual
        return supabase.auth.currentUserOrNull()?.id
    }

    // Cogemos el ultimo fichaje que sera A para que cuando pulsemos el boton diga B.
    suspend fun obtenerUltimoFichaje(trabajadorId: String): Fichaje? {
        return supabase.postgrest["fichaje"]
            .select {
                filter { eq("trabajador_id", trabajadorId) }
                order(column = "hora_fichaje", order = Order.DESCENDING)
                limit(1)
            }.decodeList<Fichaje>().firstOrNull()
    }

    //Sencillo registramos
    suspend fun registrarFichaje(trabajadorId: String, tipo: String): Fichaje {
        val nuevoRegistro = Fichaje(
            trabajadorId = trabajadorId,
            horaFichaje = java.time.Instant.now().toString(),
            tipoAccion = tipo, // "ENTRADA" o "SALIDA"
            latitud = 40.416775, // De momento las simulamos
            longitud = -3.703790 // De momento las simulamos
        )
        return supabase.postgrest["fichaje"].insert(nuevoRegistro) {
            select()
        }.decodeSingle<Fichaje>()
    }
}