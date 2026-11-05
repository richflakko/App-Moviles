package com.example.registrohoras.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registrohoras.domain.DatosDemo
import com.example.registrohoras.domain.formatoHoras
import com.example.registrohoras.ui.components.RegistroCard
import com.example.registrohoras.ui.theme.AzulPrimario
import com.example.registrohoras.ui.theme.AzulProfundo
import com.example.registrohoras.viewmodel.HorasViewModel

@Composable
fun ResumenScreen(
    horasVm: HorasViewModel,
    alRegistrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totales by horasVm.totales.collectAsStateWithLifecycle()
    val registros by horasVm.registros.collectAsStateWithLifecycle()

    val proyectos = DatosDemo.proyectos
    val totalGeneral = proyectos.sumOf { totales[it.id] ?: 0L }
    val maximo = proyectos.maxOf { totales[it.id] ?: 0L }.coerceAtLeast(1L)
    val recientes = registros.orEmpty().take(3)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "encabezado") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "DEEPSOURCE / OPERACIONES",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Text(
                    "Registro de horas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Revisa el avance y registra el tiempo dedicado a cada proyecto.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item(key = "total") { TarjetaTotal(totalGeneral) }
        item(key = "titulo_proyectos") {
            Text("Horas por proyecto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        items(proyectos, key = { "proy_${it.id}" }) { proyecto ->
            val minutos = totales[proyecto.id] ?: 0L
            TarjetaProyecto(proyecto.nombre, minutos, minutos.toFloat() / maximo)
        }
        item(key = "boton") {
            Button(
                onClick = alRegistrar,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Registrar horas")
            }
        }
        if (recientes.isNotEmpty()) {
            item(key = "titulo_recientes") {
                Text("Actividad reciente", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            items(recientes, key = { "reg_${it.id}" }) { registro -> RegistroCard(registro) }
        }
    }
}

@Composable
private fun TarjetaTotal(totalMinutos: Long) {
    // El contador "sube" hasta el valor real cada vez que cambia el total.
    val animado by animateIntAsState(targetValue = totalMinutos.toInt(), animationSpec = tween(900), label = "total")
    Box(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Brush.linearGradient(listOf(AzulPrimario, AzulProfundo)))
            .padding(22.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Total acumulado", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.85f))
            }
            Text(
                formatoHoras(animado.toLong()),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Todos los usuarios y proyectos",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun TarjetaProyecto(nombre: String, minutos: Long, fraccion: Float) {
    val progreso by animateFloatAsState(targetValue = fraccion, animationSpec = tween(900), label = "progreso")
    val animado by animateIntAsState(targetValue = minutos.toInt(), animationSpec = tween(900), label = "horas_proyecto")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Text(
                    nombre,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    formatoHoras(animado.toLong()),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
