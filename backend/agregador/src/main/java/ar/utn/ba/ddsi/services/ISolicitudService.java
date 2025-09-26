package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;

public interface ISolicitudService {
   SolicitudDeEliminacion RechazadoPorSpam(SolicitudDeEliminacion solicitud);
   void aceptarSolicitud(SolicitudDeEliminacion solicitud);
   void rechazarSolicitud(SolicitudDeEliminacion solicitud);
   void crearSolicitud(SolicitudDeEliminacion solicitud);
}
