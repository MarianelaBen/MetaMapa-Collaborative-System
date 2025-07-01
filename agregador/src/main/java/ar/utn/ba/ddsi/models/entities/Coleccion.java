package ar.utn.ba.ddsi.models.entities;

import ar.utn.ba.ddsi.algoritmos.IAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.models.entities.criterios.Criterio;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
@Getter
@Setter
public class Coleccion {
    private String titulo;
     private String descripcion;
     private Set<Fuente> fuentes;
     private Set<Criterio> criterios;
     private List<Hecho> hechos;
     private String handle;
     private TipoAlgoritmoDeConsenso algoritmoDeConsenso;

  public Coleccion(String titulo, String descripcion, Set<Fuente> fuentes) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuentes = fuentes;
    this.criterios = new HashSet<>();
    this.hechos = new ArrayList<>();
    this.algoritmoDeConsenso = null;
  }

  public void agregarFuentes(Fuente ... nuevasFuentes){
    Collections.addAll(fuentes, nuevasFuentes);
  }

  public void agregarHechos(List<Hecho> hechos){
    this.hechos.addAll(hechos);
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



