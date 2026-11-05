package com.example.registrohoras.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val AzulPrimario = Color(0xFF2563EB)
val AzulProfundo = Color(0xFF152B50)
val VerdeExito = Color(0xFF2E9E5B)

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE7FF),
    onPrimaryContainer = Color(0xFF0B1F44),
    secondary = AzulProfundo,
    onSecondary = Color.White,
    background = Color(0xFFF4F7FC),
    onBackground = AzulProfundo,
    surface = Color.White,
    onSurface = AzulProfundo,
    surfaceVariant = Color(0xFFE6ECF7),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFF94A3B8),
    error = Color(0xFFB42323),
    onError = Color.White
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFF9DBBFF),
    onPrimary = Color(0xFF0B1F44),
    primaryContainer = Color(0xFF1E3A6E),
    onPrimaryContainer = Color(0xFFDCE7FF),
    secondary = Color(0xFFBDCCE3),
    onSecondary = Color(0xFF0B1F44),
    background = Color(0xFF0E1A30),
    onBackground = Color(0xFFE6ECF7),
    surface = AzulProfundo,
    onSurface = Color(0xFFE6ECF7),
    surfaceVariant = Color(0xFF22395F),
    onSurfaceVariant = Color(0xFFBDCCE3),
    outline = Color(0xFF6F84A8),
    error = Color(0xFFFF8A8A),
    onError = Color(0xFF4A0A0A)
)

private val Formas = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun RegistroHorasTheme(oscuro: Boolean = isSystemInDarkTheme(), contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (oscuro) EsquemaOscuro else EsquemaClaro,
        shapes = Formas,
        content = contenido
    )
}
