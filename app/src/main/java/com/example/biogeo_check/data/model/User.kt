package com.example.biogeo_check.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "empleado" // "empleado" | "admin"
)
