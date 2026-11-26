package com.example.registrohoras.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrohoras.RegistroApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Decide si se muestra el login o la app, según haya o no una sesión iniciada. */
class SesionViewModel(app: Application) : AndroidViewModel(app) {
    private val auth = (app as RegistroApp).auth

    val haySesion: StateFlow<Boolean> = auth.usuarioId
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, auth.usuarioId.value != null)

    fun cerrarSesion() = auth.cerrarSesion()
}
