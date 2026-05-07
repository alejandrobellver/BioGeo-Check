package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Fichaje(
    val id: String? = null,

    val momento: String? = null,

    @SerialName("tipo_accion")
    val tipoAccion: String,

    @SerialName("usuario_id")
    val usuarioId: String,

    val latitud: Double?,

    val longitud: Double?
)