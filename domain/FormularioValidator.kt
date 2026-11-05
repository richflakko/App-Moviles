package com.example.registrohoras.domain

import java.time.LocalDate

enum class Campo { USUARIO, PROYECTO, FECHA, TAREA, HORAS, MINUTOS }

data class DatosFormulario(
    val usuarioId: String?,
    val proyectoId: String?,
    val fecha: LocalDate?,
    val tarea: String,
    val horas: String,
    val minutos: String
)

/**
 * Reglas de validación del formulario. Es Kotlin puro (sin Android ni Compose):
 * la UI solo muestra los mensajes que devuelve y se puede probar con tests unitarios.
 */
object FormularioValidator {
    const val MAX_HORAS = 24
    const val LARGO_MIN_TAREA = 3
    const val LARGO_MAX_TAREA = 80

    fun validarUsuario(id: String?): String? =
        if (id.isNullOrBlank()) "Selecciona un usuario." else null

    fun validarProyecto(id: String?): String? =
        if (id.isNullOrBlank()) "Selecciona un proyecto." else null

    fun validarFecha(fecha: LocalDate?, hoy: LocalDate = LocalDate.now()): String? = when {
        fecha == null -> "Selecciona la fecha del trabajo."
        fecha.isAfter(hoy) -> "La fecha no puede ser futura."
        else -> null
    }

    fun validarTarea(tarea: String): String? {
        val texto = tarea.trim()
        return when {
            texto.isEmpty() -> "Ingresa el nombre de la tarea."
            texto.length < LARGO_MIN_TAREA -> "Usa al menos $LARGO_MIN_TAREA caracteres."
            texto.length > LARGO_MAX_TAREA -> "Máximo $LARGO_MAX_TAREA caracteres."
            else -> null
        }
    }

    fun validarHoras(horas: String): String? {
        val texto = horas.trim()
        if (texto.isEmpty()) return "Ingresa las horas (0 si no aplica)."
        val n = texto.toIntOrNull() ?: return "Ingresa solo números enteros."
        return when {
            n < 0 -> "Las horas no pueden ser negativas."
            n > MAX_HORAS -> "Máximo $MAX_HORAS horas por registro."
            else -> null
        }
    }

    fun validarMinutos(minutos: String): String? {
        val texto = minutos.trim()
        if (texto.isEmpty()) return "Ingresa los minutos (0 si no aplica)."
        val n = texto.toIntOrNull() ?: return "Ingresa solo números enteros."
        return if (n !in 0..59) "Los minutos deben estar entre 0 y 59." else null
    }

    /** Regla cruzada: la duración total debe ser mayor que cero y no superar un día. */
    fun validarDuracion(horas: String, minutos: String): String? {
        val h = horas.trim().toIntOrNull() ?: return null
        val m = minutos.trim().toIntOrNull() ?: return null
        val total = h * 60 + m
        return when {
            total <= 0 -> "La duración total debe ser mayor que cero."
            total > MAX_HORAS * 60 -> "La duración no puede superar $MAX_HORAS horas."
            else -> null
        }
    }

    /** Devuelve un mensaje por cada campo con error (mapa vacío = formulario válido). */
    fun validar(datos: DatosFormulario, hoy: LocalDate = LocalDate.now()): Map<Campo, String> = buildMap {
        validarUsuario(datos.usuarioId)?.let { put(Campo.USUARIO, it) }
        validarProyecto(datos.proyectoId)?.let { put(Campo.PROYECTO, it) }
        validarFecha(datos.fecha, hoy)?.let { put(Campo.FECHA, it) }
        validarTarea(datos.tarea)?.let { put(Campo.TAREA, it) }
        (validarHoras(datos.horas) ?: validarDuracion(datos.horas, datos.minutos))?.let { put(Campo.HORAS, it) }
        validarMinutos(datos.minutos)?.let { put(Campo.MINUTOS, it) }
    }
}
