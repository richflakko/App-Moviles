package cl.vappnet.registrohoras;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class RegistroControllerTest {

    @Autowired
    MockMvc mvc;

    private String json(String dispositivo, long idLocal, int minutos, String tarea) {
        return """
                {"dispositivo":"%s","idLocal":%d,"usuarioId":"ana","usuarioNombre":"Ana Pérez",
                 "proyectoId":"afc","proyectoNombre":"Portal Cliente AFC","fecha":"%s",
                 "minutos":%d,"tarea":"%s"}
                """.formatted(dispositivo, idLocal, LocalDate.now(), minutos, tarea);
    }

    @Test
    void health_responde_ok() throws Exception {
        mvc.perform(get("/api/health")).andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("ok"));
    }

    @Test
    void crear_registro_valido_devuelve_201_y_reenviar_no_duplica() throws Exception {
        mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content(json("d1", 1, 90, "Revisión")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.minutos").value(90));
        // Mismo (dispositivo, idLocal): se actualiza, no se crea otro.
        mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content(json("d1", 1, 120, "Revisión")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.minutos").value(120));
        mvc.perform(get("/api/registros").param("proyectoId", "afc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void rechaza_minutos_fuera_de_rango_y_tarea_corta() throws Exception {
        mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content(json("d2", 2, 0, "Revisión")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content(json("d2", 3, 30, "ab")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.tarea").exists());
    }

    @Test
    void resumen_suma_minutos_por_proyecto() throws Exception {
        mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content(json("d3", 10, 60, "Tarea uno")))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalMinutos").isNumber())
                .andExpect(jsonPath("$.proyectos[0].proyectoId").exists());
    }

    @Test
    void eliminar_existente_204_e_inexistente_404() throws Exception {
        mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content(json("d4", 20, 45, "Para borrar")))
                .andExpect(status().isCreated());
        mvc.perform(delete("/api/registros/d4/20")).andExpect(status().isNoContent());
        mvc.perform(delete("/api/registros/d4/20")).andExpect(status().isNotFound());
    }
}
