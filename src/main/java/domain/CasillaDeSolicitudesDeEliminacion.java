package domain;

import java.util.List;

public class CasillaDeSolicitudesDeEliminacion {
  private List<Solicitud> solicitudesDeEliminacion;

  public void revisarSolicitudes(){

  }

  public void recibirSolicitud(Solicitud solicitud){
    solicitudesDeEliminacion.add(solicitud);
  }
}
