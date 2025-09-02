package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ISolicitudRepository extends JpaRepository<SolicitudDeEliminacion, Long> {
  //public SolicitudDeEliminacion save(SolicitudDeEliminacion solicitud);
  //public List<SolicitudDeEliminacion> findAll();
  //public List<SolicitudDeEliminacion> findByEstado(EstadoSolicitud estado);
  //public SolicitudDeEliminacion findById(Long id);
}
