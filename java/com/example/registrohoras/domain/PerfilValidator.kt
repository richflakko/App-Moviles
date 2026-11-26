package com.example.registrohoras.domain

/** Reglas de validación de la edición de perfil. Kotlin puro (sin Android), probado con JUnit. */
object PerfilValidator {
    const val LARGO_MIN = 2
    const val LARGO_MAX_NOMBRE = 40
    const val LARGO_MAX_MOSTRAR = 20

    /** Nombre completo: obligatorio y distinto al de otras personas. */
    fun validarNombreCompleto(nombre: String, otros: List<String>): String? {
        val texto = FormularioValidator.normalizarNombre(nombre)
        return when {
            texto.isEmpty() -> "Ingresa tu nombre."
            texto.length < LARGO_MIN -> "Usa al menos $LARGO_MIN caracteres."
            texto.length > LARGO_MAX_NOMBRE -> "Máximo $LARGO_MAX_NOMBRE caracteres."
            otros.any { FormularioValidator.normalizarNombre(it).equals(texto, ignoreCase = true) } ->
                "Ya existe otra persona con ese nombre."
            else -> null
        }
    }

    /** Nombre con el que te saluda la app: es opcional (vacío = se usa tu primer nombre). */
    fun validarNombreMostrar(nombre: String): String? {
        val texto = FormularioValidator.normalizarNombre(nombre)
        return when {
            texto.isEmpty() -> null
            texto.length < LARGO_MIN -> "Usa al menos $LARGO_MIN caracteres."
            texto.length > LARGO_MAX_MOSTRAR -> "Máximo $LARGO_MAX_MOSTRAR caracteres."
            else -> null
        }
    }
}
