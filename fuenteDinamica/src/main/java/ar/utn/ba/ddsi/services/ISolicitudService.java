package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Administrador;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoSolicitud;

public interface ISolicitudService {
  //void solicitarEdicion(Long idHecho, HechoInputDTO nuevoHecho);
  void create(Hecho hecho, TipoSolicitud tipo);
  void atencionDeSolicitud(Long idSolicitud, EstadoSolicitud estado, String comentario, Administrador administrador);
}
