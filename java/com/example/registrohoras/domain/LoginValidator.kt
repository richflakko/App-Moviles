package com.example.registrohoras.domain

/** Reglas de validación del inicio de sesión. Kotlin puro (sin Android), probado con JUnit. */
object LoginValidator {
    const val LARGO_MIN_CLAVE = 6
    private val CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validarCorreo(correo: String): String? {
        val texto = correo.trim()
        return when {
            texto.isEmpty() -> "Ingresa tu correo."
            !CORREO.matches(texto) -> "Ingresa un correo válido."
            else -> null
        }
    }

    fun validarClave(clave: String): String? = when {
        clave.isEmpty() -> "Ingresa tu contraseña."
        clave.length < LARGO_MIN_CLAVE -> "La contraseña debe tener al menos $LARGO_MIN_CLAVE caracteres."
        else -> null
    }
}
