package ar.utn.ba.ddsi.models.dtos.output;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class HechoOutputDTO {
  private long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String provincia;
  private LocalDate fechaAcontecimiento;
  private LocalTime horaAcontecimiento;
  private LocalDate fechaCarga;
  private Set<Long> idEtiquetas;
  private List<Long> idContenidoMultimedia;
  private String fuenteExterna;
  private Boolean fueEliminado;

  public HechoOutputDTO(Hecho hecho) {
    //this.id = hecho.getId();
    this.titulo = hecho.getTitulo();
    this.descripcion = hecho.getDescripcion();
    this.categoria = hecho.getCategoria().getNombre();
    this.latitud = hecho.getUbicacion().getLatitud();
    this.longitud = hecho.getUbicacion().getLongitud();
    this.provincia = hecho.getUbicacion().getProvincia();
    this.fechaAcontecimiento = hecho.getFechaAcontecimiento();
    this.horaAcontecimiento = hecho.getHoraAcontecimiento();
    this.fechaCarga = hecho.getFechaCarga();
    this.fuenteExterna = null;
    this.idEtiquetas = null;
    this.idContenidoMultimedia = null;

  }

  //Pasar de DTO a entidad
  public Hecho toHecho() {
    return new Hecho(
        this.titulo,
        this.descripcion,
        new Categoria(this.categoria),
        new Ubicacion(this.latitud, this.longitud),
        this.fechaAcontecimiento,
        this.fechaCarga,
        Origen.PROVISTO_POR_CONTRIBUYENTE, //porque viene de dinamica
        this.fuenteExterna
        );
  }

  //Pasar de entidad a DTO
  public static HechoOutputDTO fromEntity(Hecho hecho) {
    return new HechoOutputDTO(hecho);
  }
}