package com.example.registrohoras.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProveedorSocialTest {
    @Test
    fun cadaProveedor_abreUnaPaginaSegura() {
        ProveedorSocial.entries.forEach { assertTrue(it.urlInicio.startsWith("https://")) }
    }

    @Test
    fun hayTresProveedores_conSuNombre() {
        assertEquals(listOf("Google", "Apple", "Facebook"), ProveedorSocial.entries.map { it.nombre })
    }
}
