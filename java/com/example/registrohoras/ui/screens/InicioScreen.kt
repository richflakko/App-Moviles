package com.example.registrohoras.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registrohoras.domain.Perfil
import com.example.registrohoras.domain.nombreProyecto
import com.example.registrohoras.domain.nombreUsuario
import com.example.registrohoras.ui.components.AuroraButton
import com.example.registrohoras.ui.components.AvatarPerfil
import com.example.registrohoras.ui.components.MarcaVappnet
import com.example.registrohoras.ui.components.ProjectCard
import com.example.registrohoras.ui.components.RegistroCard
import com.example.registrohoras.ui.components.TotalHoursCard
import com.example.registrohoras.ui.theme.colorProyecto
import com.example.registrohoras.viewmodel.HorasViewModel
import androidx.compose.material3.TextButton
import com.example.registrohoras.domain.formatoHoras

@Composable
fun InicioScreen(
    horasVm: HorasViewModel,
    alRegistrar: () -> Unit,
    perfil: Perfil?,
    alAbrirPerfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totales by horasVm.totales.collectAsStateWithLifecycle()
    val registros by horasVm.registros.collectAsStateWithLifecycle()
    val proyectos by horasVm.proyectos.collectAsStateWithLifecycle()
    val usuarios by horasVm.usuarios.collectAsStateWithLifecycle()
    val pendientes by horasVm.pendientes.collectAsStateWithLifecycle()
    val sync by horasVm.sync.collectAsStateWithLifecycle()

    // Todo sale de los datos reales (Room): nada de cifras de ejemplo.
    val totalGeneral = proyectos.sumOf { totales[it.id] ?: 0L }
    val maximo = (proyectos.maxOfOrNull { totales[it.id] ?: 0L } ?: 0L).coerceAtLeast(1L)
    val recientes = registros.orEmpty().take(3)
    val saludo = perfil?.let { "¡Hola ${it.nombreParaSaludo}!" } ?: "¡Hola!"

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "marca") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MarcaVappnet(Modifier.weight(1f))
                AvatarPerfil(
                    perfil = perfil,
                    tamano = 44.dp,
                    modifier = Modifier.clickable(onClickLabel = "Abrir perfil", onClick = alAbrirPerfil)
                )
            }
        }
        item(key = "saludo") {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(saludo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Resumen General",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item(key = "total") { TotalHoursCard(totalGeneral, "Todos los usuarios y proyectos") }
        item(key = "sync") {
            // Estado de la conexión con el microservicio (Retrofit): pendientes, total del servidor y botón manual.
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    if (pendientes == 0) "Servidor: todo sincronizado" else "Servidor: $pendientes registro(s) pendientes de enviar",
                    style = MaterialTheme.typography.bodyMedium
                )
                sync.totalServidor?.let {
                    Text(
                        "Total en el servidor: ${formatoHoras(it)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                sync.mensaje?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = horasVm::sincronizar, enabled = !sync.sincronizando) {
                    Text(if (sync.sincronizando) "Sincronizando…" else "Sincronizar ahora")
                }
            }
        }
        item(key = "titulo_proyectos") {
            Text("Horas por proyecto", style = MaterialTheme.typography.titleMedium)
        }
        itemsIndexed(proyectos, key = { _, proyecto -> "proy_${proyecto.id}" }) { indice, proyecto ->
            val minutos = totales[proyecto.id] ?: 0L
            ProjectCard(proyecto.nombre, minutos, minutos.toFloat() / maximo, colorProyecto(indice))
        }
        item(key = "boton") {
            AuroraButton("Registrar horas", alRegistrar, icono = Icons.Filled.Add)
        }
        if (recientes.isNotEmpty()) {
            item(key = "titulo_recientes") {
                Text("Actividad reciente", style = MaterialTheme.typography.titleMedium)
            }
            items(recientes, key = { "reg_${it.id}" }) { registro ->
                RegistroCard(
                    registro = registro,
                    proyectoNombre = proyectos.nombreProyecto(registro.proyectoId),
                    usuarioNombre = usuarios.nombreUsuario(registro.usuarioId)
                )
            }
        }
    }
}
