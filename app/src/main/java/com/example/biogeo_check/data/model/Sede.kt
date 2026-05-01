package com.example.biogeo_check.data.model

data class Sede(
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Double = 100.0 // Radio permitido en metros
)
