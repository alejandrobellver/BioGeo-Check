package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.Empresa
import com.example.biogeo_check.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmpresaRepository {

    // Función para GUARDAR
    suspend fun crearEmpresa(nombreEscrito: String, cifEscrito: String) {
        withContext(Dispatchers.IO) {
            try {
                val miNuevaEmpresa = Empresa(
                    nombreEmpresa = nombreEscrito,
                    cif = cifEscrito
                )

                // Usamos el cliente que tú configuraste
                SupabaseClient.client.postgrest["empresas"].insert(miNuevaEmpresa)
                println("Repositorio: Empresa $nombreEscrito guardada con éxito.")

            } catch (e: Exception) {
                println("Repositorio ERROR al guardar: ${e.message}")
            }
        }
    }

    // Función para LEER
    suspend fun obtenerTodasLasEmpresas(): List<Empresa> {
        return withContext(Dispatchers.IO) {
            try {
                // Descargamos y devolvemos la lista
                val lista = SupabaseClient.client.postgrest["empresas"]
                    .select()
                    .decodeList<Empresa>()

                lista // Retornamos la lista si hay éxito
            } catch (e: Exception) {
                println("Repositorio ERROR al leer: ${e.message}")
                emptyList() // Si hay error, devolvemos una lista vacía para que la app no se cuelgue
            }
        }
    }
}