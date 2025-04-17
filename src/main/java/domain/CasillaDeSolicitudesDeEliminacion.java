package domain;

import domain.enumerados.EstadoSolicitud;
import java.util.List;

public class CasillaDeSolicitudesDeEliminacion {
  private List<Solicitud> solicitudesDeEliminacion;

  public CasillaDeSolicitudesDeEliminacion(List<Solicitud> solicitudesDeEliminacion) {
    this.solicitudesDeEliminacion = solicitudesDeEliminacion;
  }

  public boolean esValida(Solicitud solicitud){
    return solicitud.justificacion.length() > 500;
  }

  public void recibirSolicitud(Solicitud solicitud){
    if(this.esValida(solicitud)){
      solicitudesDeEliminacion.add(solicitud);
      return;
    }
    this.rechazar(solicitud);
  }

  public void rechazar(Solicitud solicitud){
    solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
    System.out.println("Hola, esto es un log en consola.");
  }
}


