package com.example.biogeo_check.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailValidatorTest {

    @Test
    fun `email vacio devuelve falso`() {
        val result = EmailValidator.isValid("")
        assertFalse("Un email vacío no debería ser válido", result)
    }

    @Test
    fun `email sin arroba devuelve falso`() {
        val result = EmailValidator.isValid("correofalso.com")
        assertFalse("Un email sin @ no debería ser válido", result)
    }

    @Test
    fun `email con dominio incompleto devuelve falso`() {
        val result = EmailValidator.isValid("usuario@dominio")
        assertFalse("Un email sin extensión de dominio (.com) no debería ser válido", result)
    }

    @Test
    fun `email correcto devuelve verdadero`() {
        val result = EmailValidator.isValid("trabajador@biogeocheck.com")
        assertTrue("Un email bien formado debería ser válido", result)
    }
}
