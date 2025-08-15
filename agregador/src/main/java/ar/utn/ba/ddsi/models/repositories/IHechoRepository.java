package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import java.util.List;

public interface IHechoRepository {
  public Hecho save(Hecho hecho);
  public List<Hecho> findAll();
  public Hecho findById(Long hechoId);
  public void deleteById(Long id);
}
