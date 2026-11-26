package cl.vappnet.registrohoras.dto;

import java.util.List;

public record ResumenDto(long totalMinutos, List<ProyectoTotal> proyectos) {
    public record ProyectoTotal(String proyectoId, String nombre, long minutos) {}
}
