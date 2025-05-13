package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IDetectorDeSpam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public abstract class SolicitudService implements IDetectorDeSpam{

  @Autowired
  private ISolicitudRepository solicitudRepository;

  public void gestionarSolicitudDeEliminacion(Solicitud solicitud){
    if (esSpam(solicitud.getHecho().getTitulo())){
      solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA); //si es spam se rechaza
    } else {
      Hecho hecho = solicitud.getHecho();
      if(hecho != null) { //si no es spam y existe el hecho
        solicitud.setFechaAtencion(LocalDateTime.now());
        solicitud.cambiarEstado(EstadoSolicitud.ACEPTADA);
        hecho.setFueEliminado(true);
      } else { //si no es spam y no existe el hecho se rechaza
        solicitud.setFechaAtencion(LocalDateTime.now());
        solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
      }
    }
    solicitudRepository.save(solicitud);
  }


    }

