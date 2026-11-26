package com.example.registrohoras.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Persona registrada en la app. Si tiene cuenta, guarda correo y el hash de su contraseña (nunca la contraseña).
 * Las personas agregadas con la opción "Otro" no tienen cuenta: correo, claveHash y salt son null.
 */
@Entity(tableName = "usuarios", indices = [Index(value = ["correo"], unique = true)])
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val correo: String? = null,
    val claveHash: String? = null,
    val salt: String? = null,
    /** Cómo quiere la persona que la app la llame (opcional). */
    val nombreMostrar: String? = null,
    /** Ruta de la foto de perfil guardada en el almacenamiento privado de la app. */
    val fotoPath: String? = null
)
