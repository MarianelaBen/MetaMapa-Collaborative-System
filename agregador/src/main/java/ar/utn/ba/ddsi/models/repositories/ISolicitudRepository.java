package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import java.util.List;

public interface ISolicitudRepository {
  public SolicitudDeEliminacion save(SolicitudDeEliminacion solicitud);
  public List<SolicitudDeEliminacion> findAll();
  public List<SolicitudDeEliminacion> findByEstado(EstadoSolicitud estado);
  public SolicitudDeEliminacion findById(Long id);
}
