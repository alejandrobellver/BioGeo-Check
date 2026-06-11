package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TipoContrato(
    @SerialName("contrato_id") val contratoId: String = "",
    @SerialName("nombre_contrato") val nombreContrato: String,
    @SerialName("horas_semanales") val horasSemanales: Int? = null,
    @SerialName("descanso") val descanso: Int? = null
)