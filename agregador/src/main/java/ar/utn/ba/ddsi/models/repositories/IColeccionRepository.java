package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import java.util.List;

public interface IColeccionRepository {
  public Coleccion save(Coleccion coleccion);
  public List<Coleccion> findAll();
  public void eliminarHecho(Hecho hecho);
}
