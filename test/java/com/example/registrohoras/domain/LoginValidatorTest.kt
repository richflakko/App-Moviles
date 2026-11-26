package com.example.registrohoras.domain

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class LoginValidatorTest {
    @Test
    fun correo_valido_noTieneError() {
        assertNull(LoginValidator.validarCorreo("ana@vappnet.cl"))
        assertNull(LoginValidator.validarCorreo("  luis.gomez+demo@deepsource.com  "))
    }

    @Test
    fun correo_vacioOMalFormado_dar_error() {
        assertNotNull(LoginValidator.validarCorreo(""))
        assertNotNull(LoginValidator.validarCorreo("ana"))
        assertNotNull(LoginValidator.validarCorreo("ana@vappnet"))
        assertNotNull(LoginValidator.validarCorreo("@vappnet.cl"))
    }

    @Test
    fun clave_conLargoMinimo() {
        assertNotNull(LoginValidator.validarClave(""))
        assertNotNull(LoginValidator.validarClave("12345"))
        assertNull(LoginValidator.validarClave("123456"))
    }
}
