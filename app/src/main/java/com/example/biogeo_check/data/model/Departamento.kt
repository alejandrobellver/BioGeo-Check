package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Departamento(
    @SerialName("departamento_id") val departamentoId: String = "",
    @SerialName("empresa_id") val empresaId: String,
    @SerialName("nombre_departamento") val nombreDepartamento: String,
    @SerialName("hora_entrada") val horaEntrada: String,
    @SerialName("hora_salida") val horaSalida: String,
    @SerialName("ubicacion_departamento") val ubicacionDepartamento: String? = null,
    @SerialName("turno") val turno: String? = null
)