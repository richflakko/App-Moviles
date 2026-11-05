package com.example.registrohoras.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermisosHelper {
    fun concedido(context: Context, permiso: String): Boolean =
        ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED
}
