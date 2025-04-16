package domain;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public class Hecho {
  @Getter private String titulo;
  @Getter private String descripcion;
  @Getter private String categoria;
  @Setter @Getter private String contenidoMultimedia;
  @Getter private String lugar;
  @Getter private LocalDateTime fechaAcontecimmiento;
  @Getter private LocalDateTime fechaCarga;
  @Getter private Origen origen;
  @Setter @Getter private boolean fueEliminado;

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
