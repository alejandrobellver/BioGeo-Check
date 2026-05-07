package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Trabajador(
    val id: String, // El UUID del trabajador

    @SerialName("empresa_id")
    val empresaId: String, // El enlace con la tabla Empresa

    @SerialName("nombre_completo")
    val nombreCompleto: String?,

    @SerialName("rol_trabajador")
    val rolTrabajador: String, // "admin" o "empleado"

    @SerialName("email")
    val email: String
)
