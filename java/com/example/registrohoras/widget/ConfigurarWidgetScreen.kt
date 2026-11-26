package com.example.registrohoras.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.registrohoras.domain.ColorWidget
import com.example.registrohoras.domain.formatoHoras
import com.example.registrohoras.ui.components.AuroraButton
import com.example.registrohoras.ui.components.LogoVappnet

/** Elegir el color del widget con vista previa en vivo; los textos se ajustan solos para que siempre se lean. */
@Composable
fun ConfigurarWidgetScreen(
    colorInicial: Int,
    datos: DatosWidget?,
    alGuardar: (Int) -> Unit,
    alCancelar: () -> Unit
) {
    var elegido by remember { mutableIntStateOf(colorInicial) }
    var tono by remember { mutableFloatStateOf(250f) }
    var luminosidad by remember { mutableFloatStateOf(0.3f) }
    val colores = remember(elegido) { ColorWidget.aplicar(elegido) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Personalizar widget", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Elige el color de fondo. Los textos y botones se ajustan solos para que siempre se lean bien.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        VistaPreviaWidget(colores, datos)

        Text("Colores sugeridos", style = MaterialTheme.typography.titleMedium)
        ColorWidget.PALETA.chunked(4).forEach { fila ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                fila.forEach { (nombre, color) ->
                    Muestra(nombre, color, seleccionada = elegido == color) { elegido = color }
                }
            }
        }

        Text("Color personalizado", style = MaterialTheme.typography.titleMedium)
        Text("Tono", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Slider(
            value = tono,
            onValueChange = {
                tono = it
                elegido = ColorWidget.desdeHsl(tono.toDouble(), 0.7, luminosidad.toDouble())
            },
            valueRange = 0f..360f
        )
        Text("Luminosidad", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Slider(
            value = luminosidad,
            onValueChange = {
                luminosidad = it
                elegido = ColorWidget.desdeHsl(tono.toDouble(), 0.7, luminosidad.toDouble())
            },
            valueRange = 0f..1f
        )

        if (colores.ajustado) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    "Ajustamos el color elegido para que los datos se vean bien.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AuroraButton(texto = "Guardar", alClic = { alGuardar(colores.fondo) })
        TextButton(onClick = alCancelar, modifier = Modifier.fillMaxWidth()) { Text("Cancelar") }
    }
}

@Composable
private fun Muestra(nombre: String, color: Int, seleccionada: Boolean, alClic: () -> Unit) {
    val borde = if (seleccionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color(color))
            .border(if (seleccionada) 3.dp else 1.dp, borde, CircleShape)
            .clickable(onClickLabel = "Elegir $nombre", onClick = alClic)
            .semantics { contentDescription = nombre },
        contentAlignment = Alignment.Center
    ) {
        if (seleccionada) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = Color(ColorWidget.aplicar(color).texto))
        }
    }
}

/** Réplica del widget compacto con los colores ya ajustados. */
@Composable
private fun VistaPreviaWidget(c: ColorWidget.Colores, datos: DatosWidget?) {
    val forma = RoundedCornerShape(24.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(forma)
            .background(Color(c.fondo))
            .border(1.dp, Color(c.borde), forma)
            .padding(16.dp)
    ) {
        Row {
            Column(Modifier.weight(1.2f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LogoVappnet(24.dp)
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text("vappnet", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(c.texto))
                        Text("MIS PROYECTOS", fontSize = 8.sp, color = Color(c.textoSecundario))
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text("TIEMPO ACUMULADO", fontSize = 9.sp, color = Color(c.textoSecundario))
                Text(
                    formatoHoras(datos?.total ?: 0L),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(c.texto),
                    maxLines = 1
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Box(
                    Modifier
                        .size(36.dp)
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(c.boton)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(c.signoBoton))
                }
                Spacer(Modifier.height(6.dp))
                Box(Modifier.fillMaxWidth().height(1.dp).background(Color(c.borde)))
                datos?.proyectos?.take(2)?.forEach { fila ->
                    Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(Color(fila.color)))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            fila.nombre,
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            color = Color(c.texto),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(horasCortas(fila.minutos), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(c.texto))
                    }
                }
            }
        }
    }
}
