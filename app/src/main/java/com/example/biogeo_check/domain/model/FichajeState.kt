package com.example.biogeo_check.domain.model

/**
 * Estado del fichaje del usuario
 */
enum class FichajeState {
    SIN_FICHAR,      // No ha fichado hoy
    ENTRADA_FICHADA, // Ha fichado entrada, pendiente de salida
    SALIDA_FICHADA   // Ha fichado entrada y salida
}
