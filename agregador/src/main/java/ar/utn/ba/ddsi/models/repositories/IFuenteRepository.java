package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Fuente;

import java.util.List;

public interface IFuenteRepository {
  public Fuente save(Fuente fuente);
  public List<Fuente> findAll();
  public Fuente findById(Long id);
}
