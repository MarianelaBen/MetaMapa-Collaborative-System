package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import java.util.List;

public interface ISolicitudRepository {
  public void save(SolicitudDeEliminacion solicitud);
  public List<SolicitudDeEliminacion> findAll();
}
