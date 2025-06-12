package ar.utn.ba.ddsi.models.dtos.output;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
public class HechoOutputDTO {
  private long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaAcontecimiento;
  private LocalDate fechaCarga;
  private Set<Long> idEtiquetas;
  private List<Long> idContenidoMultimedia;
  private String fuenteExterna;

  public HechoOutputDTO(Hecho hecho) {
    this.id = 0; // o poné algún valor si tenés ID en Hecho
    this.titulo = hecho.getTitulo();
    this.descripcion = hecho.getDescripcion();
    this.categoria = hecho.getCategoria().getNombre(); // si tenés getNombre()
    this.latitud = hecho.getUbicacion().getLatitud();
    this.longitud = hecho.getUbicacion().getLongitud();
    this.fechaAcontecimiento = hecho.getFechaAcontecimiento();
    this.fechaCarga = hecho.getFechaCarga();
    this.fuenteExterna = null; // o lo que corresponda
    this.idEtiquetas = null;
    this.idContenidoMultimedia = null;
  }

  public Hecho toHecho() {
    return new Hecho(
        this.titulo,
        this.descripcion,
        new Categoria(this.categoria),
        new Ubicacion(this.latitud, this.longitud),
        this.fechaAcontecimiento,
        Origen.PROVISTO_POR_CONTRIBUYENTE
        );
  }
}