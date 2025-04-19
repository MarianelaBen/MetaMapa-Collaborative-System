package domain;

public class Administrador {

  private CasillaDeSolicitudesDeEliminacion casilla;

  public Administrador(CasillaDeSolicitudesDeEliminacion casilla) {
    this.casilla = casilla;
  }

  // METODOS USADOS PARA LOS TEST

  public void atenderSolicitud(Solicitud solicitud) {
    solicitud.setAdministradorQueAtendio(this);
  }

  //Solo rechaza o acepta directo, porque suponemos falta un paso intermedio en que el admin lee la solicitud
  public void rechazarSolicitud() {
    Solicitud solicitudAAtender = casilla.pedidoDeSolicitud();
    atenderSolicitud(solicitudAAtender);
    casilla.rechazar(solicitudAAtender);
  }

  public void aceptarSolicitud() {
    Solicitud solicitudAAtender = casilla.pedidoDeSolicitud();
    atenderSolicitud(solicitudAAtender);
    casilla.aceptar(solicitudAAtender);
  }
}
