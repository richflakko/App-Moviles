package com.example.registrohoras.widget

import android.content.Context
import com.example.registrohoras.domain.ColorWidget

/** Color de fondo elegido para cada widget colocado en el escritorio (cada uno guarda el suyo). */
object WidgetPrefs {
    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)

    fun colorFondo(context: Context, widgetId: Int): Int =
        prefs(context).getInt("fondo_$widgetId", ColorWidget.FONDO_PREDETERMINADO)

    fun guardarColor(context: Context, widgetId: Int, color: Int) {
        prefs(context).edit().putInt("fondo_$widgetId", color).apply()
    }

    fun borrar(context: Context, widgetId: Int) {
        prefs(context).edit().remove("fondo_$widgetId").apply()
    }
}
