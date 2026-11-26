package com.example.registrohoras.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Hash de contraseñas con PBKDF2 + sal aleatoria. Kotlin/Java puro: se puede probar con JUnit. */
object ClaveHasher {
    private const val ITERACIONES = 120_000
    private const val BITS_CLAVE = 256

    fun nuevaSal(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hash(clave: String, sal: String): String {
        val especificacion = PBEKeySpec(clave.toCharArray(), Base64.getDecoder().decode(sal), ITERACIONES, BITS_CLAVE)
        try {
            val bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(especificacion).encoded
            return Base64.getEncoder().encodeToString(bytes)
        } finally {
            especificacion.clearPassword()
        }
    }

    /** Comparación en tiempo constante para no filtrar información por tiempos de respuesta. */
    fun verificar(clave: String, sal: String, hashEsperado: String): Boolean =
        MessageDigest.isEqual(hash(clave, sal).toByteArray(), hashEsperado.toByteArray())
}
