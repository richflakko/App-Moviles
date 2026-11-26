package com.example.registrohoras.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registrohoras.R
import com.example.registrohoras.domain.ModoTema
import com.example.registrohoras.domain.formatoHoras
import com.example.registrohoras.ui.components.AvatarPerfil
import com.example.registrohoras.ui.components.DialogoEditarPerfil
import com.example.registrohoras.ui.theme.DeepNebula
import com.example.registrohoras.util.PermisosHelper
import com.example.registrohoras.viewmodel.PerfilViewModel
import com.example.registrohoras.viewmodel.TemaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    perfilVm: PerfilViewModel,
    temaVm: TemaViewModel,
    snackbar: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val perfil by perfilVm.perfil.collectAsStateWithLifecycle()
    val estadisticas by perfilVm.estadisticas.collectAsStateWithLifecycle()
    val mensaje by perfilVm.mensaje.collectAsStateWithLifecycle()
    val modoTema by temaVm.modo.collectAsStateWithLifecycle()
    var dialogoTema by remember { mutableStateOf(false) }

    var hojaFoto by remember { mutableStateOf(false) }
    var explicarPermiso by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf(false) }
    var confirmarCierre by remember { mutableStateOf(false) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            perfilVm.mensajeMostrado()
        }
    }

    // ---- Recurso nativo: fotos del dispositivo (permiso en tiempo de ejecución + selector del sistema) ----
    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) perfilVm.cambiarFoto(uri)
    }
    fun abrirSelector() {
        selectorFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    val pedirPermisoFotos = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
        if (resultado.values.any { it }) abrirSelector()
        else perfilVm.mostrarMensaje("Sin permiso de fotos no se puede elegir una imagen.")
    }
    fun elegirDeGaleria() {
        hojaFoto = false
        if (PermisosHelper.accesoFotos(context)) {
            abrirSelector()
        } else {
            explicarPermiso = true
        }
    }

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // ---------- Cabecera con degradado y foto ----------
        Box(Modifier.fillMaxWidth().height(206.dp)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(DeepNebula)
            ) {
                Image(
                    painter = painterResource(R.drawable.aurora_banner),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.CenterEnd,
                    alpha = 0.55f,
                    modifier = Modifier.matchParentSize()
                )
                Text(
                    "Mi perfil",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp)
                )
            }
            Box(Modifier.align(Alignment.BottomCenter).size(116.dp)) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(4.dp)
                ) { AvatarPerfil(perfil, 108.dp) }
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable(onClickLabel = "Cambiar foto de perfil") { hojaFoto = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                }
            }
        }

        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ---------- Datos ----------
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(perfil?.nombre.orEmpty(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                perfil?.let {
                    Text("Te llamamos «${it.nombreParaSaludo}»", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
                perfil?.correo?.let { correo ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(6.dp))
                        Text(correo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // ---------- Mi actividad ----------
            Text("Mi actividad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaDato(Icons.Filled.Schedule, formatoHoras(estadisticas.minutos), "Horas registradas", Modifier.weight(1f))
                TarjetaDato(Icons.Filled.Timeline, estadisticas.registros.toString(), "Registros", Modifier.weight(1f))
            }
            TarjetaDato(
                Icons.Filled.Folder,
                estadisticas.proyectoPrincipal ?: "Aún sin registros",
                "Proyecto con más horas",
                Modifier.fillMaxWidth()
            )

            // ---------- Cuenta ----------
            Text("Cuenta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                FilaOpcion(Icons.Filled.Edit, "Editar perfil", "Nombre y cómo te llamamos") { editando = true }
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                FilaOpcion(Icons.Filled.PhotoLibrary, "Cambiar foto de perfil", "Elige una imagen de tus fotos") { elegirDeGaleria() }
                if (perfil?.fotoPath != null) {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    FilaOpcion(Icons.Filled.Delete, "Quitar foto", "Volver a mostrar tu inicial") { perfilVm.quitarFoto() }
                }
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                FilaOpcion(Icons.Filled.Palette, "Apariencia", modoTema.etiqueta) { dialogoTema = true }
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                FilaOpcion(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", "Salir de esta cuenta", destacado = true) { confirmarCierre = true }
            }

            Text(
                "Vappnet · Registro de horas para Deepsource",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp)
            )
        }
    }

    // ---------- Diálogos y hoja inferior ----------
    if (hojaFoto) {
        ModalBottomSheet(onDismissRequest = { hojaFoto = false }) {
            Column(Modifier.padding(bottom = 24.dp)) {
                Text(
                    "Foto de perfil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
                FilaOpcion(Icons.Filled.PhotoLibrary, "Elegir de mis fotos", "Se pedirá permiso para ver tus fotos") { elegirDeGaleria() }
                if (perfil?.fotoPath != null) {
                    FilaOpcion(Icons.Filled.Delete, "Quitar foto actual", null) {
                        hojaFoto = false
                        perfilVm.quitarFoto()
                    }
                }
            }
        }
    }

    if (explicarPermiso) {
        AlertDialog(
            onDismissRequest = { explicarPermiso = false },
            icon = { Icon(Icons.Filled.PhotoLibrary, contentDescription = null) },
            title = { Text("Acceso a tus fotos") },
            text = { Text("Vappnet necesita acceder a tus fotos para que puedas elegir tu foto de perfil. Solo se guarda la imagen que elijas.") },
            confirmButton = {
                TextButton(onClick = {
                    explicarPermiso = false
                    pedirPermisoFotos.launch(PermisosHelper.permisosFotos())
                }) { Text("Continuar") }
            },
            dismissButton = { TextButton(onClick = { explicarPermiso = false }) { Text("Ahora no") } }
        )
    }

    if (editando) {
        DialogoEditarPerfil(
            nombreInicial = perfil?.nombre.orEmpty(),
            nombreMostrarInicial = perfil?.nombreMostrar.orEmpty(),
            validarNombre = perfilVm::errorNombreCompleto,
            validarMostrar = perfilVm::errorNombreMostrar,
            alGuardar = { nombre, mostrar ->
                perfilVm.guardarPerfil(nombre, mostrar)
                editando = false
            },
            alCancelar = { editando = false }
        )
    }

    if (dialogoTema) {
        AlertDialog(
            onDismissRequest = { dialogoTema = false },
            title = { Text("Apariencia") },
            text = {
                Column {
                    ModoTema.entries.forEach { opcion ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    temaVm.cambiar(opcion)
                                    dialogoTema = false
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = opcion == modoTema, onClick = null)
                            Spacer(Modifier.width(12.dp))
                            Text(opcion.etiqueta, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { dialogoTema = false }) { Text("Cerrar") } }
        )
    }

    if (confirmarCierre) {
        AlertDialog(
            onDismissRequest = { confirmarCierre = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tus registros quedan guardados. Podrás volver a entrar cuando quieras.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarCierre = false
                    perfilVm.cerrarSesion()
                }) { Text("Cerrar sesión", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmarCierre = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun TarjetaDato(icono: ImageVector, valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) { Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp)) }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(valor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FilaOpcion(
    icono: ImageVector,
    titulo: String,
    detalle: String?,
    destacado: Boolean = false,
    alClic: () -> Unit
) {
    val color = if (destacado) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Row(
        Modifier.fillMaxWidth().clickable(onClick = alClic).padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icono, contentDescription = null, tint = if (destacado) color else MaterialTheme.colorScheme.primary)
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = color)
            if (detalle != null) {
                Text(detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
