package cl.vappnet.registrohoras.controller;

import cl.vappnet.registrohoras.dto.RegistroDto;
import cl.vappnet.registrohoras.dto.ResumenDto;
import cl.vappnet.registrohoras.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegistroController {

    private final RegistroService servicio;

    public RegistroController(RegistroService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("estado", "ok");
    }

    @GetMapping("/registros")
    public List<RegistroDto> listar(@RequestParam(required = false) String proyectoId) {
        return servicio.listar(proyectoId);
    }

    @PostMapping("/registros")
    public ResponseEntity<RegistroDto> crear(@Valid @RequestBody RegistroDto dto) {
        var resultado = servicio.guardar(dto);
        return ResponseEntity.status(resultado.creado() ? HttpStatus.CREATED : HttpStatus.OK)
                .body(resultado.registro());
    }

    @DeleteMapping("/registros/{dispositivo}/{idLocal}")
    public ResponseEntity<Void> eliminar(@PathVariable String dispositivo, @PathVariable Long idLocal) {
        return servicio.eliminar(dispositivo, idLocal)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/resumen")
    public ResumenDto resumen() {
        return servicio.resumen();
    }
}
