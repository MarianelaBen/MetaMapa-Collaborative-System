package domain;

import java.time.LocalDateTime;
import domain.enumerados.Origen;
import lombok.Getter;
import lombok.Setter;

public class Hecho {
  @Getter public String titulo;
  @Getter public String descripcion;
  @Getter public String categoria;
  @Setter @Getter public String contenidoMultimedia;
  @Getter public String lugar;
  @Getter public LocalDateTime fechaAcontecimmiento;
  @Getter public LocalDateTime fechaCarga;
  @Getter public Origen origen;
  @Setter @Getter public boolean fueEliminado;

  //  @Setter @Getter private String nombreAportante;
  //  @Setter @Getter private String apellidoAportante;
  //  @Setter @Getter private Integer edadAportante;

  public Hecho(String titulo, String descripcion, String categoria, String lugar, LocalDateTime fechaAcontecimmiento, Origen origen){
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.lugar = lugar;
    this.fechaAcontecimmiento = fechaAcontecimmiento;
    this.fechaCarga = LocalDateTime.now();
    this.origen = origen;
    this.fueEliminado = false;
  }
}
