package com.example.biogeo_check.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TipoContratoTest {

    @Test
    fun `tipo contrato se instancia correctamente con valores nulos opcionales`() {
        val contrato = TipoContrato(
            contratoId = "id_123",
            nombreContrato = "Jornada Completa"
        )

        assertEquals("id_123", contrato.contratoId)
        assertEquals("Jornada Completa", contrato.nombreContrato)
        assertNull("horasSemanales debe ser null por defecto si no se pasa", contrato.horasSemanales)
        assertNull("descanso debe ser null por defecto si no se pasa", contrato.descanso)
    }

    @Test
    fun `tipo contrato guarda las horas y descanso correctamente`() {
        val contratoParcial = TipoContrato(
            nombreContrato = "Media Jornada",
            horasSemanales = 20,
            descanso = 15
        )

        assertEquals(20, contratoParcial.horasSemanales)
        assertEquals(15, contratoParcial.descanso)
        assertEquals("", contratoParcial.contratoId) // ID por defecto es string vacío
    }
}
