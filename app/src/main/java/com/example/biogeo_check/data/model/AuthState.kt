package com.example.biogeo_check.data.model

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val trabajador: Trabajador) : AuthState()
    data class Error(val mensaje: String) : AuthState()
}