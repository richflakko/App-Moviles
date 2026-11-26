package com.example.registrohoras.data.remote

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

/** Consulta la API externa de feriados (Nager.Date) y guarda el año consultado para no repetir la llamada. */
class FeriadosRepository(private val api: FeriadosApi) {
    private val cache = ConcurrentHashMap<Int, List<FeriadoDto>>()

    /** Nombre del feriado en Chile para esa fecha, o null si no es feriado o no se pudo consultar. */
    suspend fun nombreFeriado(fecha: LocalDate): String? {
        val lista = cache[fecha.year] ?: try {
            api.feriados(fecha.year).also { cache[fecha.year] = it }
        } catch (e: IOException) {
            return null
        } catch (e: HttpException) {
            return null
        } catch (e: JsonParseException) {
            return null
        }
        return lista.firstOrNull { it.date == fecha.toString() }?.localName
    }
}
