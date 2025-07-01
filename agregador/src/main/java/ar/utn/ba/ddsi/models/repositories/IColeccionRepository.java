package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import java.util.List;
import java.util.Optional;

public interface IColeccionRepository {
  public Coleccion save(Coleccion coleccion);
  public List<Coleccion> findAll();
  public void eliminarHecho(Hecho hecho);
 public Coleccion findById(String coleccionId);
 public void deleteById(String id);

}
