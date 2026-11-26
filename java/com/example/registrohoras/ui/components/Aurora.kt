package com.example.registrohoras.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registrohoras.R
import com.example.registrohoras.domain.formatoHoras
import com.example.registrohoras.ui.theme.ColorProyecto
import com.example.registrohoras.ui.theme.Electric
import com.example.registrohoras.ui.theme.Frost
import com.example.registrohoras.ui.theme.Nebula
import com.example.registrohoras.ui.theme.Ultraviolet

// ---------------------------------------------------------------------------------------------
// Biblioteca visual Cyber Aurora. Los componentes reciben datos ya calculados (desde los ViewModels)
// y no contienen lógica de negocio.
// ---------------------------------------------------------------------------------------------

/** Símbolo de Vappnet (vector: nítido a cualquier tamaño). */
@Composable
fun LogoVappnet(tamano: Dp, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_vappnet_symbol),
        contentDescription = "Logo de Vappnet",
        modifier = modifier.size(tamano)
    )
}

/** Símbolo + wordmark en minúsculas, con el contexto del cliente debajo. */
@Composable
fun MarcaVappnet(modifier: Modifier = Modifier, contexto: String? = "DEEPSOURCE / OPERACIONES") {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        LogoVappnet(36.dp)
        Column {
            Text("vappnet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, letterSpacing = (-0.3).sp)
            if (contexto != null) {
                Text(
                    contexto,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

/** Superficie base: tarjeta clara u oscura (según el tema) con borde sutil. */
@Composable
fun AuroraCard(modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) { contenido() }
}

/** Tarjeta protagonista: la cifra es lo primero que se lee; el arte Aurora es solo una marca de agua. */
@Composable
fun TotalHoursCard(totalMinutos: Long, contexto: String, modifier: Modifier = Modifier) {
    // El contador "sube" hasta el valor real cada vez que cambia el total.
    val animado by animateIntAsState(targetValue = totalMinutos.toInt(), animationSpec = tween(900), label = "total")
    Box(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Nebula)
            .border(1.dp, Ultraviolet.copy(alpha = 0.35f), MaterialTheme.shapes.extraLarge)
    ) {
        Image(
            painter = painterResource(R.drawable.aurora_banner),
            contentDescription = null, // decorativa
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterEnd,
            modifier = Modifier.matchParentSize()
        )
        // Velo sobre el arte: garantiza contraste del texto, que queda siempre sobre fondo oscuro.
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        0f to Nebula,
                        0.5f to Nebula.copy(alpha = 0.92f),
                        1f to Nebula.copy(alpha = 0.15f)
                    )
                )
        )
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                "TIEMPO ACUMULADO",
                style = MaterialTheme.typography.labelMedium,
                color = Frost.copy(alpha = 0.8f),
                letterSpacing = 1.sp
            )
            Text(
                formatoHoras(animado.toLong()),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(contexto, style = MaterialTheme.typography.bodySmall, color = Frost.copy(alpha = 0.75f))
        }
    }
}

/** Barra discreta de acumulado relativo. */
@Composable
fun ProjectProgressBar(fraccion: Float, color: Color, modifier: Modifier = Modifier) {
    val progreso by animateFloatAsState(targetValue = fraccion.coerceIn(0f, 1f), animationSpec = tween(600), label = "barra")
    Box(
        modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(progreso)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

/** Proyecto: color identificador, nombre, tiempo acumulado y progreso relativo. */
@Composable
fun ProjectCard(nombre: String, minutos: Long, fraccion: Float, color: ColorProyecto, modifier: Modifier = Modifier) {
    val animado by animateIntAsState(targetValue = minutos.toInt(), animationSpec = tween(600), label = "horas_proyecto")
    AuroraCard(modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.fondo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Folder, contentDescription = null, tint = color.icono, modifier = Modifier.size(20.dp))
                }
                Text(
                    nombre,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(formatoHoras(animado.toLong()), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            ProjectProgressBar(fraccion, color.fondo)
        }
    }
}

/** Botón de acción principal (Electric con texto blanco, legible en modo claro y oscuro). */
@Composable
fun AuroraButton(
    texto: String,
    alClic: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    cargando: Boolean = false,
    habilitado: Boolean = true
) {
    Button(
        onClick = alClic,
        enabled = habilitado && !cargando,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = Electric,
            contentColor = Color.White,
            disabledContainerColor = Electric.copy(alpha = 0.55f),
            disabledContentColor = Color.White.copy(alpha = 0.85f)
        )
    ) {
        if (cargando) {
            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
        } else {
            if (icono != null) {
                Icon(icono, contentDescription = null)
                Spacer(Modifier.width(8.dp))
            }
            Text(texto, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Estado vacío: ilustración ligera (imagen estática) + mensaje + acción sugerida. */
@Composable
fun EmptyState(
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
    textoAccion: String? = null,
    alAccion: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.aurora_orb),
            contentDescription = null, // decorativa
            modifier = Modifier.size(168.dp)
        )
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (textoAccion != null && alAccion != null) {
            Spacer(Modifier.height(8.dp))
            AuroraButton(textoAccion, alAccion, Modifier.fillMaxWidth(0.8f))
        }
    }
}
