package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Invitacion(
    val email: String,
    @SerialName("empresa_id") val empresaId: String,
    val rol: String = "TRABAJADOR",
    @SerialName("departamento_id") val departamentoId: String? = null,
    @SerialName("contrato_id") val contratoId: String? = null
)
