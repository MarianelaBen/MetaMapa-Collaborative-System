package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IDetectorDeSpam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public abstract class SolicitudService implements IDetectorDeSpam {

  @Autowired
  private ISolicitudRepository solicitudRepository;
  //TODO preguntar si acá deberíamos pasarle un colecciónService o si está bien que se pase el repository
  private IColeccionRepository coleccionRepository;

  public Solicitud save(Solicitud solicitud) {
    if (solicitud.getHecho() == null || esSpam(solicitud.getHecho().getTitulo())) {
      solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
    }
    solicitudRepository.save(solicitud);
    return solicitud;
  }

  public Solicitud aceptarSolicitud(Solicitud solicitud) {
    Hecho hecho = solicitud.getHecho();
    if (hecho != null) {
      solicitud.setFechaAtencion(LocalDateTime.now());
      hecho.setFueEliminado(true);
      coleccionRepository.update(hecho);
    }
    solicitudRepository.save(solicitud);
    return solicitud;
  }
}
  //el administrador mismo va a cambiar el estado

