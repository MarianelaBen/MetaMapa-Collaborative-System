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
import java.util.stream.Collectors;

public class Coleccion {
  @Getter private String titulo;
  @Getter private String descripcion;
  @Getter private Fuente fuente;
  @Getter private Set<Criterio> criterios;
  @Getter public Set<Hecho> hechosDeLaColeccion;

  public Coleccion(String titulo, String descripcion, Fuente fuente){
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterios = new HashSet<>();
    this.hechosDeLaColeccion = new HashSet<>();
  }

  public void filtrarHechos(){
    this.hechosDeLaColeccion.clear();
    if(this.criterios.isEmpty()){
      this.agregarHechos(fuente.leerHechos());}
    else {
      Set<Hecho> hechosFiltrados = fuente.leerHechos().stream().filter(this::cumpleLosCriterios).collect(Collectors.toSet());
        this.agregarHechos(hechosFiltrados);}
    }



  public void agregarHechos(Set<Hecho> hechos){
    this.hechosDeLaColeccion.addAll(hechos);
  }

  public boolean cumpleLosCriterios(Hecho hecho){
    return this.criterios.stream().allMatch(c -> c.cumpleCriterio(hecho));
  }

  public void agregarCriterios(Criterio ... nuevosCriterios){
    Collections.addAll(criterios, nuevosCriterios);
  }

}



