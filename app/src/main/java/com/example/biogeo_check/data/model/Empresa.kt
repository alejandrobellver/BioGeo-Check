package com.example.biogeo_check.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Empresa(
    // El ID es opcional al enviar (lo genera la BD), pero obligatorio al recibir
    val id: String? = null,

    @SerialName("nombre_empresa")
    val nombreEmpresa: String,

    val cif: String?, // Puede ser nulo

    @SerialName("fecha_creacion")
    val fechaCreacion: String? = null // La BD pone la fecha automáticamente
)