package com.example.biogeo_check.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Incidencia(
    @SerialName("incidencia_id") val incidenciaId: String = "",
    @SerialName("trabajador_id") val trabajadorId: String,
    val fecha: String? = null,
    val tipo: String,
    val descripcion: String,
    val estado: String = "PENDIENTE",
    val origen: String
)