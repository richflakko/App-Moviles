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
import com.example.registrohoras.domain.ModoTema

/** Modo claro: Frost de fondo, tarjetas blancas, texto tinta y acentos Electric / Ultraviolet. */
private val EsquemaClaro = lightColorScheme(
    primary = Electric,
    onPrimary = Pure,
    primaryContainer = Color(0xFFE1E7FF),
    onPrimaryContainer = Color(0xFF0E1A5C),
    secondary = Ultraviolet,
    onSecondary = Pure,
    secondaryContainer = Color(0xFFE7DFFF),
    onSecondaryContainer = Color(0xFF2A1C6B),
    tertiary = Color(0xFF0B87AB),
    onTertiary = Pure,
    tertiaryContainer = Color(0xFFD5F6FF),
    onTertiaryContainer = Void,
    background = Frost,
    onBackground = Nebula,
    surface = Pure,
    onSurface = Nebula,
    surfaceVariant = Color(0xFFE9E6F5),
    onSurfaceVariant = Color(0xFF5A5675),
    outline = Color(0xFF8A86A3),
    outlineVariant = Color(0xFFD9D5EA),
    surfaceContainerLowest = Pure,
    surfaceContainerLow = Color(0xFFFAF9FE),
    surfaceContainer = Pure,
    surfaceContainerHigh = Pure,
    surfaceContainerHighest = Color(0xFFF2F0FA),
    error = Color(0xFFC62F3E),
    onError = Pure,
    errorContainer = Color(0xFFFFE3E5),
    onErrorContainer = Color(0xFF5A0A14)
)

/** Modo oscuro: Void de fondo, superficies Nebula, texto claro y acentos Ice / Ultraviolet. */
private val EsquemaOscuro = darkColorScheme(
    primary = Ice,
    onPrimary = Void,
    primaryContainer = Color(0xFF1D2A66),
    onPrimaryContainer = Color(0xFFD7E2FF),
    secondary = Ultraviolet,
    onSecondary = Void,
    secondaryContainer = Color(0xFF30246B),
    onSecondaryContainer = Color(0xFFE7DFFF),
    tertiary = Iridescent,
    onTertiary = Void,
    tertiaryContainer = Color(0xFF4A2060),
    onTertiaryContainer = Color(0xFFF6D9FF),
    background = Void,
    onBackground = Frost,
    surface = Nebula,
    onSurface = Frost,
    surfaceVariant = Color(0xFF2A2347),
    onSurfaceVariant = Color(0xFFB9B5D3),
    outline = Color(0xFF7F7A9F),
    outlineVariant = Color(0xFF3A3360),
    surfaceContainerLowest = Void,
    surfaceContainerLow = Color(0xFF110C26),
    surfaceContainer = Color(0xFF120E26),
    surfaceContainerHigh = Color(0xFF1D1638),
    surfaceContainerHighest = Color(0xFF241C45),
    error = Color(0xFFFF8A94),
    onError = Color(0xFF4A0A12),
    errorContainer = Color(0xFF5A1B25),
    onErrorContainer = Color(0xFFFFDADD)
)

/** Geometría: campos y controles 14 dp, tarjetas 18 dp, tarjeta protagonista 24 dp. */
private val Formas = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun RegistroHorasTheme(modo: ModoTema = ModoTema.SISTEMA, contenido: @Composable () -> Unit) {
    val oscuro = when (modo) {
        ModoTema.SISTEMA -> isSystemInDarkTheme()
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
    }
    MaterialTheme(
        colorScheme = if (oscuro) EsquemaOscuro else EsquemaClaro,
        typography = TipografiaAurora,
        shapes = Formas,
        content = contenido
    )
}
