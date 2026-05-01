package com.example.biogeo_check.data.remote

import com.example.biogeo_check.data.model.FichajeEvent
import com.example.biogeo_check.data.model.User
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz Retrofit para comunicación con el backend FastAPI
 */
interface ApiService {

    // === Auth ===
    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    // === Fichajes ===
    @POST("fichajes")
    suspend fun registrarFichaje(@Body event: FichajeRequest): Response<FichajeEvent>

    @GET("fichajes/user/{userId}")
    suspend fun getFichajesUsuario(@Path("userId") userId: String): Response<List<FichajeEvent>>

    @GET("fichajes")
    suspend fun getAllFichajes(): Response<List<FichajeEvent>>

    // === Sedes ===
    @GET("sedes")
    suspend fun getSedes(): Response<List<com.example.biogeo_check.data.model.Sede>>
}

// Request/Response DTOs
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val user: User)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class FichajeRequest(
    val userId: String,
    val type: String,
    val latitude: Double,
    val longitude: Double
)
