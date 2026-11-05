package com.example.registrohoras.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrohoras.RegistroApp
import com.example.registrohoras.data.local.RegistroEntity
import com.example.registrohoras.util.FotoHelper
import com.example.registrohoras.widget.HorasWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Datos para las pantallas Resumen e Historial. */
class HorasViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = (app as RegistroApp).repository

    /** Minutos acumulados por proyecto. */
    val totales: StateFlow<Map<String, Long>> =
        repositorio.totales.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    /** null = todavía cargando desde la base de datos. */
    val registros: StateFlow<List<RegistroEntity>?> =
        repositorio.registros.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun eliminar(registro: RegistroEntity) {
        viewModelScope.launch {
            repositorio.eliminar(registro)
            registro.fotoPath?.let { ruta -> withContext(Dispatchers.IO) { FotoHelper.borrar(ruta) } }
            HorasWidgetProvider.actualizarTodos(getApplication<Application>())
        }
    }
}
