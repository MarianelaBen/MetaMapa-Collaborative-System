package ar.utn.ba.ddsi.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Embeddable
public class HechoEstadoPrevio {

  private String titulo;
  private String descripcion;
  private Categoria categoria;
  private Ubicacion ubicacion;
  private LocalDate fechaAcontecimiento;
  private List<ContenidoMultimedia> contenidosMultimedia;

  public HechoEstadoPrevio(Hecho hecho) {
    this.titulo = hecho.getTitulo();
    this.descripcion = hecho.getDescripcion();
    this.categoria = hecho.getCategoria();
    this.ubicacion = hecho.getUbicacion();
    this.fechaAcontecimiento = hecho.getFechaAcontecimiento();
    this.contenidosMultimedia = new ArrayList<>(hecho.getContenidosMultimedia());
  }
}

