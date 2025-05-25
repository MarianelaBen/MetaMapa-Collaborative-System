package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IColeccionService;
import ar.utn.ba.ddsi.services.IDetectorDeSpam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public abstract class SolicitudService implements IDetectorDeSpam {

  @Autowired
  private ISolicitudRepository solicitudRepository;
  private IColeccionService coleccionService;


  //al crear la solicitud se llama a este metodo, que filtra spams
  //metodo del agregador, que suponemos que va antes de que se meta el administrador
  public Solicitud filtrarSpams(Solicitud solicitud) {

    if (solicitud.getHecho() == null || esSpam(solicitud.getHecho().getTitulo())) {
      solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
      solicitud.setFechaAtencion(LocalDateTime.now());
    }
    solicitudRepository.save(solicitud); //guarda en un repositorio propio
    return solicitud;
  }


  // metodos que va a llamar el administrador cuando acepte / rechace una solicitud

  public void aceptarSolicitud(Solicitud solicitud){
    Hecho hecho = solicitud.getHecho();
    solicitud.setFechaAtencion(LocalDateTime.now());
    solicitud.cambiarEstado(EstadoSolicitud.ACEPTADA);
    hecho.setFueEliminado(true);
    coleccionService.eliminarHechoDeColeccion(hecho);
  }

  public void rechazarSolicitud(Solicitud solicitud){
    solicitud.setFechaAtencion(LocalDateTime.now());
    solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
  }

}




