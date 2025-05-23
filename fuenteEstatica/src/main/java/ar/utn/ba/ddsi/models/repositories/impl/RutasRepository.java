package ar.utn.ba.ddsi.models.repositories.impl;

import ar.utn.ba.ddsi.models.repositories.IRutasRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RutasRepository implements IRutasRepository {
  private List<String> rutas;

  public RutasRepository() {
    this.rutas = new ArrayList<String>();
  }

  @Override
  public void save(String ruta) {
    if (!this.rutas.contains(ruta)) {
      this.rutas.add(ruta);
    }
  }

  @Override
  public List<String> findAll() {
    return this.rutas;
  }
}
