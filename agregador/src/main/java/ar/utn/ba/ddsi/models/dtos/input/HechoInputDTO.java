package ar.utn.ba.ddsi.models.dtos.input;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class HechoInputDTO {
  private long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaAcontecimiento;
  private LocalDate fechaCarga;
  //campos no obligatorios, pueden llegar como null
  private Set<Long> idEtiquetas;
  private List<Long> idContenidoMultimedia;
  private String fuenteExterna; //es el "tipo" de fuente proxy

  public HechoInputDTO(Hecho hecho) {
    this.id = 0; // o poné algún valor si tenés ID en Hecho
    this.titulo = hecho.getTitulo();
    this.descripcion = hecho.getDescripcion();
    this.categoria = hecho.getCategoria().getNombre(); // si tenés getNombre()
    this.latitud = hecho.getUbicacion().getLatitud();
    this.longitud = hecho.getUbicacion().getLongitud();
    this.fechaAcontecimiento = hecho.getFechaAcontecimiento();
    this.fechaCarga = hecho.getFechaCarga();
    this.fuenteExterna = null; // o lo que corresponda

    // Estos campos los podés dejar como null si no los usás
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
        this.fechaCarga,
        Origen.PROVENIENTE_DE_DATASET,
        this.fuenteExterna
        //TODO manejar todos los origenes y mover TipoFuenteExterna aca porque es solo de proxy
    );
  }
}