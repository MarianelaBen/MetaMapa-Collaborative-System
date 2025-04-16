package domain;

import domain.criterios.Criterio;
import domain.fuentes.Fuente;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


public class Coleccion {
  @Getter private String titulo;
  @Getter private String descripcion;
  @Getter private Fuente fuente;
  @Setter @Getter private Set<Criterio> criterios;
  @Getter private Set<Hecho> hechosDeLaColeccion;

  public Coleccion(String titulo, String descripcion, Fuente fuente){
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.hechosDeLaColeccion = new HashSet<>();
  }
}
