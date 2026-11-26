package com.example.registrohoras.data

import com.example.registrohoras.data.local.ProyectoDao
import com.example.registrohoras.data.local.ProyectoEntity
import com.example.registrohoras.data.local.UsuarioDao
import com.example.registrohoras.data.local.UsuarioEntity
import com.example.registrohoras.domain.FormularioValidator
import com.example.registrohoras.domain.Proyecto
import com.example.registrohoras.domain.Usuario
import com.example.registrohoras.util.ClaveHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.UUID

/** Acceso a los usuarios y proyectos guardados en la base de datos. */
class CatalogoRepository(
    private val usuarioDao: UsuarioDao,
    private val proyectoDao: ProyectoDao
) {
    private val mutex = Mutex()

    val usuarios: Flow<List<Usuario>> =
        usuarioDao.observarTodos().map { lista -> lista.map { Usuario(it.id, it.nombre) } }

    val proyectos: Flow<List<Proyecto>> =
        proyectoDao.observarTodos().map { lista -> lista.map { Proyecto(it.id, it.nombre) } }

    suspend fun proyectosActuales(): List<Proyecto> =
        proyectoDao.todos().map { Proyecto(it.id, it.nombre) }

    /** Agrega una persona sin cuenta (opción "Otro"). Si ya existe una con ese nombre, devuelve la existente. */
    suspend fun agregarUsuario(nombre: String): String = mutex.withLock {
        val limpio = FormularioValidator.normalizarNombre(nombre)
        val existente = usuarioDao.todos().firstOrNull { it.nombre.equals(limpio, ignoreCase = true) }
        if (existente != null) {
            existente.id
        } else {
            val id = "usr_${UUID.randomUUID()}"
            usuarioDao.insertar(UsuarioEntity(id = id, nombre = limpio))
            id
        }
    }

    /** Agrega un proyecto nuevo (opción "Otro"). Si ya existe uno con ese nombre, devuelve el existente. */
    suspend fun agregarProyecto(nombre: String): String = mutex.withLock {
        val limpio = FormularioValidator.normalizarNombre(nombre)
        val existente = proyectoDao.todos().firstOrNull { it.nombre.equals(limpio, ignoreCase = true) }
        if (existente != null) {
            existente.id
        } else {
            val id = "proy_${UUID.randomUUID()}"
            proyectoDao.insertar(ProyectoEntity(id = id, nombre = limpio))
            id
        }
    }

    /** Crea los datos de demostración solo si las tablas están vacías (primer arranque o tras migrar). */
    suspend fun asegurarDatosIniciales() {
        mutex.withLock {
            if (proyectoDao.contar() == 0) {
                SemillaDemo.proyectos.forEach { proyectoDao.insertar(ProyectoEntity(it.id, it.nombre)) }
            }
            if (usuarioDao.contar() == 0) {
                val usuarios = withContext(Dispatchers.Default) {
                    SemillaDemo.usuarios.map { demo ->
                        val sal = ClaveHasher.nuevaSal()
                        UsuarioEntity(demo.id, demo.nombre, demo.correo, ClaveHasher.hash(demo.clave, sal), sal)
                    }
                }
                usuarios.forEach { usuarioDao.insertar(it) }
            }
        }
    }
}
