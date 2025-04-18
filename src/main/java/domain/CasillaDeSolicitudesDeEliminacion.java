package domain;

import domain.enumerados.EstadoSolicitud;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

public class CasillaDeSolicitudesDeEliminacion {
  @Getter public List<Solicitud> solicitudesPendientes;
  @Getter public List<Solicitud> solicitudesAtendidas;

  public CasillaDeSolicitudesDeEliminacion() {
    this.solicitudesPendientes = new ArrayList<Solicitud>();
    this.solicitudesAtendidas = new ArrayList<Solicitud>();
  }

  public boolean esValida(Solicitud solicitud){
    return solicitud.justificacion.length() > 500;
  }

  public void recibirSolicitud(Solicitud solicitud){
    if(this.esValida(solicitud)){
      solicitudesPendientes.add(solicitud);
      return;
    }
    this.rechazar(solicitud);
  }

  public void rechazar(Solicitud solicitud){
    solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
    this.enviarARegistro(solicitud);
    System.out.println("Hola, rechace.");
  }

  public void aceptar(Solicitud solicitud){
    solicitud.cambiarEstado(EstadoSolicitud.ACEPTADA );
    solicitud.getHecho().fueEliminado = true;
    this.enviarARegistro(solicitud);
    System.out.println("Hola, acepte.");
  }

  public void enviarARegistro(Solicitud solicitud){
    this.solicitudesAtendidas.add(solicitud);
  }

  public Solicitud pedidoDeSolicitud(){
    return solicitudesPendientes.remove(0); //Esta sin tomar en cuenta caso lista vacia, por que es solo para test
  }
}


