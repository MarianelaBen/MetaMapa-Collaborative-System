package ar.utn.ba.ddsi.models.entities;

import ar.utn.ba.ddsi.models.entities.criterios.Criterio;
import ar.utn.ba.ddsi.models.entities.fuentes.Fuente;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;

public class Coleccion {
  @Getter private String titulo;
  @Getter private String descripcion;
  @Getter private Set<Fuente> fuentes;
  @Getter private Set<Criterio> criterios;
  @Getter private List<Hecho> hechosDeLaColeccion;

  public Coleccion(String titulo, String descripcion){
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuentes = new HashSet<>();
    this.criterios = new HashSet<>();
    this.hechosDeLaColeccion = new ArrayList<>();
  }

  public void agregarFuentes(Fuente ... nuevasFuentes){
    Collections.addAll(fuentes, nuevasFuentes);
  }

  public void filtrarHechos(){
    this.hechosDeLaColeccion.clear();
    List<Hecho> hechosFiltrados = fuentes.stream().flatMap(fuente -> fuente.getHechos().stream()).filter(this::noFueEliminado).collect(Collectors.toList());
    if(this.criterios.isEmpty()){
      this.agregarHechos(hechosFiltrados);}
    else {this.agregarHechos(hechosFiltrados.stream().filter(this::cumpleLosCriterios).collect(Collectors.toList()));}
    }

  public void agregarHechos(List<Hecho> hechos){
    this.hechosDeLaColeccion.addAll(hechos);
  }

  public boolean cumpleLosCriterios(Hecho hecho){
    return this.criterios.stream().allMatch(c -> c.cumpleCriterio(hecho));
  }

  public boolean noFueEliminado(Hecho hecho){
    return !hecho.isFueEliminado();
  }

  public void agregarCriterios(Criterio ... nuevosCriterios){
    Collections.addAll(criterios, nuevosCriterios);
  }


}



