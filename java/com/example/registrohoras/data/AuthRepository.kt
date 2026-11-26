package com.example.registrohoras.data

import com.example.registrohoras.data.local.UsuarioDao
import com.example.registrohoras.util.ClaveHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

/** Inicio y cierre de sesión con credenciales locales. */
class AuthRepository(
    private val usuarioDao: UsuarioDao,
    private val sesion: SesionManager
) {
    /** Id del usuario con sesión iniciada, o null. */
    val usuarioId: StateFlow<String?> = sesion.usuarioId

    /** true si el correo y la contraseña son correctos (y deja la sesión iniciada). */
    suspend fun iniciarSesion(correo: String, clave: String): Boolean {
        val usuario = usuarioDao.buscarPorCorreo(correo.trim()) ?: return false
        // Las personas agregadas con "Otro" no tienen contraseña: no pueden iniciar sesión.
        val salt = usuario.salt ?: return false
        val claveHash = usuario.claveHash ?: return false
        val valido = withContext(Dispatchers.Default) {
            ClaveHasher.verificar(clave, salt, claveHash)
        }
        if (valido) sesion.iniciar(usuario.id)
        return valido
    }

    /**
     * SOLO DEMOSTRACIÓN: entra como una cuenta de prueba sin pedir contraseña, para simular el regreso
     * desde Google, Apple o Facebook. Con un proveedor real, aquí se validaría el token que él entrega.
     */
    suspend fun iniciarSesionDemo(usuarioId: String): Boolean {
        val usuario = usuarioDao.buscarPorId(usuarioId) ?: return false
        if (usuario.correo == null) return false // solo cuentas que pueden iniciar sesión
        sesion.iniciar(usuario.id)
        return true
    }

    fun cerrarSesion() = sesion.cerrar()
}
