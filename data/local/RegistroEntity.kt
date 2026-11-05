package com.example.registrohoras.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros")
data class RegistroEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: String,
    val proyectoId: String,
    /** Fecha en formato ISO (yyyy-MM-dd). */
    val fecha: String,
    val minutos: Int,
    val tarea: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val fotoPath: String? = null
)

/** Resultado de la consulta que suma los minutos por proyecto. */
data class TotalProyecto(val proyectoId: String, val total: Long)
