package com.example.registrohoras.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.registrohoras.RegistroApp
import com.example.registrohoras.data.SemillaDemo
import com.example.registrohoras.domain.LoginValidator
import com.example.registrohoras.domain.ProveedorSocial
import com.example.registrohoras.domain.UsuarioDemo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado de la pantalla de login. La pantalla solo lo dibuja; las reglas viven en LoginValidator. */
data class LoginState(
    val correo: String = "",
    val clave: String = "",
    val verClave: Boolean = false,
    val errorCorreo: String? = null,
    val errorClave: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false,
    /** Proveedor cuya página oficial se abrió; al volver a la app se ofrece elegir una cuenta de prueba. */
    val proveedorPendiente: ProveedorSocial? = null,
    val elegirCuentaDe: ProveedorSocial? = null
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {
    private val auth = (app as RegistroApp).auth

    private val _estado = MutableStateFlow(LoginState())
    val estado: StateFlow<LoginState> = _estado.asStateFlow()

    /** Usuarios de demostración para completar las credenciales con un toque. */
    val demos: List<UsuarioDemo> = SemillaDemo.usuarios

    fun onCorreo(texto: String) = _estado.update { it.copy(correo = texto, errorCorreo = null, errorGeneral = null) }
    fun onClave(texto: String) = _estado.update { it.copy(clave = texto, errorClave = null, errorGeneral = null) }
    fun alternarClave() = _estado.update { it.copy(verClave = !it.verClave) }

    fun usarDemo(demo: UsuarioDemo) = _estado.update {
        it.copy(correo = demo.correo, clave = demo.clave, errorCorreo = null, errorClave = null, errorGeneral = null)
    }

    // ---------- Inicio con Google / Apple / Facebook (modo demostración) ----------
    fun abrirProveedor(proveedor: ProveedorSocial) =
        _estado.update { it.copy(proveedorPendiente = proveedor, errorGeneral = null) }

    fun noSePudoAbrir() =
        _estado.update { it.copy(proveedorPendiente = null, errorGeneral = "No se pudo abrir el navegador.") }

    /** Al volver del navegador se pide elegir con qué cuenta de prueba continuar. */
    fun alVolverAlApp() = _estado.update { actual ->
        if (actual.proveedorPendiente != null) actual.copy(proveedorPendiente = null, elegirCuentaDe = actual.proveedorPendiente) else actual
    }

    fun cancelarCuentaDemo() = _estado.update { it.copy(elegirCuentaDe = null) }

    fun entrarConCuentaDemo(demo: UsuarioDemo) {
        viewModelScope.launch {
            auth.iniciarSesionDemo(demo.id)
            _estado.update { LoginState() }
        }
    }

    fun iniciarSesion() {
        val actual = _estado.value
        if (actual.cargando) return

        val errorCorreo = LoginValidator.validarCorreo(actual.correo)
        val errorClave = LoginValidator.validarClave(actual.clave)
        if (errorCorreo != null || errorClave != null) {
            _estado.update { it.copy(errorCorreo = errorCorreo, errorClave = errorClave) }
            return
        }

        _estado.update { it.copy(cargando = true, errorGeneral = null) }
        viewModelScope.launch {
            val correcto = auth.iniciarSesion(actual.correo, actual.clave)
            // Si fue correcto, SesionViewModel cambia a la app; aquí solo se limpia el formulario.
            _estado.update {
                if (correcto) LoginState() else it.copy(cargando = false, errorGeneral = "Correo o contraseña incorrectos.")
            }
        }
    }
}
