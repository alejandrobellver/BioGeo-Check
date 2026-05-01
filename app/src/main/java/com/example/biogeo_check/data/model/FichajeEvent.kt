package com.example.biogeo_check.data.model

data class FichajeEvent(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val type: FichajeType = FichajeType.ENTRADA,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val sedeName: String = ""
)

enum class FichajeType {
    ENTRADA,
    SALIDA
}
