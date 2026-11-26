package com.example.registrohoras.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios ORDER BY rowid")
    fun observarTodos(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios ORDER BY rowid")
    suspend fun todos(): List<UsuarioEntity>

    @Query("SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(:correo) LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    fun observarPorId(id: String): Flow<UsuarioEntity?>

    @Query("UPDATE usuarios SET nombre = :nombre, nombreMostrar = :nombreMostrar WHERE id = :id")
    suspend fun actualizarNombres(id: String, nombre: String, nombreMostrar: String?)

    @Query("UPDATE usuarios SET fotoPath = :ruta WHERE id = :id")
    suspend fun actualizarFoto(id: String, ruta: String?)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}
