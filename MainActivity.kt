package com.example.registrohoras

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.registrohoras.ui.navigation.AppNavigation
import com.example.registrohoras.ui.theme.RegistroHorasTheme

class MainActivity : ComponentActivity() {
    /** Pantalla pedida desde fuera (p. ej. el botón "Registrar horas" del widget). */
    private var destino by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Solo en el primer arranque: al girar la pantalla no se debe volver a navegar.
        if (savedInstanceState == null) destino = intent.getStringExtra(EXTRA_DESTINO)
        setContent {
            RegistroHorasTheme {
                AppNavigation(destinoExterno = destino, alConsumirDestino = { destino = null })
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
