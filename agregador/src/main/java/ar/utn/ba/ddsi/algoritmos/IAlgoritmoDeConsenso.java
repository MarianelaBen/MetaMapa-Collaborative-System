package ar.utn.ba.ddsi.algoritmos;

import ar.utn.ba.ddsi.models.entities.Hecho;
import java.util.List;

public interface IAlgoritmoDeConsenso {
  void calcularConsenso(List<Hecho> hechos);
}
