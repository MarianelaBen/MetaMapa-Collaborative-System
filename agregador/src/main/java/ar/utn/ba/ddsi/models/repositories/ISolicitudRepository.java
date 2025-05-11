package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Solicitud;
import java.util.List;

public interface ISolicitudRepository {
  public void save(Solicitud solicitud);
  public List<Solicitud> findAll();
}
