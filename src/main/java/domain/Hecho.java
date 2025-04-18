package domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import domain.criterios.Criterio;
import domain.enumerados.Origen;
import lombok.Getter;
import lombok.Setter;

public class Hecho {
  @Getter public String titulo;
  @Getter public String descripcion;
  @Getter public String categoria;
  @Setter @Getter public String contenidoMultimedia; //Lo dejamos como string y mas adelante vemos.
  @Getter public Integer latitud;
  @Getter public Integer longitud;
  @Getter public LocalDate fechaAcontecimiento;
  @Getter public LocalDate fechaCarga;
  @Getter public Origen origen;
  @Setter @Getter public boolean fueEliminado;

  //  @Setter @Getter private String nombreAportante;
  //  @Setter @Getter private String apellidoAportante;
  //  @Setter @Getter private Integer edadAportante;

  public Hecho(String titulo, String descripcion, String categoria, Integer latitud, Integer longitud, LocalDate fechaAcontecimiento, Origen origen){
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaAcontecimiento = fechaAcontecimiento;
    this.fechaCarga = LocalDate.now();
    this.origen = origen;
    this.fueEliminado = false;
  }

  private Set<String> etiquetas = new HashSet<>(); //seguro cambia el tipo

  public void agregarEtiqueta(String etiqueta) {
    this.etiquetas.add(etiqueta);
  }

  public Set<String> getEtiquetas() {
    return etiquetas;
  }

}
