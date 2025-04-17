package domain;

import domain.criterios.Criterio;
import domain.fuentes.Fuente;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import domain.fuentes.FuenteEstatica;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

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

  public void filtrarHechos(){
    this.agregarHechos((Hecho) fuente.leerHechos().stream().filter(this::cumpleLosCriterios).toList());
  }

  public void agregarHechos(Hecho ... hechos){

    Collections.addAll(this.hechosDeLaColeccion, hechos);
  }

  public boolean cumpleLosCriterios(Hecho hecho){
    return this.criterios.stream().allMatch(c -> c.cumpleCriterio(hecho));
  }

}



