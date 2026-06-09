package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Fichaje(
    @SerialName("fichaje_id") val fichajeId: String = "",
    @SerialName("trabajador_id") val trabajadorId: String,
    @SerialName("hora_fichaje") val horaFichaje: String? = null,
    @SerialName("tipo_accion") val tipoAccion: String, // "ENTRADA", "INICIO_DESCANSO", etc.
    val latitud: Double,
    val longitud: Double
)