package com.example.registrohoras.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/** Manejo de los archivos de foto (cámara) usando FileProvider: nunca se expone una ruta file://. */
object FotoHelper {
    fun crearArchivo(context: Context): File {
        val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
        return File(carpeta, "registro_${System.currentTimeMillis()}.jpg").apply { createNewFile() }
    }

    fun uriDe(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    fun borrar(ruta: String) {
        runCatching { File(ruta).delete() }
    }
}
