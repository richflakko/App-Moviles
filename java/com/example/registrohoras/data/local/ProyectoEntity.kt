package com.example.registrohoras.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "proyectos")
data class ProyectoEntity(
    @PrimaryKey val id: String,
    val nombre: String
)
