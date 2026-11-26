package com.example.registrohoras.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ---- Paleta Cyber Aurora (ver brief de identidad) ----
val Void = Color(0xFF090817)          // fondo oscuro principal
val Nebula = Color(0xFF17102F)        // superficie oscura / tarjetas
val Electric = Color(0xFF345CFF)      // acción primaria
val Ice = Color(0xFF36D8FF)           // acento cian / actividad
val Ultraviolet = Color(0xFF926BFF)   // acento secundario
val Iridescent = Color(0xFFD578FF)    // reflejo magenta puntual
val Frost = Color(0xFFF2F0FA)         // fondo claro
val Pure = Color(0xFFFFFFFF)          // tarjetas claras

/** Estados semánticos: el degradado nunca los reemplaza. */
val VerdeExito = Color(0xFF2E9E5B)

// ---- Gradientes de identidad (solo para acentos y superficies protagonistas) ----
val AuroraBlue: Brush = Brush.linearGradient(listOf(Electric, Ice))
val DeepNebula: Brush = Brush.horizontalGradient(listOf(Nebula, Ultraviolet))
val LiquidSpectrum: Brush = Brush.linearGradient(listOf(Ice, Iridescent))

// ---- Color identificador de cada proyecto (se asigna por su posición en la lista) ----
data class ColorProyecto(val fondo: Color, val icono: Color)

private val PaletaProyectos = listOf(
    ColorProyecto(Electric, Color.White),
    ColorProyecto(Ultraviolet, Color.White),
    ColorProyecto(Ice, Void),
    ColorProyecto(Iridescent, Void)
)

fun colorProyecto(indice: Int): ColorProyecto = PaletaProyectos[indice.mod(PaletaProyectos.size)]

// Alias de los nombres anteriores, para no romper pantallas que aún los usan.
val AzulPrimario = Electric
val AzulProfundo = Nebula
