package com.example.registrohoras.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.registrohoras.RegistroApp
import com.example.registrohoras.domain.ModoTema
import kotlinx.coroutines.flow.StateFlow

/** Apariencia de la app (Sistema / Claro / Oscuro). */
class TemaViewModel(app: Application) : AndroidViewModel(app) {
    private val tema = (app as RegistroApp).tema

    val modo: StateFlow<ModoTema> = tema.modo

    fun cambiar(modo: ModoTema) = tema.guardar(modo)
}
