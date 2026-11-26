package com.example.registrohoras.domain

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Colores del widget con legibilidad garantizada. Kotlin puro (sin Android), probado con JUnit.
 *
 * Sea cual sea el color que elija la persona, [aplicar] lo adapta: evita el negro y el blanco puros,
 * elige texto blanco o tinta y, si hace falta, aclara u oscurece el fondo hasta lograr un contraste
 * mínimo de 4,5:1 (criterio WCAG AA para texto normal).
 */
object ColorWidget {
    const val CONTRASTE_MINIMO = 4.5
    private const val CONTRASTE_BOTON = 2.5

    val FONDO_PREDETERMINADO: Int = 0xFF17102F.toInt() // Nebula
    private val BLANCO: Int = 0xFFFFFFFF.toInt()
    private val TINTA: Int = 0xFF17102F.toInt()
    private val ELECTRIC: Int = 0xFF345CFF.toInt()
    private val ICE: Int = 0xFF36D8FF.toInt()

    /** Paleta sugerida: ya cumple el contraste, pero igual pasa por [aplicar]. */
    val PALETA: List<Pair<String, Int>> = listOf(
        "Nebula" to 0xFF17102F.toInt(),
        "Medianoche" to 0xFF0E1330.toInt(),
        "Eléctrico" to 0xFF345CFF.toInt(),
        "Violeta" to 0xFF5B3FD0.toInt(),
        "Índigo" to 0xFF2B2E8F.toInt(),
        "Océano" to 0xFF0A6C8F.toInt(),
        "Escarcha" to 0xFFF2F0FA.toInt(),
        "Hielo" to 0xFFCFF4FF.toInt()
    )

    data class Colores(
        val fondo: Int,
        val texto: Int,
        val textoSecundario: Int,
        val borde: Int,
        val boton: Int,
        val signoBoton: Int,
        /** true si hubo que cambiar el color elegido para que los datos se lean bien. */
        val ajustado: Boolean
    )

    fun aplicar(elegido: Int): Colores {
        val opaco = elegido or (0xFF shl 24)
        var (h, s, l) = rgbAHsl(opaco)

        // 1) Nunca negro ni blanco puros: se llevan a un tono de la marca (Nebula / Escarcha).
        //    Si el color ya es válido se conserva tal cual (sin recalcularlo, para no alterarlo por redondeo).
        var fondo = opaco
        if (l < 0.12) {
            h = 255.0; s = max(s, 0.45); l = 0.12
            fondo = hslARgb(h, s, l)
        } else if (l > 0.97) {
            h = 250.0; s = max(s, 0.45); l = 0.97
            fondo = hslARgb(h, s, l)
        }

        // 2) El texto (blanco o tinta) debe superar el contraste mínimo.
        var pasos = 0
        while (mejorContraste(fondo) < CONTRASTE_MINIMO && pasos < 60) {
            val oscurecer = contraste(fondo, BLANCO) >= contraste(fondo, TINTA)
            l = (l + if (oscurecer) -0.02 else 0.02).coerceIn(0.0, 1.0)
            fondo = hslARgb(h, s, l)
            pasos++
        }

        val texto = if (contraste(fondo, BLANCO) >= contraste(fondo, TINTA)) BLANCO else TINTA
        val suave = mezclar(texto, fondo, 0.28)
        val textoSecundario = if (contraste(suave, fondo) >= CONTRASTE_MINIMO) suave else texto
        val borde = mezclar(fondo, texto, 0.2)

        // Botón "+": Electric siempre que se distinga del fondo; si no, otro color de la marca.
        val candidatos = listOf(ELECTRIC, ICE, TINTA, BLANCO)
        val boton = candidatos.firstOrNull { contraste(it, fondo) >= CONTRASTE_BOTON }
            ?: candidatos.maxBy { contraste(it, fondo) }
        val signo = if (contraste(boton, BLANCO) >= contraste(boton, TINTA)) BLANCO else TINTA

        return Colores(fondo, texto, textoSecundario, borde, boton, signo, ajustado = fondo != opaco)
    }

    /** Color a partir de tono (0–360), saturación y luminosidad (0–1), para el selector libre. */
    fun desdeHsl(tono: Double, saturacion: Double, luminosidad: Double): Int =
        hslARgb(((tono % 360.0) + 360.0) % 360.0, saturacion.coerceIn(0.0, 1.0), luminosidad.coerceIn(0.0, 1.0))

    // ---------- Contraste (fórmula WCAG) ----------
    fun luminancia(argb: Int): Double {
        fun canal(valor: Int): Double {
            val c = valor / 255.0
            return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * canal((argb shr 16) and 0xFF) + 0.7152 * canal((argb shr 8) and 0xFF) + 0.0722 * canal(argb and 0xFF)
    }

    fun contraste(a: Int, b: Int): Double {
        val la = luminancia(a)
        val lb = luminancia(b)
        return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
    }

    private fun mejorContraste(fondo: Int): Double = max(contraste(fondo, BLANCO), contraste(fondo, TINTA))

    private fun mezclar(desde: Int, hacia: Int, t: Double): Int {
        fun canal(desplazamiento: Int): Int {
            val a = (desde shr desplazamiento) and 0xFF
            val b = (hacia shr desplazamiento) and 0xFF
            return (a * (1 - t) + b * t).roundToInt().coerceIn(0, 255)
        }
        return (0xFF shl 24) or (canal(16) shl 16) or (canal(8) shl 8) or canal(0)
    }

    // ---------- Conversión HSL <-> RGB ----------
    private fun rgbAHsl(argb: Int): Triple<Double, Double, Double> {
        val r = ((argb shr 16) and 0xFF) / 255.0
        val g = ((argb shr 8) and 0xFF) / 255.0
        val b = (argb and 0xFF) / 255.0
        val maximo = max(r, max(g, b))
        val minimo = min(r, min(g, b))
        val l = (maximo + minimo) / 2.0
        if (maximo == minimo) return Triple(0.0, 0.0, l)
        val d = maximo - minimo
        val s = if (l > 0.5) d / (2.0 - maximo - minimo) else d / (maximo + minimo)
        val h = when (maximo) {
            r -> (g - b) / d + (if (g < b) 6.0 else 0.0)
            g -> (b - r) / d + 2.0
            else -> (r - g) / d + 4.0
        } * 60.0
        return Triple(h, s, l)
    }

    private fun hslARgb(h: Double, s: Double, l: Double): Int {
        val c = (1.0 - abs(2.0 * l - 1.0)) * s
        val x = c * (1.0 - abs((h / 60.0) % 2.0 - 1.0))
        val m = l - c / 2.0
        val (r, g, b) = when {
            h < 60 -> Triple(c, x, 0.0)
            h < 120 -> Triple(x, c, 0.0)
            h < 180 -> Triple(0.0, c, x)
            h < 240 -> Triple(0.0, x, c)
            h < 300 -> Triple(x, 0.0, c)
            else -> Triple(c, 0.0, x)
        }
        fun a8(v: Double) = ((v + m) * 255.0).roundToInt().coerceIn(0, 255)
        return (0xFF shl 24) or (a8(r) shl 16) or (a8(g) shl 8) or a8(b)
    }
}
