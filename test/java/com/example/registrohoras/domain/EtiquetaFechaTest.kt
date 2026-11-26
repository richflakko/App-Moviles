package com.example.registrohoras.domain

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class EtiquetaFechaTest {
    private val hoy = LocalDate.of(2026, 11, 23)

    @Test
    fun hoy_ayer_yOtrasFechas() {
        assertEquals("HOY · 23 NOVIEMBRE 2026", etiquetaFecha("2026-11-23", hoy))
        assertEquals("AYER · 22 NOVIEMBRE 2026", etiquetaFecha("2026-11-22", hoy))
        assertEquals("20 NOVIEMBRE 2026", etiquetaFecha("2026-11-20", hoy))
    }

    @Test
    fun fechaInvalida_seDevuelveTalCual() {
        assertEquals("sin-fecha", etiquetaFecha("sin-fecha", hoy))
    }
}
