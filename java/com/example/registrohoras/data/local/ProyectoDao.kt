package com.example.registrohoras.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(proyecto: ProyectoEntity): Long

    @Query("SELECT * FROM proyectos ORDER BY rowid")
    fun observarTodos(): Flow<List<ProyectoEntity>>

    @Query("SELECT * FROM proyectos ORDER BY rowid")
    suspend fun todos(): List<ProyectoEntity>

    @Query("SELECT COUNT(*) FROM proyectos")
    suspend fun contar(): Int
}
