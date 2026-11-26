package com.example.registrohoras.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.registrohoras.ui.screens.LoginScreen
import com.example.registrohoras.viewmodel.SesionViewModel

/** Raíz de la app: sin sesión muestra el login; con sesión, la navegación principal. */
@Composable
fun AppRaiz(
    destinoExterno: String?,
    alConsumirDestino: () -> Unit,
    sesionVm: SesionViewModel = viewModel()
) {
    val haySesion by sesionVm.haySesion.collectAsStateWithLifecycle()

    Crossfade(targetState = haySesion, animationSpec = tween(350), label = "sesion") { conSesion ->
        if (conSesion) {
            // Si el widget pidió una pantalla antes de iniciar sesión, se abre apenas se entra.
            AppNavigation(
                destinoExterno = destinoExterno,
                alConsumirDestino = alConsumirDestino
            )
        } else {
            LoginScreen()
        }
    }
}
