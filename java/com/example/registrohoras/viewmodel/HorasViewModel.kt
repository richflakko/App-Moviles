package com.example.registrohoras.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrohoras.RegistroApp
import com.example.registrohoras.data.local.RegistroEntity
import com.example.registrohoras.domain.Proyecto
import com.example.registrohoras.domain.Usuario
import com.example.registrohoras.util.FotoHelper
import com.example.registrohoras.widget.HorasWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Estado de la sincronización con el microservicio (lo dibuja la pantalla Inicio). */
data class EstadoSync(
    val sincronizando: Boolean = false,
    val mensaje: String? = null,
    /** Minutos que tiene el servidor en total (null = aún no consultado o sin conexión). */
    val totalServidor: Long? = null
)

/** Datos para las pantallas Inicio e Historial. */
class HorasViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = (app as RegistroApp).repository
    private val catalogo = (app as RegistroApp).catalogo
    private val registroApp = app as RegistroApp

    /** Usuarios y proyectos guardados en la base de datos. */
    val usuarios: StateFlow<List<Usuario>> =
        catalogo.usuarios.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val proyectos: StateFlow<List<Proyecto>> =
        catalogo.proyectos.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Minutos acumulados por proyecto. */
    val totales: StateFlow<Map<String, Long>> =
        repositorio.totales.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    /** null = todavía cargando desde la base de datos. */
    val registros: StateFlow<List<RegistroEntity>?> =
        repositorio.registros.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /** Registros que todavía no llegan al servidor. */
    val pendientes: StateFlow<Int> =
        repositorio.pendientes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _sync = MutableStateFlow(EstadoSync())
    val sync: StateFlow<EstadoSync> = _sync.asStateFlow()

    /** Envía lo pendiente al servidor y consulta el total que éste tiene. */
    fun sincronizar() {
        if (_sync.value.sincronizando) return
        _sync.update { it.copy(sincronizando = true, mensaje = null) }
        viewModelScope.launch {
            val resultado = registroApp.sync.sincronizarPendientes()
            val total = registroApp.sync.totalServidor()
            _sync.value = EstadoSync(
                sincronizando = false,
                totalServidor = total,
                mensaje = when {
                    resultado.sinConexion || total == null -> "Sin conexión con el servidor. Se reintentará más tarde."
                    resultado.fallidos > 0 -> "${resultado.fallidos} registro(s) fueron rechazados por el servidor."
                    resultado.enviados > 0 -> "Se enviaron ${resultado.enviados} registro(s) al servidor."
                    else -> "Todo está sincronizado."
                }
            )
        }
    }

    fun eliminar(registro: RegistroEntity) {
        viewModelScope.launch {
            repositorio.eliminar(registro)
            registro.fotoPath?.let { ruta -> withContext(Dispatchers.IO) { FotoHelper.borrar(ruta) } }
            HorasWidgetProvider.actualizarTodos(getApplication<Application>())
            registroApp.appScope.launch { registroApp.sync.eliminarRemoto(registro) }
        }
    }
}
