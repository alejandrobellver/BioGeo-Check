package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.ApiResult
import com.example.biogeo_check.data.model.FichajeEvent
import com.example.biogeo_check.data.remote.FichajeRequest
import com.example.biogeo_check.data.remote.RetrofitClient

/**
 * Repositorio de fichajes
 */
class FichajeRepository {

    private val api = RetrofitClient.apiService

    suspend fun registrarFichaje(
        userId: String,
        type: String,
        latitude: Double,
        longitude: Double
    ): ApiResult<FichajeEvent> {
        return try {
            val response = api.registrarFichaje(
                FichajeRequest(userId, type, latitude, longitude)
            )
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al registrar fichaje", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de conexión")
        }
    }

    suspend fun getFichajesUsuario(userId: String): ApiResult<List<FichajeEvent>> {
        return try {
            val response = api.getFichajesUsuario(userId)
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al obtener fichajes", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de conexión")
        }
    }

    suspend fun getAllFichajes(): ApiResult<List<FichajeEvent>> {
        return try {
            val response = api.getAllFichajes()
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error al obtener fichajes", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de conexión")
        }
    }
}
