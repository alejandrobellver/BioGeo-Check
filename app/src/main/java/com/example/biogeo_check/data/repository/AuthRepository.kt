package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.Trabajador
import com.example.biogeo_check.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    private val client = SupabaseClient.client

    suspend fun obtenerTrabajadorPorEmail(email: String, empresaId: String): Trabajador? {
        return withContext(Dispatchers.IO) {
            try {
                val query = client.postgrest["Trabajadores"]
                    .select {
                        filter {
                            eq("email", email)
                            eq("empresa_id", empresaId)
                        }
                    }

                // LOG 2: Ver el JSON crudo que devuelve Supabase antes de convertirlo a objeto
                val rawData = query.data

                val resultado = query.decodeSingleOrNull<Trabajador>()
                resultado

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}