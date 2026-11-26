package com.example.registrohoras.data

import com.example.registrohoras.data.local.RegistroEntity
import com.example.registrohoras.data.remote.ApiClient
import com.example.registrohoras.data.remote.FeriadosApi
import com.example.registrohoras.data.remote.FeriadosRepository
import com.example.registrohoras.data.remote.RegistroApi
import com.example.registrohoras.data.remote.aDtoRemoto
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

/** Pruebas de la capa Retrofit contra un servidor falso (no necesitan internet ni el backend). */
class RemotoTest {
    private lateinit var servidor: MockWebServer

    @Before fun iniciar() { servidor = MockWebServer().also { it.start() } }
    @After fun cerrar() { servidor.shutdown() }

    private fun url() = servidor.url("/").toString()

    @Test
    fun registro_se_convierte_a_dto_con_los_nombres_del_catalogo() {
        val r = RegistroEntity(id = 7, usuarioId = "ana", proyectoId = "afc", fecha = "2026-11-23", minutos = 90, tarea = "Revisión")
        val dto = r.aDtoRemoto("dev-1", "Ana Pérez", "Portal Cliente AFC")
        assertEquals(7L, dto.idLocal)
        assertEquals("dev-1", dto.dispositivo)
        assertEquals("Ana Pérez", dto.usuarioNombre)
        assertEquals(90, dto.minutos)
    }

    @Test
    fun crear_envia_POST_con_json_y_lee_la_respuesta() = runBlocking {
        servidor.enqueue(
            MockResponse().setResponseCode(201).setHeader("Content-Type", "application/json").setBody(
                """{"id":1,"dispositivo":"d","idLocal":7,"usuarioId":"ana","usuarioNombre":"Ana","proyectoId":"afc","proyectoNombre":"AFC","fecha":"2026-11-23","minutos":90,"tarea":"Revisión"}"""
            )
        )
        val api = ApiClient.crear(url(), RegistroApi::class.java)
        val dto = RegistroEntity(id = 7, usuarioId = "ana", proyectoId = "afc", fecha = "2026-11-23", minutos = 90, tarea = "Revisión")
            .aDtoRemoto("d", "Ana", "AFC")

        val respuesta = api.crear(dto)

        assertTrue(respuesta.isSuccessful)
        assertEquals(1L, respuesta.body()?.id)
        val pedido = servidor.takeRequest()
        assertEquals("POST", pedido.method)
        assertEquals("/api/registros", pedido.path)
        assertTrue(pedido.body.readUtf8().contains("\"minutos\":90"))
    }

    @Test
    fun eliminar_usa_DELETE_con_dispositivo_e_id() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(204))
        ApiClient.crear(url(), RegistroApi::class.java).eliminar("d", 7)
        val pedido = servidor.takeRequest()
        assertEquals("DELETE", pedido.method)
        assertEquals("/api/registros/d/7", pedido.path)
    }

    @Test
    fun feriados_detecta_un_feriado_y_una_fecha_normal() = runBlocking {
        servidor.enqueue(
            MockResponse().setHeader("Content-Type", "application/json").setBody(
                """[{"date":"2026-12-25","localName":"Navidad","name":"Christmas Day"}]"""
            )
        )
        val repo = FeriadosRepository(ApiClient.crear(url(), FeriadosApi::class.java))
        assertEquals("Navidad", repo.nombreFeriado(LocalDate.of(2026, 12, 25)))
        // Segunda consulta del mismo año: sale de la memoria, no del servidor.
        assertNull(repo.nombreFeriado(LocalDate.of(2026, 12, 26)))
        assertEquals(1, servidor.requestCount)
    }

    @Test
    fun feriados_devuelve_null_si_el_servidor_falla() = runBlocking {
        servidor.enqueue(MockResponse().setResponseCode(500))
        val repo = FeriadosRepository(ApiClient.crear(url(), FeriadosApi::class.java))
        assertNull(repo.nombreFeriado(LocalDate.of(2026, 12, 25)))
    }
}
