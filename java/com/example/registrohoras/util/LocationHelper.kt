package com.example.registrohoras.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import com.example.registrohoras.domain.Coordenadas
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

object LocationHelper {
    fun tienePermiso(context: Context): Boolean =
        PermisosHelper.concedido(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
            PermisosHelper.concedido(context, Manifest.permission.ACCESS_COARSE_LOCATION)

    /** Pide una sola lectura de ubicación. Devuelve null si no hay permiso, GPS apagado o falla. */
    @SuppressLint("MissingPermission") // se comprueba con tienePermiso() justo antes
    fun obtener(context: Context, alResultado: (Coordenadas?) -> Unit) {
        if (!tienePermiso(context)) {
            alResultado(null)
            return
        }
        LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { loc -> alResultado(loc?.let { Coordenadas(it.latitude, it.longitude) }) }
            .addOnFailureListener { alResultado(null) }
    }
}
