package cl.vappnet.registrohoras.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Registro de horas recibido desde la app Android.
 * (dispositivo, idLocal) identifica de forma única el registro que nació en el teléfono,
 * así reenviarlo no crea duplicados.
 */
@Entity
@Table(name = "registros",
        uniqueConstraints = @UniqueConstraint(columnNames = {"dispositivo", "idLocal"}))
public class RegistroRemoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String dispositivo;

    @Column(nullable = false)
    private Long idLocal;

    @Column(nullable = false)
    private String usuarioId;

    @Column(nullable = false)
    private String usuarioNombre;

    @Column(nullable = false)
    private String proyectoId;

    @Column(nullable = false)
    private String proyectoNombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private int minutos;

    @Column(nullable = false, length = 80)
    private String tarea;

    private Double latitud;
    private Double longitud;

    public Long getId() { return id; }
    public String getDispositivo() { return dispositivo; }
    public void setDispositivo(String dispositivo) { this.dispositivo = dispositivo; }
    public Long getIdLocal() { return idLocal; }
    public void setIdLocal(Long idLocal) { this.idLocal = idLocal; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public String getProyectoId() { return proyectoId; }
    public void setProyectoId(String proyectoId) { this.proyectoId = proyectoId; }
    public String getProyectoNombre() { return proyectoNombre; }
    public void setProyectoNombre(String proyectoNombre) { this.proyectoNombre = proyectoNombre; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public int getMinutos() { return minutos; }
    public void setMinutos(int minutos) { this.minutos = minutos; }
    public String getTarea() { return tarea; }
    public void setTarea(String tarea) { this.tarea = tarea; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
}
