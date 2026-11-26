package cl.vappnet.registrohoras.repository;

import cl.vappnet.registrohoras.model.RegistroRemoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RegistroRepository extends JpaRepository<RegistroRemoto, Long> {

    Optional<RegistroRemoto> findByDispositivoAndIdLocal(String dispositivo, Long idLocal);

    List<RegistroRemoto> findAllByOrderByFechaDescIdDesc();

    List<RegistroRemoto> findByProyectoIdOrderByFechaDescIdDesc(String proyectoId);

    /** Fila: [proyectoId, proyectoNombre, suma de minutos]. */
    @Query("select r.proyectoId, max(r.proyectoNombre), sum(r.minutos) from RegistroRemoto r " +
            "group by r.proyectoId order by sum(r.minutos) desc")
    List<Object[]> totalesPorProyecto();
}
