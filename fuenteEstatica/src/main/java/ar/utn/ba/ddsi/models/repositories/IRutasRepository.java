package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Ruta;
import java.util.List;

public interface IRutasRepository {
  void save(Ruta ruta);
  Ruta findById(Long id);
  Long generarNuevoId();
}
