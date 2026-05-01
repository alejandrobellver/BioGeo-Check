package com.example.biogeo_check.data.model

/**
 * Wrapper para respuestas de la API
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int = -1) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}
