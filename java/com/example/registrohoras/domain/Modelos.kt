package com.example.registrohoras.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Usuario(val id: String, val nombre: String)
data class Proyecto(val id: String, val nombre: String)

data class Coordenadas(val latitud: Double, val longitud: Double) {
    fun texto(): String = String.format(Locale.US, "%.4f, %.4f", latitud, longitud)
}

/** Datos del perfil de una persona. */
data class Perfil(
    val id: String,
    val nombre: String,
    val correo: String?,
    val nombreMostrar: String?,
    val fotoPath: String?
) {
    /** Cómo la app llama a la persona: el nombre que eligió o, si no eligió, su primer nombre. */
    val nombreParaSaludo: String
        get() = nombreMostrar?.takeIf { it.isNotBlank() } ?: nombre.trim().substringBefore(' ')
}

/** Credencial de demostración (solo para probar y presentar la app; no usar en producción). */
data class UsuarioDemo(val id: String, val nombre: String, val correo: String, val clave: String)

fun List<Usuario>.nombreUsuario(id: String): String = firstOrNull { it.id == id }?.nombre ?: "Usuario desconocido"
fun List<Proyecto>.nombreProyecto(id: String): String = firstOrNull { it.id == id }?.nombre ?: "Proyecto desconocido"

fun formatoHoras(minutos: Long): String = "${minutos / 60} h ${minutos % 60} min"

private val formatoFecha: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun fechaLegible(iso: String): String =
    runCatching { LocalDate.parse(iso).format(formatoFecha) }.getOrDefault(iso)

fun fechaLegible(fecha: LocalDate): String = fecha.format(formatoFecha)

private val formatoEtiqueta = java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale.forLanguageTag("es"))

/** Encabezado de grupo del historial: "HOY · 23 NOVIEMBRE 2026", "AYER · ..." o solo la fecha. */
fun etiquetaFecha(iso: String, hoy: LocalDate = LocalDate.now()): String {
    val fecha = runCatching { LocalDate.parse(iso) }.getOrNull() ?: return iso
    val texto = fecha.format(formatoEtiqueta).uppercase(java.util.Locale.forLanguageTag("es"))
    return when (fecha) {
        hoy -> "HOY · $texto"
        hoy.minusDays(1) -> "AYER · $texto"
        else -> texto
    }
}
