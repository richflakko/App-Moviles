package com.example.registrohoras.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.FileOutputStream

/**
 * Guarda la foto de perfil elegida: la copia al almacenamiento privado de la app,
 * recortada en cuadrado y reducida (así pesa poco y no depende de la galería).
 */
object PerfilFotoHelper {
    private const val LADO = 512
    private const val MAXIMO_DECODIFICADO = 1024

    /** Devuelve la ruta del archivo guardado, o null si no se pudo leer la imagen. */
    fun guardar(context: Context, uri: Uri, usuarioId: String): String? = runCatching {
        val cuadrada = recortarCuadrado(decodificar(context, uri))
        val carpeta = File(context.filesDir, "perfil").apply { mkdirs() }
        // El nombre cambia cada vez para que la imagen se refresque en pantalla.
        val archivo = File(carpeta, "${usuarioId}_${System.currentTimeMillis()}.jpg")
        FileOutputStream(archivo).use { cuadrada.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        archivo.absolutePath
    }.getOrNull()

    fun borrar(ruta: String?) {
        ruta?.let { File(it).delete() }
    }

    private fun decodificar(context: Context, uri: Uri): Bitmap =
        if (Build.VERSION.SDK_INT >= 28) {
            val fuente = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(fuente) { decoder, info, _ ->
                val mayor = maxOf(info.size.width, info.size.height)
                if (mayor > MAXIMO_DECODIFICADO) {
                    val factor = MAXIMO_DECODIFICADO.toFloat() / mayor
                    decoder.setTargetSize(
                        (info.size.width * factor).toInt().coerceAtLeast(1),
                        (info.size.height * factor).toInt().coerceAtLeast(1)
                    )
                }
                decoder.setAllocator(ImageDecoder.ALLOCATOR_SOFTWARE)
            }
        } else {
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
                ?: error("No se pudo leer la imagen")
        }

    private fun recortarCuadrado(origen: Bitmap): Bitmap {
        val lado = minOf(origen.width, origen.height)
        val cuadrado = Bitmap.createBitmap(origen, (origen.width - lado) / 2, (origen.height - lado) / 2, lado, lado)
        return if (lado > LADO) Bitmap.createScaledBitmap(cuadrado, LADO, LADO, true) else cuadrado
    }
}
