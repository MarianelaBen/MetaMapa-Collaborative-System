package ar.utn.ba.ddsi.schedulers;

import ar.utn.ba.ddsi.services.IConsensoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ConsensoScheduler {
  @Autowired
  IConsensoService consensoService;

  @Scheduled(cron = "0 0 3 * * *") //se ejecuta todos los dais a las 3AM (suponemos eso horario de baja acividad)
  public void ejecutarAlgoritmoDeConsenso() {
    try {
      consensoService.aplicarAlgoritmoDeConsenso();
    } catch (Exception e) {
      System.err.println("Error al ejecutar el algoritmo de consenso: " + e.getMessage());
    }
  }

}
