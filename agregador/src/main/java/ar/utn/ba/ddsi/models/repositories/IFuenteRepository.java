package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Solicitud;

import java.util.List;

public interface IFuenteRepository {
  public void save(Fuente fuente);
  public List<Fuente> findAll();
}
