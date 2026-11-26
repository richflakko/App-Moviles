package com.example.registrohoras.data.remote

import android.content.Context
import java.util.UUID

/** Identificador anónimo de esta instalación: junto con el id local del registro evita duplicados en el servidor. */
object DispositivoId {
    fun obtener(context: Context): String {
        val prefs = context.applicationContext.getSharedPreferences("dispositivo", Context.MODE_PRIVATE)
        return prefs.getString("id", null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString("id", it).apply()
        }
    }
}
