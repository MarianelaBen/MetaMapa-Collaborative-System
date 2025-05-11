package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import java.util.List;

public interface IColeccionRepository {
  public void save(Coleccion coleccion);
  public List<Coleccion> findAll();
}
