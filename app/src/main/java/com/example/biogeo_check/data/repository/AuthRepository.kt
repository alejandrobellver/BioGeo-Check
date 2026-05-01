package com.example.biogeo_check.data.repository

import com.example.biogeo_check.data.model.ApiResult
import com.example.biogeo_check.data.model.User
import com.example.biogeo_check.data.remote.LoginRequest
import com.example.biogeo_check.data.remote.RegisterRequest
import com.example.biogeo_check.data.remote.RetrofitClient

/**
 * Repositorio de autenticación
 */
class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun login(email: String, password: String): ApiResult<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!.user)
            } else {
                ApiResult.Error("Credenciales incorrectas", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de conexión")
        }
    }

    suspend fun register(name: String, email: String, password: String): ApiResult<User> {
        return try {
            val response = api.register(RegisterRequest(name, email, password))
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Error en el registro", response.code())
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Error de conexión")
        }
    }
}
