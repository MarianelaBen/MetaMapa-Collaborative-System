package ar.utn.ba.ddsi.models.repositories;


import ar.utn.ba.ddsi.models.entities.Hecho;
import java.util.List;

public interface IHechoRepository {
  public void save(Hecho hecho);

  public void delete(Hecho hecho);

  public Long generarNuevoId();

  public Hecho findById(Long id);

  public List<Hecho> findAll();
}
