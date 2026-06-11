package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Invitacion(
// El 'id' se puede omitir si lo genera Supabase automáticamente
    val email: String,
    @SerialName("empresa_id") val empresaId: String, // ¡Necesario!
    val rol: String = "TRABAJADOR"
)
