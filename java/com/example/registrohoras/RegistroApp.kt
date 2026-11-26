package com.example.registrohoras

import android.app.Application
import com.example.registrohoras.data.AuthRepository
import com.example.registrohoras.data.CatalogoRepository
import com.example.registrohoras.data.PerfilRepository
import com.example.registrohoras.data.RegistroRepository
import com.example.registrohoras.data.SesionManager
import com.example.registrohoras.data.TemaManager
import com.example.registrohoras.data.local.AppDatabase
import com.example.registrohoras.data.remote.ApiClient
import com.example.registrohoras.data.remote.DispositivoId
import com.example.registrohoras.data.remote.FeriadosRepository
import com.example.registrohoras.data.remote.SyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Punto único de creación de la base de datos, los repositorios y la sesión (lo usan ViewModels y widget). */
class RegistroApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.crear(this) }
    val repository: RegistroRepository by lazy { RegistroRepository(database.registroDao()) }
    val catalogo: CatalogoRepository by lazy { CatalogoRepository(database.usuarioDao(), database.proyectoDao()) }
    val perfiles: PerfilRepository by lazy { PerfilRepository(database.usuarioDao()) }
    val sesion: SesionManager by lazy { SesionManager(this) }
    val tema: TemaManager by lazy { TemaManager(this) }
    val auth: AuthRepository by lazy { AuthRepository(database.usuarioDao(), sesion) }

    /** Ámbito para trabajo que debe terminar aunque la pantalla se cierre (p. ej. enviar un registro al servidor). */
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val sync: SyncRepository by lazy {
        SyncRepository(database.registroDao(), database.usuarioDao(), database.proyectoDao(), ApiClient.registros, DispositivoId.obtener(this))
    }
    val feriados: FeriadosRepository by lazy { FeriadosRepository(ApiClient.feriados) }

    override fun onCreate() {
        super.onCreate()
        // Primer arranque: crea los usuarios y proyectos de demostración.
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            catalogo.asegurarDatosIniciales()
            // Envía lo que haya quedado pendiente (si no hay conexión, se reintenta la próxima vez).
            sync.sincronizarPendientes()
        }
    }
}
