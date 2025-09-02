package ar.utn.ba.ddsi.models.dtos.output;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Contribuyente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class HechoOutputDTO {
  //private long id; el output no necesita id no se muestra a los de afuera //TODO BORRAR
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaAcontecimiento;
  private LocalDate fechaCarga;
  //private Set<Long> idEtiquetas; porque enviarias los ids? //TODO analiza si es los ids o las direcciones y los nombres
  //private List<Long> idContenidoMultimedia;
  private Set<String> idEtiquetas;
  private List<String> idContenidoMultimedia;
  private String fuenteExterna;
  private Contribuyente contribuyente;

  /*public HechoOutputDTO(Hecho hecho) {
    //this.id = hecho.getId();
    this.titulo = hecho.getTitulo();
    this.descripcion = hecho.getDescripcion();
    this.categoria = hecho.getCategoria().getNombre();
    this.latitud = hecho.getUbicacion().getLatitud();
    this.longitud = hecho.getUbicacion().getLongitud();
    this.fechaAcontecimiento = hecho.getFechaAcontecimiento();
    this.fechaCarga = hecho.getFechaCarga();
    this.fuenteExterna = null;
    this.idEtiquetas = null;
    this.idContenidoMultimedia = null;
  }*/ //No, se pierden todos los datos se hace "a mano ahora" //TODO BORRAR

  //Pasar de DTO a entidad
  /*public Hecho toHecho() {
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
  }*/ //No ahora se hace personal, se pierden datos aca //TODO BORRAR

  //Pasar de entidad a DTO
  /*public static HechoOutputDTO fromEntity(Hecho hecho) {
    return new HechoOutputDTO(hecho);
  }*/ // para que? //TODO BORRAR
}