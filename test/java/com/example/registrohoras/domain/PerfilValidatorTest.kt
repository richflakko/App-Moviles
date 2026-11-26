package com.example.registrohoras.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PerfilValidatorTest {
    private val otros = listOf("Luis Gómez")

    @Test
    fun nombreCompleto_validaLargoYDuplicados() {
        assertNotNull(PerfilValidator.validarNombreCompleto("  ", otros))
        assertNotNull(PerfilValidator.validarNombreCompleto("A", otros))
        assertNotNull(PerfilValidator.validarNombreCompleto("x".repeat(41), otros))
        assertNotNull(PerfilValidator.validarNombreCompleto("luis   gómez", otros))
        assertNull(PerfilValidator.validarNombreCompleto("Ana Pérez", otros))
    }

    @Test
    fun nombreMostrar_esOpcional_peroConLimites() {
        assertNull(PerfilValidator.validarNombreMostrar(""))
        assertNull(PerfilValidator.validarNombreMostrar("Ani"))
        assertNotNull(PerfilValidator.validarNombreMostrar("A"))
        assertNotNull(PerfilValidator.validarNombreMostrar("x".repeat(21)))
    }

    @Test
    fun saludo_usaNombreElegido_oPrimerNombre() {
        val sinElegir = Perfil("ana", "Ana Pérez", "ana@vappnet.cl", null, null)
        val elegido = sinElegir.copy(nombreMostrar = "Ani")
        assertEquals("Ana", sinElegir.nombreParaSaludo)
        assertEquals("Ani", elegido.nombreParaSaludo)
    }
}
