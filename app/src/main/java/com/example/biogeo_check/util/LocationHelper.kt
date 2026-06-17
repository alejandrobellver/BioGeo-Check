package com.example.biogeo_check.util

import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationHelper {

    /**
     * 📍 FUNCIÓN 1: Obtiene la ubicación en tiempo real mediante el sensor GPS del móvil.
     * Ideal para cuando el trabajador pulsa el botón de "Fichar".
     */
    fun obtenerUbicacionActual(
        context: Context,
        onSuccess: (Location) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            val request = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            fusedLocationClient.getCurrentLocation(request, null)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        onSuccess(location)
                    } else {
                        onError("No se pudo obtener una localización precisa. Reintenta o activa el GPS.")
                    }
                }
                .addOnFailureListener {
                    onError("Error de hardware al inicializar el sensor GPS.")
                }
        } catch (e: SecurityException) {
            onError("La aplicación no cuenta con permisos de ubicación (GPS).")
        } catch (e: Exception) {
            onError("Error inesperado en el sensor: ${e.message}")
        }
    }

    /**
     * 🗺️ FUNCIÓN 2: Convierte una dirección de texto en coordenadas geográficas (Lat/Long).
     * Ideal para el registro de empresas. Devuelve null de forma segura si no es válida.
     */
    fun obtenerCoordenadasDesdeDireccion(
        context: Context,
        direccionCompleta: String
    ): Pair<Double, Double>? {
        val geocoder = Geocoder(context, java.util.Locale.getDefault())
        return try {
            val direcciones = geocoder.getFromLocationName(direccionCompleta, 1)
            if (!direcciones.isNullOrEmpty()) {
                val resultado = direcciones[0]
                Pair(resultado.latitude, resultado.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}