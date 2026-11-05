package com.example.registrohoras.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Usuario(val id: String, val nombre: String)
data class Proyecto(val id: String, val nombre: String)

data class Coordenadas(val latitud: Double, val longitud: Double) {
    fun texto(): String = String.format(Locale.US, "%.4f, %.4f", latitud, longitud)
}

/** Catálogo de ejemplo (el requisito pide seleccionar usuario, no autenticarse). */
object DatosDemo {
    val usuarios = listOf(Usuario("ana", "Ana Pérez"), Usuario("luis", "Luis Gómez"))
    val proyectos = listOf(
        Proyecto("afc", "Portal Cliente AFC"),
        Proyecto("intranet", "Intranet Deepsource"),
        Proyecto("soporte", "Soporte operaciones")
    )

    fun nombreUsuario(id: String): String = usuarios.firstOrNull { it.id == id }?.nombre ?: "Usuario desconocido"
    fun nombreProyecto(id: String): String = proyectos.firstOrNull { it.id == id }?.nombre ?: "Proyecto desconocido"
}

fun formatoHoras(minutos: Long): String = "${minutos / 60} h ${minutos % 60} min"

private val formatoFecha: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun fechaLegible(iso: String): String =
    runCatching { LocalDate.parse(iso).format(formatoFecha) }.getOrDefault(iso)

fun fechaLegible(fecha: LocalDate): String = fecha.format(formatoFecha)
