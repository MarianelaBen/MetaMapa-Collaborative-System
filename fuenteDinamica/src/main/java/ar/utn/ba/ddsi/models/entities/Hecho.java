package ar.utn.ba.ddsi.models.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
  private Long idContribuyente;
  private List<ContenidoMultimedia> contenidosMultimedia;
  private HechoEstadoPrevio estadoPrevio;

  public Hecho(String titulo, String descripcion, Categoria categoria, Ubicacion ubicacion, LocalDate fechaAcontecimiento, Origen origen){
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = ubicacion;
    this.fechaAcontecimiento = fechaAcontecimiento;
    this.fechaCarga = LocalDate.now();
    this.origen = origen;
    this.fueEliminado = false;
    this.etiquetas = new HashSet<>();
    this.contenidosMultimedia = new ArrayList<>();
  }

  public void agregarEtiqueta(Etiqueta etiqueta) {
    this.etiquetas.add(etiqueta);
  }

  public void actualizarHecho(String titulo, String descripcion, Categoria categoria, Ubicacion ubicacion, LocalDate fechaAcontecimiento) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.ubicacion = ubicacion;
    this.fechaAcontecimiento = fechaAcontecimiento;
  }

  public List<String> getPathsMultimedia(List<ContenidoMultimedia> contenidos){
    return contenidos.stream().map(ContenidoMultimedia::getPath).collect(Collectors.toList());
  }

  /*public void restaurarDesde(HechoEstadoPrevio previo) {

  }*/
}
