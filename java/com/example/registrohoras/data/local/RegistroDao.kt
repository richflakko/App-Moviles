package com.example.registrohoras.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {
    @Insert
    suspend fun insertar(registro: RegistroEntity): Long

    @Delete
    suspend fun eliminar(registro: RegistroEntity)

    @Query("SELECT * FROM registros ORDER BY id DESC")
    fun observarTodos(): Flow<List<RegistroEntity>>

    @Query("SELECT proyectoId, SUM(minutos) AS total FROM registros GROUP BY proyectoId")
    fun observarTotales(): Flow<List<TotalProyecto>>

    @Query("SELECT proyectoId, SUM(minutos) AS total FROM registros GROUP BY proyectoId")
    suspend fun totales(): List<TotalProyecto>

    @Query("SELECT * FROM registros WHERE sincronizado = 0 ORDER BY id")
    suspend fun pendientes(): List<RegistroEntity>

    @Query("SELECT COUNT(*) FROM registros WHERE sincronizado = 0")
    fun observarPendientes(): Flow<Int>

    @Query("UPDATE registros SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: Long)
}
