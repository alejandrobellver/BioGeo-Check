package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Trabajador(
    @SerialName("trabajador_id") val trabajadorId: String, // Este será el uid de Supabase Auth
    @SerialName("empresa_id") val empresaId: String,
    @SerialName("departamento_id") val departamentoId: String? = null,
    @SerialName("contrato_id") val contratoId: String? = null,
    val nombre: String? = null,
    val apellidos: String? = null,
    val dni: String? = null,
    val email: String,
    val rol: String = "TRABAJADOR" // Puede ser "JEFE" o "TRABAJADOR"
)