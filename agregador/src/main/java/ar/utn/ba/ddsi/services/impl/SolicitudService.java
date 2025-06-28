package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IColeccionService;
import ar.utn.ba.ddsi.services.IDetectorDeSpam;
import ar.utn.ba.ddsi.services.ISolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public abstract class SolicitudService implements ISolicitudService {

  @Autowired
  private ISolicitudRepository solicitudRepository;
  private IColeccionService coleccionService;
  private IDetectorDeSpam detectorDeSpam;


  //al crear la solicitud se llama a este metodo, que filtra spams
  //metodo del agregador, que suponemos que va antes de que se meta el administrador
  @Override
  public SolicitudDeEliminacion RechazadoPorSpam(SolicitudDeEliminacion solicitud) {

    if (solicitud.getHecho() == null || detectorDeSpam.esSpam(solicitud.getHecho().getTitulo())) {
      solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
      solicitud.setFechaAtencion(LocalDateTime.now());
    }
    solicitudRepository.save(solicitud);
    return solicitud;
  }


  // metodos que va a llamar el administrador cuando acepte / rechace una solicitud
  @Override
  public void aceptarSolicitud(SolicitudDeEliminacion solicitud){
    Hecho hecho = solicitud.getHecho();
    solicitud.setFechaAtencion(LocalDateTime.now());
    solicitud.cambiarEstado(EstadoSolicitud.ACEPTADA);
    hecho.setFueEliminado(true);
  }
  @Override
  public void rechazarSolicitud(SolicitudDeEliminacion solicitud){
    solicitud.setFechaAtencion(LocalDateTime.now());
    solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
  }
  @Override
  public void crearSolicitud(SolicitudDeEliminacion solicitud){
    solicitudRepository.save(solicitud);
  }

}




