package com.example.registrohoras.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.registrohoras.ui.screens.HistorialScreen
import com.example.registrohoras.ui.screens.RegistroScreen
import com.example.registrohoras.ui.screens.InicioScreen
import com.example.registrohoras.ui.screens.PerfilScreen
import com.example.registrohoras.viewmodel.HorasViewModel
import com.example.registrohoras.viewmodel.PerfilViewModel
import com.example.registrohoras.viewmodel.TemaViewModel

sealed class Destino(val ruta: String, val titulo: String, val icono: ImageVector) {
    data object Inicio : Destino("inicio", "Inicio", Icons.Filled.Home)
    data object Registrar : Destino("registrar", "Registrar", Icons.Filled.Add)
    data object Historial : Destino("historial", "Historial", Icons.Filled.History)
    data object Perfil : Destino("perfil", "Perfil", Icons.Filled.Person)
}

private val destinos = listOf(Destino.Inicio, Destino.Registrar, Destino.Historial, Destino.Perfil)

@Composable
fun AppNavigation(destinoExterno: String?, alConsumirDestino: () -> Unit) {
    val navController = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    // Un único ViewModel de datos compartido por Inicio e Historial.
    val horasVm: HorasViewModel = viewModel()
    // Perfil de la persona con sesión iniciada (lo usan Inicio, para el saludo, y la pestaña Perfil).
    val perfilVm: PerfilViewModel = viewModel()
    val temaVm: TemaViewModel = viewModel()
    val perfil by perfilVm.perfil.collectAsStateWithLifecycle()

    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    fun ir(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Llegada desde el widget ("Registrar horas"): abre directamente el formulario.
    LaunchedEffect(destinoExterno) {
        if (destinoExterno != null) {
            if (destinos.any { it.ruta == destinoExterno }) ir(destinoExterno)
            alConsumirDestino()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar {
                destinos.forEach { destino ->
                    NavigationBarItem(
                        selected = rutaActual == destino.ruta,
                        onClick = { ir(destino.ruta) },
                        icon = { Icon(destino.icono, contentDescription = null) },
                        label = { Text(destino.titulo) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destino.Inicio.ruta,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding),
            enterTransition = { fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 14 } },
            exitTransition = { fadeOut(tween(150)) }
        ) {
            composable(Destino.Inicio.ruta) {
                InicioScreen(
                    horasVm = horasVm,
                    perfil = perfil,
                    alRegistrar = { ir(Destino.Registrar.ruta) },
                    alAbrirPerfil = { ir(Destino.Perfil.ruta) }
                )
            }
            composable(Destino.Registrar.ruta) {
                RegistroScreen(snackbar = snackbar, alVerHistorial = { ir(Destino.Historial.ruta) })
            }
            composable(Destino.Historial.ruta) {
                HistorialScreen(horasVm = horasVm, alRegistrar = { ir(Destino.Registrar.ruta) })
            }
            composable(Destino.Perfil.ruta) {
                PerfilScreen(perfilVm = perfilVm, temaVm = temaVm, snackbar = snackbar)
            }
        }
    }
}
