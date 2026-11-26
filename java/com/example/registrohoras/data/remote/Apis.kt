package com.example.registrohoras.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** API REST propia (microservicio Spring Boot de la carpeta backend/). */
interface RegistroApi {
    @POST("api/registros")
    suspend fun crear(@Body registro: RegistroRemotoDto): Response<RegistroRemotoDto>

    @DELETE("api/registros/{dispositivo}/{idLocal}")
    suspend fun eliminar(@Path("dispositivo") dispositivo: String, @Path("idLocal") idLocal: Long): Response<Unit>

    @GET("api/resumen")
    suspend fun resumen(): Response<ResumenRemotoDto>
}

/** API externa pública (Nager.Date, sin llave): feriados por país y año. */
interface FeriadosApi {
    @GET("api/v3/PublicHolidays/{anio}/CL")
    suspend fun feriados(@Path("anio") anio: Int): List<FeriadoDto>
}
