package ar.utn.ba.ddsi.models.repositories.impl;

import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.repositories.IFuenteRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FuenteRepository implements IFuenteRepository {
  private List<Fuente> fuentes = new ArrayList<>();

  @Override
  public void save(Fuente fuente){
    fuentes.add(fuente);
  }

  @Override
  public List<Fuente> findAll(){
    return this.fuentes;
  }
}
