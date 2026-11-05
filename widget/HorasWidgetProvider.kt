package com.example.registrohoras.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.registrohoras.MainActivity
import com.example.registrohoras.R
import com.example.registrohoras.RegistroApp
import com.example.registrohoras.domain.DatosDemo
import com.example.registrohoras.domain.formatoHoras
import com.example.registrohoras.ui.navigation.Destino
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HorasWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val pendiente = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                actualizar(context.applicationContext, manager, ids)
            } finally {
                pendiente.finish()
            }
        }
    }

    companion object {
        /** Se llama al guardar o eliminar un registro para refrescar todos los widgets colocados. */
        fun actualizarTodos(context: Context) {
            val app = context.applicationContext
            val manager = AppWidgetManager.getInstance(app)
            val ids = manager.getAppWidgetIds(ComponentName(app, HorasWidgetProvider::class.java))
            if (ids.isEmpty()) return
            CoroutineScope(Dispatchers.IO).launch { actualizar(app, manager, ids) }
        }

        private suspend fun actualizar(context: Context, manager: AppWidgetManager, ids: IntArray) {
            val repositorio = (context as RegistroApp).repository
            val totales = repositorio.totalesActuales()

            // El botón abre la app directamente en la pantalla de registro.
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(MainActivity.EXTRA_DESTINO, Destino.Registrar.ruta)
            }
            val pending = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            ids.forEach { id ->
                val views = RemoteViews(context.packageName, R.layout.widget_horas)
                views.removeAllViews(R.id.project_rows)
                DatosDemo.proyectos.forEach { proyecto ->
                    val fila = RemoteViews(context.packageName, R.layout.widget_row)
                    fila.setTextViewText(R.id.project_name, proyecto.nombre)
                    fila.setTextViewText(R.id.project_hours, formatoHoras(totales[proyecto.id] ?: 0L))
                    views.addView(R.id.project_rows, fila)
                }
                views.setOnClickPendingIntent(R.id.register_button, pending)
                manager.updateAppWidget(id, views)
            }
        }
    }
}
