package ar.utn.ba.ddsi.models.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Hecho {
    private Long id;
   private String titulo;
   private String descripcion;
   private Categoria categoria;
   private Ubicacion ubicacion;
   private LocalDate fechaAcontecimiento;
   private LocalDate fechaCarga;
   private Origen origen;
   private boolean fueEliminado;
   private Set<Etiqueta> etiquetas;
   String fuenteExterna;
   boolean consensuado;

  // private String nombreAportante;
  // private String apellidoAportante;
  // private Integer edadAportante;

  public Hecho(String titulo, String descripcion, Categoria categoria, Ubicacion ubicacion, LocalDate fechaAcontecimiento, Origen origen, String fuenteExterna){
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = ubicacion;
    this.fechaAcontecimiento = fechaAcontecimiento;
    this.fechaCarga = LocalDate.now();
    this.origen = origen;
    this.fueEliminado = false;
    this.etiquetas = new HashSet<>();
    this.fuenteExterna = fuenteExterna;
    this.consensuado = true;
  }

  public void agregarEtiqueta(Etiqueta etiqueta) {
    this.etiquetas.add(etiqueta);
  }

}