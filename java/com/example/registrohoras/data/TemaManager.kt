package com.example.registrohoras.data

import android.content.Context
import com.example.registrohoras.domain.ModoTema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Guarda la apariencia elegida (Sistema / Claro / Oscuro) para que se mantenga al cerrar la app. */
class TemaManager(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("tema", Context.MODE_PRIVATE)
    private val _modo = MutableStateFlow(leer())

    val modo: StateFlow<ModoTema> = _modo.asStateFlow()

    fun guardar(modo: ModoTema) {
        prefs.edit().putString(CLAVE_MODO, modo.name).apply()
        _modo.value = modo
    }

    private fun leer(): ModoTema =
        runCatching { ModoTema.valueOf(prefs.getString(CLAVE_MODO, null) ?: ModoTema.SISTEMA.name) }
            .getOrDefault(ModoTema.SISTEMA)

    private companion object {
        const val CLAVE_MODO = "modo"
    }
}
