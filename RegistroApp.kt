package com.example.registrohoras

import android.app.Application
import com.example.registrohoras.data.RegistroRepository
import com.example.registrohoras.data.local.AppDatabase

/** Punto único de creación de la base de datos y el repositorio (lo usan ViewModels y widget). */
class RegistroApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.crear(this) }
    val repository: RegistroRepository by lazy { RegistroRepository(database.registroDao()) }
}
