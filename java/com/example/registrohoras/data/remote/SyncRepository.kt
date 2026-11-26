package com.example.registrohoras.data.remote

import com.example.registrohoras.data.local.ProyectoDao
import com.example.registrohoras.data.local.RegistroDao
import com.example.registrohoras.data.local.RegistroEntity
import com.example.registrohoras.data.local.UsuarioDao
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException

data class ResultadoSync(val enviados: Int, val fallidos: Int, val sinConexion: Boolean)

/**
 * Sincroniza Room (fuente de verdad, funciona sin internet) con el microservicio.
 * Los registros nuevos quedan "pendientes" y se envían cuando hay conexión.
 */
class SyncRepository(
    private val registroDao: RegistroDao,
    private val usuarioDao: UsuarioDao,
    private val proyectoDao: ProyectoDao,
    private val api: RegistroApi,
    private val dispositivo: String
) {
    private val mutex = Mutex()

    /** Envía al servidor todos los registros pendientes. Se detiene si no hay conexión. */
    suspend fun sincronizarPendientes(): ResultadoSync = mutex.withLock {
        val pendientes = registroDao.pendientes()
        val usuarios = usuarioDao.todos().associateBy { it.id }
        val proyectos = proyectoDao.todos().associateBy { it.id }
        var enviados = 0
        var fallidos = 0
        var sinConexion = false
        for (registro in pendientes) {
            val dto = registro.aDtoRemoto(
                dispositivo = dispositivo,
                usuarioNombre = usuarios[registro.usuarioId]?.nombre ?: registro.usuarioId,
                proyectoNombre = proyectos[registro.proyectoId]?.nombre ?: registro.proyectoId
            )
            try {
                if (api.crear(dto).isSuccessful) {
                    registroDao.marcarSincronizado(registro.id)
                    enviados++
                } else {
                    fallidos++
                }
            } catch (e: IOException) {
                sinConexion = true
                fallidos = pendientes.size - enviados
                break
            }
        }
        ResultadoSync(enviados, fallidos, sinConexion)
    }

    /** Avisa al servidor que el registro se eliminó. Si no hay conexión, simplemente no se refleja allá. */
    suspend fun eliminarRemoto(registro: RegistroEntity) {
        try {
            api.eliminar(dispositivo, registro.id)
        } catch (e: IOException) {
            // Sin conexión: el servidor conserva su copia.
        }
    }

    /** Total de minutos que tiene el servidor, o null si no se pudo consultar. */
    suspend fun totalServidor(): Long? = try {
        val respuesta = api.resumen()
        if (respuesta.isSuccessful) respuesta.body()?.totalMinutos else null
    } catch (e: IOException) {
        null
    }
}
