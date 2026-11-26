package com.example.registrohoras.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.registrohoras.RegistroApp
import com.example.registrohoras.ui.theme.RegistroHorasTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Pantalla de personalización del widget. Android la abre al colocar el widget y también desde el menú
 * del widget en el escritorio (mantener presionado → configuración / editar).
 */
class WidgetConfigActivity : ComponentActivity() {
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        widgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        // Si la persona sale sin guardar, el widget no se agrega (o se queda como estaba).
        setResult(RESULT_CANCELED, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val app = application as RegistroApp
        setContent {
            val modo by app.tema.modo.collectAsStateWithLifecycle()
            var datos by remember { mutableStateOf<DatosWidget?>(null) }
            LaunchedEffect(Unit) { datos = HorasWidgetProvider.cargarDatos(app) }

            RegistroHorasTheme(modo) {
                ConfigurarWidgetScreen(
                    colorInicial = WidgetPrefs.colorFondo(app, widgetId),
                    datos = datos,
                    alGuardar = { color -> guardar(app, color) },
                    alCancelar = { finish() }
                )
            }
        }
    }

    private fun guardar(app: RegistroApp, color: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            WidgetPrefs.guardarColor(app, widgetId, color)
            HorasWidgetProvider.actualizar(app, AppWidgetManager.getInstance(app), intArrayOf(widgetId))
            withContext(Dispatchers.Main) {
                setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
                finish()
            }
        }
    }
}
