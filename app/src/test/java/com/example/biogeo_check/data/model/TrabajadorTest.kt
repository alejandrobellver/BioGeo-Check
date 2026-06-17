package com.example.biogeo_check.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TrabajadorTest {

    @Test
    fun `Trabajador se instancia correctamente asumiendo rol TRABAJADOR por defecto`() {
        val trabajador = Trabajador(
            trabajadorId = "t_100",
            empresaId = "e_100",
            email = "empleado@biogeo.com"
        )

        assertEquals("t_100", trabajador.trabajadorId)
        assertEquals("e_100", trabajador.empresaId)
        assertEquals("empleado@biogeo.com", trabajador.email)
        
        // Validamos el valor por defecto crítico
        assertEquals("TRABAJADOR", trabajador.rol)

        // Validamos que los opcionales empiezan en null si no se asignan
        assertNull(trabajador.nombre)
        assertNull(trabajador.apellidos)
        assertNull(trabajador.dni)
        assertNull(trabajador.departamentoId)
        assertNull(trabajador.contratoId)
    }
}
