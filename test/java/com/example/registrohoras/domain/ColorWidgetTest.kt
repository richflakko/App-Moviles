package com.example.registrohoras.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorWidgetTest {
    private val negro = 0xFF000000.toInt()
    private val blanco = 0xFFFFFFFF.toInt()

    @Test
    fun negroPuro_seAdapta_yElTextoSeLee() {
        val colores = ColorWidget.aplicar(negro)
        assertTrue(colores.ajustado)
        assertNotEquals(negro, colores.fondo)
        assertTrue(ColorWidget.contraste(colores.fondo, colores.texto) >= ColorWidget.CONTRASTE_MINIMO)
    }

    @Test
    fun blancoPuro_seAdapta_yElTextoSeLee() {
        val colores = ColorWidget.aplicar(blanco)
        assertTrue(colores.ajustado)
        assertNotEquals(blanco, colores.fondo)
        assertTrue(ColorWidget.contraste(colores.fondo, colores.texto) >= ColorWidget.CONTRASTE_MINIMO)
    }

    @Test
    fun grisMedio_seAjustaHastaCumplirElContraste() {
        val colores = ColorWidget.aplicar(0xFF808080.toInt())
        assertTrue(ColorWidget.contraste(colores.fondo, colores.texto) >= ColorWidget.CONTRASTE_MINIMO)
    }

    @Test
    fun colorDeLaPaleta_noSeAltera() {
        val nebula = ColorWidget.PALETA.first().second
        val colores = ColorWidget.aplicar(nebula)
        assertFalse(colores.ajustado)
        assertEquals(nebula, colores.fondo)
    }

    @Test
    fun cualquierColorElegido_terminaConTextoLegible() {
        for (tono in 0 until 360 step 15) {
            for (luminosidad in listOf(0.0, 0.1, 0.25, 0.4, 0.5, 0.6, 0.75, 0.9, 1.0)) {
                val elegido = ColorWidget.desdeHsl(tono.toDouble(), 0.7, luminosidad)
                val colores = ColorWidget.aplicar(elegido)
                assertTrue(
                    "tono=$tono lum=$luminosidad",
                    ColorWidget.contraste(colores.fondo, colores.texto) >= ColorWidget.CONTRASTE_MINIMO
                )
            }
        }
    }

    @Test
    fun todaLaPaleta_cumpleElContraste() {
        ColorWidget.PALETA.forEach { (_, color) ->
            val colores = ColorWidget.aplicar(color)
            assertTrue(ColorWidget.contraste(colores.fondo, colores.texto) >= ColorWidget.CONTRASTE_MINIMO)
        }
    }

    @Test
    fun botonMas_seDistingueDelFondo() {
        val electric = ColorWidget.aplicar(0xFF345CFF.toInt())
        assertTrue(ColorWidget.contraste(electric.boton, electric.fondo) >= 2.0)
    }
}
