package com.example.biogeo_check.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FichajeTest {

    @Test
    fun `Fichaje se instancia correctamente asumiendo nulos opcionales`() {
        val fichaje = Fichaje(
            trabajadorId = "t123",
            tipoAccion = "ENTRADA"
        )

        assertEquals("t123", fichaje.trabajadorId)
        assertEquals("ENTRADA", fichaje.tipoAccion)

        // Verificamos nulos por defecto en datos críticos
        assertNull(fichaje.fichajeId)
        assertNull(fichaje.horaFichaje)
        assertNull(fichaje.latitud)
        assertNull(fichaje.longitud)
    }
}
