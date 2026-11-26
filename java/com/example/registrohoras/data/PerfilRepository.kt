package com.example.registrohoras.data

import com.example.registrohoras.data.local.UsuarioDao
import com.example.registrohoras.domain.Perfil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Lectura y edición del perfil de una persona (nombre, nombre para mostrar y foto). */
class PerfilRepository(private val usuarioDao: UsuarioDao) {

    fun observar(id: String): Flow<Perfil?> =
        usuarioDao.observarPorId(id).map { usuario ->
            usuario?.let { Perfil(it.id, it.nombre, it.correo, it.nombreMostrar, it.fotoPath) }
        }

    suspend fun actualizarNombres(id: String, nombre: String, nombreMostrar: String?) =
        usuarioDao.actualizarNombres(id, nombre, nombreMostrar)

    suspend fun actualizarFoto(id: String, ruta: String?) = usuarioDao.actualizarFoto(id, ruta)
}
