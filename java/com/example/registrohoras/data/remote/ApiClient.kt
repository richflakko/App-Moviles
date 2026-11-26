package com.example.registrohoras.data.remote

import com.example.registrohoras.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Crea los clientes Retrofit. La URL del microservicio sale de BuildConfig (ver README). */
object ApiClient {

    /** Fábrica reutilizable (también la usan las pruebas con MockWebServer). */
    fun <T> crear(baseUrl: String, servicio: Class<T>, cliente: OkHttpClient = clienteHttp()): T =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(servicio)

    private fun clienteHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
            })
            .build()

    val registros: RegistroApi by lazy { crear(BuildConfig.API_BASE_URL, RegistroApi::class.java) }
    val feriados: FeriadosApi by lazy { crear("https://date.nager.at/", FeriadosApi::class.java) }
}
