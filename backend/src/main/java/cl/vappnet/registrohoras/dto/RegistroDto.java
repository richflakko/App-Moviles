package cl.vappnet.registrohoras.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/** Cuerpo JSON que envía y recibe la app (mismos nombres que la entidad Room). */
public record RegistroDto(
        Long id,
        @NotBlank String dispositivo,
        @NotNull Long idLocal,
        @NotBlank String usuarioId,
        @NotBlank String usuarioNombre,
        @NotBlank String proyectoId,
        @NotBlank String proyectoNombre,
        @NotNull @PastOrPresent LocalDate fecha,
        @Min(1) @Max(1440) int minutos,
        @NotBlank @Size(min = 3, max = 80) String tarea,
        Double latitud,
        Double longitud
) {}
