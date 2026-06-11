package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Trabajador(
    @SerialName("trabajador_id") val trabajadorId: String,
    @SerialName("empresa_id") val empresaId: String,
    @SerialName("departamento_id") val departamentoId: String? = null,
    @SerialName("contrato_id") val contratoId: String? = null,
    @SerialName("nombre") val nombre: String? = null,
    @SerialName("apellidos") val apellidos: String? = null,
    @SerialName("dni") val dni: String? = null,
    @SerialName("email") val email: String,
    @SerialName("rol") val rol: String = "TRABAJADOR"
)