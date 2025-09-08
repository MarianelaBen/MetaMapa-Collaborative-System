package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoSolicitud;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IHechoService;
import ar.utn.ba.ddsi.services.ISolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class SolicitudService implements ISolicitudService {

  @Autowired
  private ISolicitudRepository solicitudRepository;
  @Autowired
  private IHechoService hechoService;

    @Override
    public void create(Hecho hecho, TipoSolicitud tipo){
      Solicitud solicitud = new Solicitud(hecho, tipo);
      this.solicitudRepository.save(solicitud);
    }

    @Override
    public void atencionDeSolicitud(Long idSolicitud, EstadoSolicitud estado, String comentario, Long idAdministrador){
      Solicitud solicitud = solicitudRepository.findById(idSolicitud);
      solicitud.cambiarEstado(estado);
      solicitud.setComentario(comentario);

      switch (estado) {
        case ACEPTADA:
          solicitud.setEstado(EstadoSolicitud.ACEPTADA);
          guardadoDeCredencial(solicitud, comentario, idAdministrador);
          break;
        case RECHAZADA:
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            guardadoDeCredencial(solicitud, comentario, idAdministrador);
          if (solicitud.getTipoSolicitud() == TipoSolicitud.CREACION){
            this.hechoService.creacionRechazada(solicitud.getHecho());
          }
          else{
            this.hechoService.edicionRechazada(solicitud.getHecho());
          }
          break;
      }
      solicitudRepository.save(solicitud);

    }

    public void guardadoDeCredencial(Solicitud solicitud ,String comentario, Long idAdministrador){
      solicitud.setComentario(comentario);
      solicitud.setIdAdministradorQueAtendio(idAdministrador);
      solicitud.setFechaAtencion(LocalDate.now());
    }
}