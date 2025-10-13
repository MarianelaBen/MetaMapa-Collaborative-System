package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;

public interface ISolicitudService {
   SolicitudDeEliminacion RechazadoPorSpam(SolicitudDeEliminacion solicitud);
   void aceptarSolicitud(SolicitudDeEliminacion solicitud);
   void rechazarSolicitud(SolicitudDeEliminacion solicitud);
    SolicitudDeEliminacion crearSolicitud(SolicitudInputDTO solicitud);

  Long contarPorEstado(EstadoSolicitud estadoSolicitud);
}
