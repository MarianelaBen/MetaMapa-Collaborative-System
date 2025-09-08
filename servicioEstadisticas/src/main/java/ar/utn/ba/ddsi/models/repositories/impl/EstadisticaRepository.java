package ar.utn.ba.ddsi.models.repositories.impl;

import ar.utn.ba.ddsi.models.entities.Estadistica;
import ar.utn.ba.ddsi.models.repositories.IEstadisticaRepository;
import java.time.LocalDateTime;
import java.util.List;

abstract class EstadisticaRepository implements IEstadisticaRepository {
  @Override
  public List<Estadistica> findByTipo(String tipo) {
    return List.of();
  }

  @Override
  public Estadistica findFirstByTipoOrderByFechaCalculoDesc(String tipo) {
    return null;
  }

  @Override
  public List<Estadistica> findByFechaCalculo(LocalDateTime fecha_calculo) {
    return List.of();
  }
}
