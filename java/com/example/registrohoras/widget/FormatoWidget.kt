package com.example.registrohoras.widget

/** Horas redondeadas hacia abajo para espacios reducidos: "42 h" (o "30 min" si es menos de una hora). */
fun horasCortas(minutos: Long): String = if (minutos >= 60) "${minutos / 60} h" else "$minutos min"
