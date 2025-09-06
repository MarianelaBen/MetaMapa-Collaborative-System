package ar.utn.ba.ddsi.schedulers;

import ar.utn.ba.ddsi.services.IEstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EstadisticasScheduler {
  @Autowired
  IEstadisticasService estadisticasService;

  @Scheduled(cron = "0 0 0 1 * *")
  public void runPeriodicRecalculation() {
    estadisticasService.recalcularEstadisticas();
  }
}