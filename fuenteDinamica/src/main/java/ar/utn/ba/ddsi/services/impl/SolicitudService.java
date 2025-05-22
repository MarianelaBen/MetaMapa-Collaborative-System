package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoSolicitud;
import ar.utn.ba.ddsi.models.repositories.impl.SolicitudRepository;
import ar.utn.ba.ddsi.services.ISolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolicitudService implements ISolicitudService {

  @Autowired
  private SolicitudRepository solicitudRepository;
  //public void solicitarEdicion(Long idHecho, HechoInputDTO nuevoHecho) {

    @Override
    public void create(Hecho hecho, TipoSolicitud tipo){
      Solicitud solicitud = new Solicitud(hecho, tipo);
      this.solicitudRepository.save(solicitud);
    }

    @Override
    public void atencionDeSolicitud(Long idSolicitud, EstadoSolicitud estado, String comentario){
      Solicitud solicitud = solicitudRepository.findById(idSolicitud);
      solicitud.cambiarEstado(estado);
      //solicitud.setAdministradorQueAtendio(Administrador); TODO habria que hacer que llegue el id de admin y de ahi sacar al admin y gurdarlo
      solicitud.setComentario(comentario);

      //TODO todo el tema de rechazar y aceptar cada tipo de solicitud
      switch (estado) {
        case ACEPTADA:
          if(solicitud.getTipoSolicitud() == TipoSolicitud.CREACION){}
          else{}
          break;
        case RECHAZADA:
          if(solicitud.getTipoSolicitud() == TipoSolicitud.CREACION){}
          else{}
          break;
      }

      solicitudRepository.save(solicitud);

    }
}