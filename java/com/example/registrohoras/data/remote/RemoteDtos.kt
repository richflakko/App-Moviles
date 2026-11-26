package com.example.registrohoras.data.remote

import com.example.registrohoras.data.local.RegistroEntity

/** Cuerpo JSON que se envía al microservicio Spring Boot (mismos nombres que su RegistroDto). */
data class RegistroRemotoDto(
    val id: Long? = null,
    val dispositivo: String,
    val idLocal: Long,
    val usuarioId: String,
    val usuarioNombre: String,
    val proyectoId: String,
    val proyectoNombre: String,
    /** Fecha ISO (yyyy-MM-dd). */
    val fecha: String,
    val minutos: Int,
    val tarea: String,
    val latitud: Double? = null,
    val longitud: Double? = null
)

data class ProyectoTotalDto(val proyectoId: String, val nombre: String, val minutos: Long)

data class ResumenRemotoDto(val totalMinutos: Long, val proyectos: List<ProyectoTotalDto>)

/** Feriado devuelto por la API pública Nager.Date. */
data class FeriadoDto(val date: String, val localName: String, val name: String)

/** Convierte un registro de Room en el cuerpo que espera el servidor. */
fun RegistroEntity.aDtoRemoto(dispositivo: String, usuarioNombre: String, proyectoNombre: String) =
    RegistroRemotoDto(
        dispositivo = dispositivo,
        idLocal = id,
        usuarioId = usuarioId,
        usuarioNombre = usuarioNombre,
        proyectoId = proyectoId,
        proyectoNombre = proyectoNombre,
        fecha = fecha,
        minutos = minutos,
        tarea = tarea,
        latitud = latitud,
        longitud = longitud
    )
