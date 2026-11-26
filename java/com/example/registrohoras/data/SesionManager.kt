package com.example.registrohoras.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Guarda quién tiene la sesión iniciada (solo su id) para que sobreviva al cierre de la app. */
class SesionManager(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("sesion", Context.MODE_PRIVATE)
    private val _usuarioId = MutableStateFlow(prefs.getString(CLAVE_USUARIO, null))

    /** null = nadie ha iniciado sesión. */
    val usuarioId: StateFlow<String?> = _usuarioId.asStateFlow()

    fun iniciar(id: String) {
        prefs.edit().putString(CLAVE_USUARIO, id).apply()
        _usuarioId.value = id
    }

    fun cerrar() {
        prefs.edit().remove(CLAVE_USUARIO).apply()
        _usuarioId.value = null
    }

    private companion object {
        const val CLAVE_USUARIO = "usuario_id"
    }
}
