package com.example.registrohoras

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.registrohoras.domain.ModoTema
import com.example.registrohoras.ui.navigation.AppRaiz
import com.example.registrohoras.ui.theme.RegistroHorasTheme
import com.example.registrohoras.viewmodel.TemaViewModel

class MainActivity : ComponentActivity() {
    /** Pantalla pedida desde fuera (p. ej. el botón "Registrar horas" del widget). */
    private var destino by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Solo en el primer arranque: al girar la pantalla no se debe volver a navegar.
        if (savedInstanceState == null) destino = intent.getStringExtra(EXTRA_DESTINO)
        setContent {
            val temaVm: TemaViewModel = viewModel()
            val modo by temaVm.modo.collectAsStateWithLifecycle()
            val oscuro = when (modo) {
                ModoTema.SISTEMA -> isSystemInDarkTheme()
                ModoTema.CLARO -> false
                ModoTema.OSCURO -> true
            }
            // Los iconos de la barra de estado y de navegación siguen la apariencia elegida en la app.
            DisposableEffect(oscuro) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT) { oscuro },
                    navigationBarStyle = SystemBarStyle.auto(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT) { oscuro }
                )
                onDispose { }
            }
            RegistroHorasTheme(modo) {
                AppRaiz(destinoExterno = destino, alConsumirDestino = { destino = null })
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        destino = intent.getStringExtra(EXTRA_DESTINO)
    }

    companion object {
        const val EXTRA_DESTINO = "destino"
    }
}
