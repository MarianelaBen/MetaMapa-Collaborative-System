package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import java.util.Optional;

public interface ISolicitudRepository {
  void save(Solicitud solicitud);
  Solicitud findById(Long id);
}
