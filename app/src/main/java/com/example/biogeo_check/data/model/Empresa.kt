package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Empresa(
    @SerialName("id") val empresaId: String = "",
    @SerialName("nombre_empresa") val nombreEmpresa: String,
    val cif: String,
    @SerialName("fecha_creacion") val fechaCreacion: String? = null,
    val direccion: String
)