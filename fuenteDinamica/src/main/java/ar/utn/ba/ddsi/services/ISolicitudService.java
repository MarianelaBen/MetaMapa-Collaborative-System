package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoSolicitud;

public interface ISolicitudService {
  void solicitarEdicion(Long idHecho, HechoInputDTO nuevoHecho);
  void create(Hecho hecho, TipoSolicitud tipo);
}
