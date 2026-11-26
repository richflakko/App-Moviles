package com.example.registrohoras.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrohoras.RegistroApp
import com.example.registrohoras.domain.FormularioValidator
import com.example.registrohoras.domain.Perfil
import com.example.registrohoras.domain.PerfilValidator
import com.example.registrohoras.domain.Usuario
import com.example.registrohoras.domain.nombreProyecto
import com.example.registrohoras.util.PerfilFotoHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Actividad personal de la persona con sesión iniciada. */
data class Estadisticas(
    val minutos: Long = 0,
    val registros: Int = 0,
    val proyectoPrincipal: String? = null
)

class PerfilViewModel(app: Application) : AndroidViewModel(app) {
    private val raiz = app as RegistroApp
    private val perfiles = raiz.perfiles
    private val sesion = raiz.sesion
    private val catalogo = raiz.catalogo

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val usuarios: StateFlow<List<Usuario>> =
        catalogo.usuarios.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Perfil de la persona con sesión iniciada (se actualiza solo al editarlo). */
    @OptIn(ExperimentalCoroutinesApi::class)
    val perfil: StateFlow<Perfil?> = sesion.usuarioId
        .flatMapLatest { id -> if (id == null) flowOf(null) else perfiles.observar(id) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Horas, registros y proyecto principal de la persona con sesión iniciada. */
    val estadisticas: StateFlow<Estadisticas> =
        combine(raiz.repository.registros, sesion.usuarioId, catalogo.proyectos) { lista, id, proyectos ->
            val propios = lista.filter { it.usuarioId == id }
            val principal = propios
                .groupBy { it.proyectoId }
                .maxByOrNull { (_, registros) -> registros.sumOf { it.minutos.toLong() } }
                ?.key
            Estadisticas(
                minutos = propios.sumOf { it.minutos.toLong() },
                registros = propios.size,
                proyectoPrincipal = principal?.let { proyectos.nombreProyecto(it) }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Estadisticas())

    // ---------- Validación en vivo del diálogo "Editar perfil" ----------
    fun errorNombreCompleto(nombre: String): String? {
        val miId = sesion.usuarioId.value
        val otros = usuarios.value.filter { it.id != miId }.map { it.nombre }
        return PerfilValidator.validarNombreCompleto(nombre, otros)
    }

    fun errorNombreMostrar(nombre: String): String? = PerfilValidator.validarNombreMostrar(nombre)

    // ---------- Acciones ----------
    fun guardarPerfil(nombre: String, nombreMostrar: String) {
        val id = sesion.usuarioId.value ?: return
        viewModelScope.launch {
            perfiles.actualizarNombres(
                id = id,
                nombre = FormularioValidator.normalizarNombre(nombre),
                nombreMostrar = FormularioValidator.normalizarNombre(nombreMostrar).ifEmpty { null }
            )
            _mensaje.value = "Perfil actualizado."
        }
    }

    fun cambiarFoto(uri: Uri) {
        val id = sesion.usuarioId.value ?: return
        viewModelScope.launch {
            val anterior = perfil.value?.fotoPath
            val ruta = withContext(Dispatchers.IO) { PerfilFotoHelper.guardar(getApplication<Application>(), uri, id) }
            if (ruta == null) {
                _mensaje.value = "No se pudo usar esa imagen. Prueba con otra."
            } else {
                perfiles.actualizarFoto(id, ruta)
                PerfilFotoHelper.borrar(anterior)
                _mensaje.value = "Foto de perfil actualizada."
            }
        }
    }

    fun quitarFoto() {
        val id = sesion.usuarioId.value ?: return
        viewModelScope.launch {
            val anterior = perfil.value?.fotoPath
            perfiles.actualizarFoto(id, null)
            PerfilFotoHelper.borrar(anterior)
            _mensaje.value = "Foto de perfil eliminada."
        }
    }

    fun cerrarSesion() = raiz.auth.cerrarSesion()

    fun mostrarMensaje(texto: String) = _mensaje.update { texto }
    fun mensajeMostrado() = _mensaje.update { null }
}
