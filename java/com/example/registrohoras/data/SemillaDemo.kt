package com.example.registrohoras.data

import com.example.registrohoras.domain.Proyecto
import com.example.registrohoras.domain.UsuarioDemo

/**
 * Datos de demostración que se crean la primera vez que se abre la app.
 * Los ids coinciden con los registros ya guardados antes de existir las tablas.
 * IMPORTANTE: son credenciales de prueba; se reemplazan o eliminan antes de una versión real.
 */
object SemillaDemo {
    val usuarios = listOf(
        UsuarioDemo(id = "ana", nombre = "Ana Pérez", correo = "ana@vappnet.cl", clave = "Ana#2026"),
        UsuarioDemo(id = "luis", nombre = "Luis Gómez", correo = "luis@vappnet.cl", clave = "Luis#2026")
    )
    val proyectos = listOf(
        Proyecto("afc", "Portal Cliente AFC"),
        Proyecto("intranet", "Intranet Deepsource"),
        Proyecto("soporte", "Soporte operaciones")
    )
}
