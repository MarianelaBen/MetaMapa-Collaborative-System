package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.entities.Coleccion;

public interface IConsensoService {
  void aplicarAlgoritmoDeConsenso();
  void usoDeAlgoritmo(Coleccion coleccion);
}
