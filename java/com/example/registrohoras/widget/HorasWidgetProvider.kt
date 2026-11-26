package com.example.registrohoras.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import com.example.registrohoras.MainActivity
import com.example.registrohoras.R
import com.example.registrohoras.RegistroApp
import com.example.registrohoras.domain.ColorWidget
import com.example.registrohoras.domain.formatoHoras
import com.example.registrohoras.ui.navigation.Destino
import com.example.registrohoras.ui.theme.colorProyecto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Una fila del widget: proyecto, tiempo acumulado y su color identificador (el mismo de la app). */
data class FilaWidget(val nombre: String, val minutos: Long, val color: Int)

/** Datos que muestra el widget; salen de las mismas consultas de Room que usa la app. */
data class DatosWidget(val total: Long, val proyectos: List<FilaWidget>)

/**
 * Widget "Horas por proyecto" (RemoteViews, como el original). Elige entre el diseño compacto y el
 * completo según el tamaño que tenga en el escritorio, y aplica el color elegido por la persona.
 */
open class HorasWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ejecutar(context) { actualizar(it, manager, ids) }
    }

    /** Al cambiar el tamaño del widget se vuelve a dibujar (compacto <-> completo). */
    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, newOptions: Bundle) {
        ejecutar(context) { actualizar(it, manager, intArrayOf(id)) }
    }

    override fun onDeleted(context: Context, ids: IntArray) {
        ids.forEach { WidgetPrefs.borrar(context, it) }
    }

    private fun ejecutar(context: Context, bloque: suspend (Context) -> Unit) {
        val pendiente = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                bloque(context.applicationContext)
            } finally {
                pendiente.finish()
            }
        }
    }

    companion object {
        /** Alto (dp) por debajo del cual se usa el diseño compacto 4x2. */
        private const val UMBRAL_COMPACTO_DP = 160

        /** Alto fijo (dp) del diseño completo sin filas de proyectos; cada fila ocupa ~22 dp. */
        private const val ALTO_FIJO_COMPLETO_DP = 183
        private const val ALTO_FILA_DP = 22

        /** Se llama al guardar o eliminar un registro para refrescar todos los widgets colocados. */
        fun actualizarTodos(context: Context) {
            val app = context.applicationContext
            val manager = AppWidgetManager.getInstance(app)
            val ids = listOf(HorasWidgetProvider::class.java, HorasWidgetCompactoProvider::class.java)
                .flatMap { manager.getAppWidgetIds(ComponentName(app, it)).toList() }
            if (ids.isEmpty()) return
            CoroutineScope(Dispatchers.IO).launch { actualizar(app, manager, ids.toIntArray()) }
        }

        /** Totales por proyecto (los de mayor tiempo primero). También los usa la pantalla de personalización. */
        suspend fun cargarDatos(app: RegistroApp): DatosWidget {
            // Si el widget se actualiza antes de abrir la app, se asegura que existan los proyectos.
            app.catalogo.asegurarDatosIniciales()
            val proyectos = app.catalogo.proyectosActuales()
            val totales = app.repository.totalesActuales()
            val filas = proyectos.mapIndexed { indice, proyecto ->
                FilaWidget(proyecto.nombre, totales[proyecto.id] ?: 0L, colorProyecto(indice).fondo.toArgb())
            }
            return DatosWidget(total = filas.sumOf { it.minutos }, proyectos = filas.sortedByDescending { it.minutos })
        }

        suspend fun actualizar(context: Context, manager: AppWidgetManager, ids: IntArray) {
            val app = context.applicationContext as RegistroApp
            val datos = cargarDatos(app)

            // El botón abre la app directamente en Registrar; tocar el resto del widget abre la app.
            val registrar = PendingIntent.getActivity(
                app, 0,
                Intent(app, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(MainActivity.EXTRA_DESTINO, Destino.Registrar.ruta)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val abrir = PendingIntent.getActivity(
                app, 1,
                Intent(app, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            ids.forEach { id ->
                val compacto = esCompacto(manager, id)
                val maxFilas = if (compacto) 2 else ((altoDp(manager, id) - ALTO_FIJO_COMPLETO_DP) / ALTO_FILA_DP).coerceIn(2, 6)
                val colores = ColorWidget.aplicar(WidgetPrefs.colorFondo(app, id))
                manager.updateAppWidget(id, crearVistas(app, compacto, colores, datos, maxFilas, registrar, abrir))
            }
        }

        private fun altoDp(manager: AppWidgetManager, id: Int): Int =
            manager.getAppWidgetOptions(id).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0).takeIf { it > 0 } ?: 250

        private fun esCompacto(manager: AppWidgetManager, id: Int): Boolean {
            val alto = manager.getAppWidgetOptions(id).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
            if (alto > 0) return alto < UMBRAL_COMPACTO_DP
            // Aún sin medidas: se decide por el widget que eligió la persona en el selector.
            return manager.getAppWidgetInfo(id)?.provider?.className == HorasWidgetCompactoProvider::class.java.name
        }

        private fun crearVistas(
            context: Context,
            compacto: Boolean,
            c: ColorWidget.Colores,
            datos: DatosWidget,
            maxFilas: Int,
            registrar: PendingIntent,
            abrir: PendingIntent
        ): RemoteViews {
            val vistas = RemoteViews(context.packageName, if (compacto) R.layout.widget_horas_compacto else R.layout.widget_horas)

            // El color de fondo se aplica como filtro sobre una silueta: conserva las esquinas redondeadas.
            vistas.setInt(R.id.wd_fondo, "setColorFilter", c.fondo)
            vistas.setInt(R.id.wd_borde, "setColorFilter", c.borde)
            vistas.setInt(R.id.wd_div, "setBackgroundColor", c.borde)
            vistas.setTextColor(R.id.wd_marca, c.texto)
            vistas.setTextColor(R.id.wd_subtitulo, c.textoSecundario)
            vistas.setTextColor(R.id.wd_etiqueta, c.textoSecundario)
            vistas.setTextColor(R.id.wd_total, c.texto)
            vistas.setTextViewText(R.id.wd_total, formatoHoras(datos.total))
            vistas.setInt(R.id.wd_boton_fondo, "setColorFilter", c.boton)
            vistas.setTextColor(R.id.wd_boton_signo, c.signoBoton)

            if (!compacto) {
                vistas.setInt(R.id.wd_pildora, "setColorFilter", c.boton)
                vistas.setTextColor(R.id.wd_pildora_texto, c.signoBoton)
                vistas.setOnClickPendingIntent(R.id.register_button, registrar)
            }

            vistas.removeAllViews(R.id.project_rows)
            datos.proyectos.take(maxFilas).forEach { fila ->
                val vista = RemoteViews(context.packageName, R.layout.widget_row)
                vista.setInt(R.id.project_dot, "setColorFilter", fila.color)
                vista.setTextViewText(R.id.project_name, fila.nombre)
                vista.setTextColor(R.id.project_name, c.texto)
                vista.setTextViewText(R.id.project_hours, if (compacto) horasCortas(fila.minutos) else formatoHoras(fila.minutos))
                vista.setTextColor(R.id.project_hours, c.texto)
                vistas.addView(R.id.project_rows, vista)
            }

            vistas.setOnClickPendingIntent(R.id.widget_root, abrir)
            vistas.setOnClickPendingIntent(R.id.widget_mas, registrar)
            return vistas
        }
    }
}

/** Mismo widget con tamaño inicial 4x2: es la segunda opción que aparece en el selector de widgets. */
class HorasWidgetCompactoProvider : HorasWidgetProvider()
