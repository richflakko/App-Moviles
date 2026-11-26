package com.example.registrohoras.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registrohoras.data.local.RegistroEntity
import com.example.registrohoras.domain.etiquetaFecha
import com.example.registrohoras.domain.formatoHoras
import com.example.registrohoras.domain.nombreProyecto
import com.example.registrohoras.domain.nombreUsuario
import com.example.registrohoras.ui.components.EmptyState
import com.example.registrohoras.ui.components.RegistroCard
import com.example.registrohoras.viewmodel.HorasViewModel

@Composable
fun HistorialScreen(
    horasVm: HorasViewModel,
    alRegistrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val registros by horasVm.registros.collectAsStateWithLifecycle()
    val proyectos by horasVm.proyectos.collectAsStateWithLifecycle()
    val usuarios by horasVm.usuarios.collectAsStateWithLifecycle()
    var porEliminar by remember { mutableStateOf<RegistroEntity?>(null) }
    var busqueda by remember { mutableStateOf("") }

    Column(modifier.fillMaxSize()) {
        Column(
            Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Historial", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Todos los registros guardados en este dispositivo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val lista = registros
        when {
            lista == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            lista.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(
                    titulo = "Todavía no hay actividad",
                    mensaje = "Tu primer registro aparecerá aquí.",
                    textoAccion = "Registrar horas",
                    alAccion = alRegistrar
                )
            }
            else -> {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                    placeholder = { Text("Buscar por tarea, proyecto o usuario") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { busqueda = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    shape = MaterialTheme.shapes.medium
                )

                val consulta = busqueda.trim()
                val filtrada = if (consulta.isEmpty()) lista else lista.filter { registro ->
                    registro.tarea.contains(consulta, ignoreCase = true) ||
                        proyectos.nombreProyecto(registro.proyectoId).contains(consulta, ignoreCase = true) ||
                        usuarios.nombreUsuario(registro.usuarioId).contains(consulta, ignoreCase = true)
                }

                if (filtrada.isEmpty()) {
                    EmptyState(
                        titulo = "Sin resultados",
                        mensaje = "No encontramos registros para «$consulta».",
                        modifier = Modifier.padding(top = 24.dp)
                    )
                } else {
                    // Los registros recientes aparecen primero, agrupados por día.
                    val grupos = filtrada.groupBy { it.fecha }.toSortedMap(compareByDescending<String> { it })
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        grupos.forEach { (fecha, delDia) ->
                            item(key = "fecha_$fecha") {
                                Text(
                                    etiquetaFecha(fecha),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            items(delDia, key = { it.id }) { registro ->
                                RegistroCard(
                                    registro = registro,
                                    proyectoNombre = proyectos.nombreProyecto(registro.proyectoId),
                                    usuarioNombre = usuarios.nombreUsuario(registro.usuarioId),
                                    modifier = Modifier.animateItem(),
                                    alEliminar = { porEliminar = registro }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    porEliminar?.let { registro ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            title = { Text("¿Eliminar registro?") },
            text = {
                Text("Se eliminará \"${registro.tarea}\" (${formatoHoras(registro.minutos.toLong())}). Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    horasVm.eliminar(registro)
                    porEliminar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { porEliminar = null }) { Text("Cancelar") } }
        )
    }
}
