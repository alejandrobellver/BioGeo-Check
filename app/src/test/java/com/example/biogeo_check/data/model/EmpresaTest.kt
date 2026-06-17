package com.example.biogeo_check.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EmpresaTest {

    @Test
    fun `Empresa se crea con los valores requeridos y por defecto`() {
        val empresa = Empresa(
            empresaId = "emp123",
            nombreEmpresa = "BioGeoCorp",
            cif = "B12345678",
            direccion = "Calle Falsa 123",
            cp = 28001,
            ciudad = "Madrid"
        )

        assertEquals("emp123", empresa.empresaId)
        assertEquals("BioGeoCorp", empresa.nombreEmpresa)
        assertEquals("B12345678", empresa.cif)
        assertEquals("Calle Falsa 123", empresa.direccion)
        assertEquals(28001, empresa.cp)
        assertEquals("Madrid", empresa.ciudad)
        
        // Comprobamos que los parámetros opcionales son nulos si no se pasan
        assertNull(empresa.fechaCreacion)
        assertNull(empresa.latitud)
        assertNull(empresa.longitud)
    }
}
