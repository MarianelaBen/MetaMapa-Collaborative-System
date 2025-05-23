package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Administrador;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoSolicitud;
import ar.utn.ba.ddsi.models.repositories.impl.SolicitudRepository;
import ar.utn.ba.ddsi.services.IHechoService;
import ar.utn.ba.ddsi.services.ISolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class SolicitudService implements ISolicitudService {

  @Autowired
  private SolicitudRepository solicitudRepository;
  @Autowired
  private IHechoService hechoService;
  //public void solicitarEdicion(Long idHecho, HechoInputDTO nuevoHecho) {

    @Override
    public void create(Hecho hecho, TipoSolicitud tipo){
      Solicitud solicitud = new Solicitud(hecho, tipo);
      this.solicitudRepository.save(solicitud);
    }

    @Override
    public void atencionDeSolicitud(Long idSolicitud, EstadoSolicitud estado, String comentario, Administrador administrador){
      Solicitud solicitud = solicitudRepository.findById(idSolicitud);
      solicitud.cambiarEstado(estado);
      //solicitud.setAdministradorQueAtendio(Administrador); TODO habria que hacer que llegue el id de admin y de ahi sacar al admin y gurdarlo
      solicitud.setComentario(comentario);

      //TODO todo el tema de rechazar y aceptar cada tipo de solicitud
      switch (estado) {
        case ACEPTADA:
          solicitud.setEstado(EstadoSolicitud.ACEPTADA);
          guardadoDeCredencial(solicitud, comentario, administrador);
          break;
        case RECHAZADA:
            solicitud.setEstado(EstadoSolicitud.RECHAZADA);
            guardadoDeCredencial(solicitud, comentario, administrador);
          if(solicitud.getTipoSolicitud() == TipoSolicitud.CREACION){
            this.hechoService.creacionRechazada(solicitud.getHecho());
          }
          else{
            this.hechoService.edicionRechazada(solicitud.getHecho());
          }
          break;
      }

      solicitudRepository.save(solicitud);

    }

    public void guardadoDeCredencial(Solicitud solicitud ,String comentario, Administrador administrador){
      solicitud.setComentario(comentario);
      solicitud.setAdministradorQueAtendio(administrador);
      solicitud.setFechaAtencion(LocalDate.now());
    }
}