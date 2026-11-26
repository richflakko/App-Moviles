package com.example.registrohoras.data

import com.example.registrohoras.data.local.RegistroDao
import com.example.registrohoras.data.local.RegistroEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Única puerta de acceso a los datos: los ViewModels no conocen Room directamente. */
class RegistroRepository(private val dao: RegistroDao) {

    val registros: Flow<List<RegistroEntity>> = dao.observarTodos()

    /** Minutos acumulados por id de proyecto. */
    val totales: Flow<Map<String, Long>> =
        dao.observarTotales().map { lista -> lista.associate { it.proyectoId to it.total } }

    /** Cantidad de registros que aún no llegan al servidor. */
    val pendientes: Flow<Int> = dao.observarPendientes()

    suspend fun guardar(registro: RegistroEntity): Long = dao.insertar(registro)

    suspend fun eliminar(registro: RegistroEntity) = dao.eliminar(registro)

    suspend fun totalesActuales(): Map<String, Long> =
        dao.totales().associate { it.proyectoId to it.total }
}
