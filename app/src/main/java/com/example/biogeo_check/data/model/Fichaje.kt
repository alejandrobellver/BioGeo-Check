package com.example.biogeo_check.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Fichaje(
    val usuarioId: String,  // de momento lo pongo a mano, más adelante se generará solo.
    val tipoAccion: String, // (es la idea) "ENTRADA", "SALIDA", "INICIO_DESCANSO", "FIN_DESCANSO"
    val latitud: Double? = null,  // Por ahora puede ser nulo hasta que metamos el GPS
    val longitud: Double? = null, // Por ahora puede ser nulo hasta que metamos el GPS
    val email: String
)