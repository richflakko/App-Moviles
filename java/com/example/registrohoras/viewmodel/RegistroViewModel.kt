package com.example.registrohoras.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrohoras.RegistroApp
import com.example.registrohoras.data.local.RegistroEntity
import com.example.registrohoras.domain.Campo
import com.example.registrohoras.domain.Coordenadas
import com.example.registrohoras.domain.DatosFormulario
import com.example.registrohoras.domain.FormularioValidator
import com.example.registrohoras.domain.Proyecto
import com.example.registrohoras.domain.Usuario
import com.example.registrohoras.domain.formatoHoras
import com.example.registrohoras.util.FotoHelper
import com.example.registrohoras.widget.HorasWidgetProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

/** Estado completo del formulario. La pantalla solo lo dibuja; no decide nada. */
data class FormularioState(
    val usuarioId: String? = null,
    val proyectoId: String? = null,
    /** Nombres escritos a mano con la opción "Otro" (se crean al guardar el registro). */
    val usuarioNuevo: String? = null,
    val proyectoNuevo: String? = null,
    val fecha: LocalDate = LocalDate.now(),
    val tarea: String = "",
    val horas: String = "0",
    val minutos: String = "0",
    val fotoPath: String? = null,
    val ubicacion: Coordenadas? = null,
    val buscandoUbicacion: Boolean = false,
    /** Campos que el usuario ya tocó: solo a esos se les muestra el error. */
    val tocados: Set<Campo> = emptySet(),
    val errores: Map<Campo, String> = emptyMap(),
    val guardando: Boolean = false,
    /** Duración formateada del último registro guardado (dispara el diálogo de éxito). */
    val guardado: String? = null,
    val mensaje: String? = null,
    /** Nombre del feriado (API externa Nager.Date) si la fecha elegida es feriado en Chile. */
    val feriado: String? = null
) {
    fun toDatos() = DatosFormulario(usuarioId, proyectoId, fecha, tarea, horas, minutos, usuarioNuevo, proyectoNuevo)
    fun error(campo: Campo): String? = errores[campo]
    fun esValido(campo: Campo): Boolean = campo in tocados && campo !in errores
}

class RegistroViewModel(app: Application) : AndroidViewModel(app) {
    private val repositorio = (app as RegistroApp).repository
    private val catalogo = (app as RegistroApp).catalogo
    private val registroApp = app as RegistroApp

    private val _estado = MutableStateFlow(FormularioState())
    val estado: StateFlow<FormularioState> = _estado.asStateFlow()

    private var fotoPendiente: File? = null

    /** Opciones de los selectores (vienen de la base de datos). */
    val usuarios: StateFlow<List<Usuario>> =
        catalogo.usuarios.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val proyectos: StateFlow<List<Proyecto>> =
        catalogo.proyectos.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Con la sesión iniciada, el formulario parte con el usuario conectado ya seleccionado.
        (app as RegistroApp).sesion.usuarioId.value?.let { id -> _estado.update { it.copy(usuarioId = id) } }
        consultarFeriado(LocalDate.now())
    }

    /** Pregunta a la API externa si la fecha es feriado; si no hay internet, simplemente no avisa. */
    private fun consultarFeriado(fecha: LocalDate) {
        viewModelScope.launch {
            val nombre = registroApp.feriados.nombreFeriado(fecha)
            // Solo se aplica si la persona no cambió de fecha mientras esperaba la respuesta.
            _estado.update { if (it.fecha == fecha) it.copy(feriado = nombre) else it }
        }
    }

    /** Aplica un cambio y revalida; los errores solo se muestran en campos ya tocados. */
    private fun actualizar(campo: Campo? = null, cambio: (FormularioState) -> FormularioState) {
        _estado.update { actual ->
            val nuevo = cambio(actual)
            val tocados = if (campo != null) nuevo.tocados + campo else nuevo.tocados
            nuevo.copy(
                tocados = tocados,
                errores = FormularioValidator.validar(nuevo.toDatos()).filterKeys { it in tocados }
            )
        }
    }

    fun onUsuario(id: String) = actualizar(Campo.USUARIO) { it.copy(usuarioId = id, usuarioNuevo = null) }
    fun onProyecto(id: String) = actualizar(Campo.PROYECTO) { it.copy(proyectoId = id, proyectoNuevo = null) }

    /** Opción "Otro": se usa el nombre escrito en lugar de un usuario/proyecto de la lista. */
    fun onUsuarioNuevo(nombre: String) = actualizar(Campo.USUARIO) {
        it.copy(usuarioId = null, usuarioNuevo = FormularioValidator.normalizarNombre(nombre))
    }
    fun onProyectoNuevo(nombre: String) = actualizar(Campo.PROYECTO) {
        it.copy(proyectoId = null, proyectoNuevo = FormularioValidator.normalizarNombre(nombre))
    }

    /** Validación en vivo del diálogo "Otro" (incluye evitar nombres repetidos). */
    fun errorUsuarioNuevo(nombre: String): String? =
        FormularioValidator.validarNombreNuevo(nombre, usuarios.value.map { it.nombre }, "usuario")
    fun errorProyectoNuevo(nombre: String): String? =
        FormularioValidator.validarNombreNuevo(nombre, proyectos.value.map { it.nombre }, "proyecto")
    fun onFecha(fecha: LocalDate) {
        actualizar(Campo.FECHA) { it.copy(fecha = fecha, feriado = null) }
        consultarFeriado(fecha)
    }
    fun onTarea(texto: String) = actualizar(Campo.TAREA) { it.copy(tarea = texto) }
    fun onHoras(texto: String) = actualizar(Campo.HORAS) { it.copy(horas = texto.filter(Char::isDigit).take(3)) }
    fun onMinutos(texto: String) = actualizar(Campo.MINUTOS) { it.copy(minutos = texto.filter(Char::isDigit).take(3)) }

    /** Acceso rápido de duración: completa horas y minutos (siguen siendo editables). */
    fun duracionRapida(totalMinutos: Int) {
        actualizar(Campo.HORAS) { it.copy(horas = (totalMinutos / 60).toString()) }
        actualizar(Campo.MINUTOS) { it.copy(minutos = (totalMinutos % 60).toString()) }
    }

    /** Se llama cuando el campo pierde el foco: marca el campo como tocado y valida. */
    fun onPerdioFoco(campo: Campo) = actualizar(campo) { it }

    fun guardar() {
        val actual = _estado.value
        if (actual.guardando) return

        val errores = FormularioValidator.validar(actual.toDatos())
        if (errores.isNotEmpty()) {
            _estado.update {
                it.copy(
                    tocados = Campo.entries.toSet(),
                    errores = errores,
                    mensaje = "Revisa los campos marcados en rojo."
                )
            }
            return
        }
        val minutos = actual.horas.trim().toInt() * 60 + actual.minutos.trim().toInt()

        _estado.update { it.copy(guardando = true) }
        viewModelScope.launch {
            try {
                // "Otro": la persona o el proyecto escritos a mano se crean recién al guardar el registro.
                val usuarioId = actual.usuarioId ?: catalogo.agregarUsuario(actual.usuarioNuevo.orEmpty())
                val proyectoId = actual.proyectoId ?: catalogo.agregarProyecto(actual.proyectoNuevo.orEmpty())
                repositorio.guardar(
                    RegistroEntity(
                        usuarioId = usuarioId,
                        proyectoId = proyectoId,
                        fecha = actual.fecha.toString(),
                        minutos = minutos,
                        tarea = actual.tarea.trim(),
                        latitud = actual.ubicacion?.latitud,
                        longitud = actual.ubicacion?.longitud,
                        fotoPath = actual.fotoPath
                    )
                )
                HorasWidgetProvider.actualizarTodos(getApplication<Application>())
                // Envío al microservicio en segundo plano: el registro ya quedó guardado en Room aunque no haya internet.
                registroApp.appScope.launch { registroApp.sync.sincronizarPendientes() }
                // Formulario limpio, pero se conserva el usuario para registrar más rápido.
                _estado.value = FormularioState(usuarioId = usuarioId, guardado = formatoHoras(minutos.toLong()))
                consultarFeriado(LocalDate.now())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.update { it.copy(guardando = false, mensaje = "No se pudo guardar. Inténtalo nuevamente.") }
            }
        }
    }

    // ---------- Recurso nativo: cámara ----------
    fun prepararFoto(): Uri {
        val archivo = FotoHelper.crearArchivo(getApplication<Application>())
        fotoPendiente = archivo
        return FotoHelper.uriDe(getApplication<Application>(), archivo)
    }

    fun resultadoFoto(exito: Boolean) {
        val archivo = fotoPendiente
        fotoPendiente = null
        if (exito && archivo != null && archivo.length() > 0) {
            val anterior = _estado.value.fotoPath
            _estado.update { it.copy(fotoPath = archivo.absolutePath) }
            anterior?.let { FotoHelper.borrar(it) }
        } else {
            archivo?.delete()
        }
    }

    fun quitarFoto() {
        _estado.value.fotoPath?.let { FotoHelper.borrar(it) }
        _estado.update { it.copy(fotoPath = null) }
    }

    // ---------- Recurso nativo: ubicación ----------
    fun buscandoUbicacion() = _estado.update { it.copy(buscandoUbicacion = true) }

    fun resultadoUbicacion(coordenadas: Coordenadas?) = _estado.update {
        it.copy(
            buscandoUbicacion = false,
            ubicacion = coordenadas ?: it.ubicacion,
            mensaje = if (coordenadas == null) "No se pudo obtener la ubicación. Activa el GPS e inténtalo de nuevo." else it.mensaje
        )
    }

    fun quitarUbicacion() = _estado.update { it.copy(ubicacion = null) }

    // ---------- Mensajes ----------
    fun mostrarMensaje(texto: String) = _estado.update { it.copy(mensaje = texto) }
    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }
    fun cerrarGuardado() = _estado.update { it.copy(guardado = null) }
}
