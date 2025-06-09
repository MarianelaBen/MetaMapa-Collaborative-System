package ar.utn.ba.ddsi.schedulers;

import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ColeccionesScheduler {
  private final IColeccionService coleccionService;
  private final long unaHoraEnMs = 3600000;

  public ColeccionesScheduler(IColeccionService coleccionService){
    this.coleccionService = coleccionService;
  }


  //el fixedRate cada ese tiempo toma un hilo

  @Scheduled( fixedRate = unaHoraEnMs )
  public void actualizarHechosColecciones() {
    coleccionService.actualizarColecciones();
  }

}
