package com.example.registrohoras.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FormularioValidatorTest {
    private val hoy = LocalDate.of(2026, 10, 1)

    private fun datosValidos() = DatosFormulario(
        usuarioId = "ana",
        proyectoId = "afc",
        fecha = hoy,
        tarea = "Implementar login OAuth",
        horas = "1",
        minutos = "30"
    )

    @Test
    fun formularioValido_noTieneErrores() {
        assertTrue(FormularioValidator.validar(datosValidos(), hoy).isEmpty())
    }

    @Test
    fun formularioVacio_marcaUsuarioProyectoYTarea() {
        val errores = FormularioValidator.validar(
            DatosFormulario(null, null, hoy, "", "0", "0"), hoy
        )
        assertNotNull(errores[Campo.USUARIO])
        assertNotNull(errores[Campo.PROYECTO])
        assertNotNull(errores[Campo.TAREA])
        assertNotNull(errores[Campo.HORAS]) // duración 0 h 0 min
    }

    @Test
    fun minutos_fueraDeRango_dan_error() {
        assertNull(FormularioValidator.validarMinutos("0"))
        assertNull(FormularioValidator.validarMinutos("59"))
        assertNotNull(FormularioValidator.validarMinutos("60"))
        assertNotNull(FormularioValidator.validarMinutos("-1"))
        assertNotNull(FormularioValidator.validarMinutos(""))
        assertNotNull(FormularioValidator.validarMinutos("abc"))
    }

    @Test
    fun horas_conLimiteDeUnDia() {
        assertNull(FormularioValidator.validarHoras("0"))
        assertNull(FormularioValidator.validarHoras("24"))
        assertNotNull(FormularioValidator.validarHoras("25"))
        assertNotNull(FormularioValidator.validarHoras(""))
    }

    @Test
    fun duracion_debeSerMayorQueCero_yNoSuperarUnDia() {
        assertNotNull(FormularioValidator.validarDuracion("0", "0"))
        assertNull(FormularioValidator.validarDuracion("0", "1"))
        assertNull(FormularioValidator.validarDuracion("24", "0"))
        assertNotNull(FormularioValidator.validarDuracion("24", "1"))
    }

    @Test
    fun tarea_validaLargo() {
        assertNotNull(FormularioValidator.validarTarea("   "))
        assertNotNull(FormularioValidator.validarTarea("ab"))
        assertNull(FormularioValidator.validarTarea("Login"))
        assertNotNull(FormularioValidator.validarTarea("x".repeat(81)))
    }

    @Test
    fun fecha_noPuedeSerFutura() {
        assertNull(FormularioValidator.validarFecha(hoy, hoy))
        assertNull(FormularioValidator.validarFecha(hoy.minusDays(3), hoy))
        assertEquals("La fecha no puede ser futura.", FormularioValidator.validarFecha(hoy.plusDays(1), hoy))
        assertNotNull(FormularioValidator.validarFecha(null, hoy))
    }
}
