package com.example.registrohoras.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.registrohoras.domain.ProveedorSocial
import com.example.registrohoras.ui.components.BotonSocial
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.registrohoras.R
import com.example.registrohoras.ui.components.AuroraButton
import com.example.registrohoras.ui.components.LogoVappnet
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.registrohoras.viewmodel.LoginViewModel

@Composable
fun LoginScreen(modifier: Modifier = Modifier, vm: LoginViewModel = viewModel()) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val foco = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current

    // Al volver de la página del proveedor (navegador) se ofrece elegir una cuenta de prueba.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.alVolverAlApp() }
    // Entrada suave del formulario la primera vez que se muestra la pantalla.
    val entrada = remember { MutableTransitionState(false).apply { targetState = true } }

    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Arte decorativo (imagen estática, sin animación permanente).
        Image(
            painter = painterResource(R.drawable.aurora_orb),
            contentDescription = null,
            alpha = 0.35f,
            modifier = Modifier.align(Alignment.TopEnd).size(260.dp).offset(x = 90.dp, y = (-40).dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Marca()

            AnimatedVisibility(
                visibleState = entrada,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 10 }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Text("Iniciar sesión", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = estado.correo,
                                onValueChange = vm::onCorreo,
                                label = { Text("Correo") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                                isError = estado.errorCorreo != null,
                                supportingText = { estado.errorCorreo?.let { Text(it) } },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = estado.clave,
                                onValueChange = vm::onClave,
                                label = { Text("Contraseña") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = vm::alternarClave) {
                                        Icon(
                                            if (estado.verClave) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (estado.verClave) "Ocultar contraseña" else "Mostrar contraseña"
                                        )
                                    }
                                },
                                visualTransformation = if (estado.verClave) VisualTransformation.None else PasswordVisualTransformation(),
                                isError = estado.errorClave != null,
                                supportingText = { estado.errorClave?.let { Text(it) } },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    foco.clearFocus()
                                    vm.iniciarSesion()
                                }),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            )

                            AnimatedVisibility(visible = estado.errorGeneral != null) {
                                Text(
                                    estado.errorGeneral.orEmpty(),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            AuroraButton(
                                texto = "Iniciar sesión",
                                alClic = {
                                    foco.clearFocus()
                                    vm.iniciarSesion()
                                },
                                cargando = estado.cargando
                            )
                        }
                    }

                    // Inicio de sesión con otras cuentas: abre la página oficial de cada proveedor.
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(Modifier.weight(1f))
                            Text(
                                "o continúa con",
                                modifier = Modifier.padding(horizontal = 12.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            HorizontalDivider(Modifier.weight(1f))
                        }
                        ProveedorSocial.entries.forEach { proveedor ->
                            BotonSocial(
                                proveedor = proveedor,
                                alClic = {
                                    vm.abrirProveedor(proveedor)
                                    runCatching { uriHandler.openUri(proveedor.urlInicio) }.onFailure { vm.noSePudoAbrir() }
                                }
                            )
                        }
                    }

                    // Acceso de demostración (se elimina en una versión real).
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Acceso de demostración", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Toca un usuario para completar sus credenciales.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                vm.demos.forEach { demo ->
                                    AssistChip(
                                        onClick = { vm.usarDemo(demo) },
                                        label = { Text(demo.nombre) },
                                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    estado.elegirCuentaDe?.let { proveedor ->
        AlertDialog(
            onDismissRequest = vm::cancelarCuentaDemo,
            title = { Text("Continuar con ${proveedor.nombre}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Modo demostración: la app no recibe datos reales de ${proveedor.nombre}. " +
                            "Elige con qué cuenta de prueba quieres entrar.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    vm.demos.forEach { demo ->
                        TextButton(onClick = { vm.entrarConCuentaDemo(demo) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(demo.nombre)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = vm::cancelarCuentaDemo) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Marca() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        LogoVappnet(88.dp)
        Text("vappnet", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
        Text(
            "TIME. PROJECTS. INTELLIGENCE.",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.5.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Inicia sesión para registrar tus horas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
