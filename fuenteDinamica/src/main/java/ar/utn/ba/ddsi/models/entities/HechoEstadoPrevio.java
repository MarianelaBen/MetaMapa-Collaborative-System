package ar.utn.ba.ddsi.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hechoEstadoPrevio")
@Setter
@Getter
@NoArgsConstructor
public class HechoEstadoPrevio {

  @Id
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId //uso el id de hecho
  @JoinColumn(name = "id") //columna es pk y fk
  private Hecho hecho;

  @Column(name = "titulo", nullable = false)
  private String titulo;

  @Column(name = "descripcion", nullable = false)
  private String descripcion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoria_id", nullable = false)
  private Categoria categoria;

  @Embedded
  private Ubicacion ubicacion;

  @Column(name = "fecha_acontecimiento", nullable = false)
  private LocalDateTime fechaAcontecimiento;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @JoinColumn(name = "hecho_id")
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

