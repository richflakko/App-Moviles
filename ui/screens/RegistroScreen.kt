package com.example.registrohoras.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.registrohoras.domain.Campo
import com.example.registrohoras.domain.Coordenadas
import com.example.registrohoras.domain.DatosDemo
import com.example.registrohoras.ui.components.CampoFecha
import com.example.registrohoras.ui.components.CampoSelector
import com.example.registrohoras.ui.components.CampoTexto
import com.example.registrohoras.ui.theme.VerdeExito
import com.example.registrohoras.util.LocationHelper
import com.example.registrohoras.util.PermisosHelper
import com.example.registrohoras.viewmodel.RegistroViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    snackbar: SnackbarHostState,
    alVerHistorial: () -> Unit,
    modifier: Modifier = Modifier,
    vm: RegistroViewModel = viewModel()
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.mensajeMostrado()
        }
    }

    // ---- Recurso nativo 1: cámara (permiso en tiempo de ejecución + FileProvider) ----
    val tomarFoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        vm.resultadoFoto(exito)
    }
    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) tomarFoto.launch(vm.prepararFoto())
        else vm.mostrarMensaje("Sin permiso de cámara no se puede adjuntar una foto.")
    }

    // ---- Recurso nativo 2: ubicación (GPS) ----
    val buscarUbicacion = {
        vm.buscandoUbicacion()
        LocationHelper.obtener(context) { coordenadas -> vm.resultadoUbicacion(coordenadas) }
    }
    val permisoUbicacion = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
        if (resultado.values.any { it }) buscarUbicacion()
        else vm.mostrarMensaje("Sin permiso de ubicación no se puede adjuntar el lugar de trabajo.")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Nuevo registro", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Registra el tiempo dedicado a cada proyecto.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SeccionCard("Datos del trabajo") {
            CampoSelector(
                seleccionado = estado.usuarioId,
                opciones = DatosDemo.usuarios.map { it.id to it.nombre },
                etiqueta = "Usuario",
                icono = Icons.Filled.Person,
                error = estado.error(Campo.USUARIO),
                valido = estado.esValido(Campo.USUARIO),
                alSeleccionar = vm::onUsuario
            )
            CampoSelector(
                seleccionado = estado.proyectoId,
                opciones = DatosDemo.proyectos.map { it.id to it.nombre },
                etiqueta = "Proyecto",
                icono = Icons.Filled.Folder,
                error = estado.error(Campo.PROYECTO),
                valido = estado.esValido(Campo.PROYECTO),
                alSeleccionar = vm::onProyecto
            )
            CampoFecha(
                fecha = estado.fecha,
                error = estado.error(Campo.FECHA),
                valido = estado.esValido(Campo.FECHA),
                alSeleccionar = vm::onFecha
            )
            CampoTexto(
                valor = estado.tarea,
                alCambiar = vm::onTarea,
                etiqueta = "Nombre de la tarea",
                icono = Icons.Filled.Edit,
                error = estado.error(Campo.TAREA),
                valido = estado.esValido(Campo.TAREA),
                alPerderFoco = { vm.onPerdioFoco(Campo.TAREA) },
                placeholder = "Ej.: Implementar login OAuth",
                capitalizar = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                CampoTexto(
                    valor = estado.horas,
                    alCambiar = vm::onHoras,
                    etiqueta = "Horas",
                    icono = Icons.Filled.Schedule,
                    error = estado.error(Campo.HORAS),
                    valido = estado.esValido(Campo.HORAS),
                    alPerderFoco = { vm.onPerdioFoco(Campo.HORAS) },
                    teclado = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                CampoTexto(
                    valor = estado.minutos,
                    alCambiar = vm::onMinutos,
                    etiqueta = "Minutos",
                    icono = Icons.Filled.Timer,
                    error = estado.error(Campo.MINUTOS),
                    valido = estado.esValido(Campo.MINUTOS),
                    alPerderFoco = { vm.onPerdioFoco(Campo.MINUTOS) },
                    teclado = KeyboardType.Number,
                    accion = ImeAction.Done,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        SeccionCard("Evidencia (opcional)") {
            Text(
                "Adjunta una foto del trabajo y/o el lugar donde lo realizaste.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        if (PermisosHelper.concedido(context, Manifest.permission.CAMERA)) {
                            tomarFoto.launch(vm.prepararFoto())
                        } else {
                            permisoCamara.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tomar foto")
                }
                OutlinedButton(
                    onClick = {
                        if (LocationHelper.tienePermiso(context)) {
                            buscarUbicacion()
                        } else {
                            permisoUbicacion.launch(
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                            )
                        }
                    },
                    enabled = !estado.buscandoUbicacion,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (estado.buscandoUbicacion) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Ubicación")
                }
            }
            estado.fotoPath?.let { ruta -> VistaPreviaFoto(ruta, alQuitar = vm::quitarFoto) }
            estado.ubicacion?.let { coordenadas -> ChipUbicacion(coordenadas, alQuitar = vm::quitarUbicacion) }
        }

        Button(
            onClick = vm::guardar,
            enabled = !estado.guardando,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (estado.guardando) {
                CircularProgressIndicator(
                    Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar registro", style = MaterialTheme.typography.titleSmall)
            }
        }
    }

    estado.guardado?.let { duracion ->
        DialogoGuardado(
            duracion = duracion,
            alContinuar = vm::cerrarGuardado,
            alVerHistorial = {
                vm.cerrarGuardado()
                alVerHistorial()
            }
        )
    }
}

@Composable
private fun SeccionCard(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            contenido()
        }
    }
}

@Composable
private fun VistaPreviaFoto(ruta: String, alQuitar: () -> Unit) {
    val visible = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(visibleState = visible, enter = fadeIn() + expandVertically()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(MaterialTheme.shapes.medium)
        ) {
            AsyncImage(
                model = File(ruta),
                contentDescription = "Foto adjunta al registro",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = alQuitar,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Quitar foto")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChipUbicacion(coordenadas: Coordenadas, alQuitar: () -> Unit) {
    InputChip(
        selected = true,
        onClick = {},
        label = { Text(coordenadas.texto()) },
        leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(InputChipDefaults.IconSize)) },
        trailingIcon = {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Quitar ubicación",
                modifier = Modifier.size(InputChipDefaults.IconSize).clickable(onClick = alQuitar)
            )
        }
    )
}

@Composable
private fun DialogoGuardado(duracion: String, alContinuar: () -> Unit, alVerHistorial: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    // El check "rebota" al aparecer: retroalimentación de que el guardado salió bien.
    val escala by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "check"
    )
    AlertDialog(
        onDismissRequest = alContinuar,
        icon = {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = VerdeExito,
                modifier = Modifier.size(48.dp).scale(escala)
            )
        },
        title = { Text("Registro guardado") },
        text = { Text("Se registraron $duracion. El widget ya muestra el nuevo total.") },
        confirmButton = { TextButton(onClick = alContinuar) { Text("Continuar") } },
        dismissButton = { TextButton(onClick = alVerHistorial) { Text("Ver historial") } }
    )
}
