package com.example.registrohoras.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClaveHasherTest {
    @Test
    fun mismaClaveYSal_dan_elMismoHash() {
        val sal = ClaveHasher.nuevaSal()
        assertEquals(ClaveHasher.hash("Ana#2026", sal), ClaveHasher.hash("Ana#2026", sal))
    }

    @Test
    fun salesDistintas_dan_hashesDistintos() {
        assertNotEquals(
            ClaveHasher.hash("Ana#2026", ClaveHasher.nuevaSal()),
            ClaveHasher.hash("Ana#2026", ClaveHasher.nuevaSal())
        )
    }

    @Test
    fun verificar_aceptaClaveCorrecta_yRechazaIncorrecta() {
        val sal = ClaveHasher.nuevaSal()
        val hash = ClaveHasher.hash("Ana#2026", sal)
        assertTrue(ClaveHasher.verificar("Ana#2026", sal, hash))
        assertFalse(ClaveHasher.verificar("ana#2026", sal, hash))
    }
}
