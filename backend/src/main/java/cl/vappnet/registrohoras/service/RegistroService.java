package cl.vappnet.registrohoras.service;

import cl.vappnet.registrohoras.dto.RegistroDto;
import cl.vappnet.registrohoras.dto.ResumenDto;
import cl.vappnet.registrohoras.model.RegistroRemoto;
import cl.vappnet.registrohoras.repository.RegistroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistroService {

    private final RegistroRepository repo;

    public RegistroService(RegistroRepository repo) {
        this.repo = repo;
    }

    /** Resultado de guardar: el registro y si fue creado (true) o ya existía y se actualizó (false). */
    public record Guardado(RegistroDto registro, boolean creado) {}

    /** Idempotente: el mismo (dispositivo, idLocal) actualiza en vez de duplicar. */
    @Transactional
    public Guardado guardar(RegistroDto dto) {
        var existente = repo.findByDispositivoAndIdLocal(dto.dispositivo(), dto.idLocal());
        boolean creado = existente.isEmpty();
        RegistroRemoto r = existente.orElseGet(RegistroRemoto::new);
        r.setDispositivo(dto.dispositivo());
        r.setIdLocal(dto.idLocal());
        r.setUsuarioId(dto.usuarioId());
        r.setUsuarioNombre(dto.usuarioNombre());
        r.setProyectoId(dto.proyectoId());
        r.setProyectoNombre(dto.proyectoNombre());
        r.setFecha(dto.fecha());
        r.setMinutos(dto.minutos());
        r.setTarea(dto.tarea().trim());
        r.setLatitud(dto.latitud());
        r.setLongitud(dto.longitud());
        return new Guardado(aDto(repo.save(r)), creado);
    }

    @Transactional(readOnly = true)
    public List<RegistroDto> listar(String proyectoId) {
        var lista = (proyectoId == null || proyectoId.isBlank())
                ? repo.findAllByOrderByFechaDescIdDesc()
                : repo.findByProyectoIdOrderByFechaDescIdDesc(proyectoId);
        return lista.stream().map(this::aDto).toList();
    }

    /** @return true si existía y se eliminó. */
    @Transactional
    public boolean eliminar(String dispositivo, Long idLocal) {
        var existente = repo.findByDispositivoAndIdLocal(dispositivo, idLocal);
        existente.ifPresent(repo::delete);
        return existente.isPresent();
    }

    @Transactional(readOnly = true)
    public ResumenDto resumen() {
        var filas = repo.totalesPorProyecto().stream()
                .map(f -> new ResumenDto.ProyectoTotal((String) f[0], (String) f[1], ((Number) f[2]).longValue()))
                .toList();
        long total = filas.stream().mapToLong(ResumenDto.ProyectoTotal::minutos).sum();
        return new ResumenDto(total, filas);
    }

    private RegistroDto aDto(RegistroRemoto r) {
        return new RegistroDto(r.getId(), r.getDispositivo(), r.getIdLocal(), r.getUsuarioId(), r.getUsuarioNombre(),
                r.getProyectoId(), r.getProyectoNombre(), r.getFecha(), r.getMinutos(), r.getTarea(),
                r.getLatitud(), r.getLongitud());
    }
}
