package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {

  // obtener la última fila de un tipo por fecha (descendente)
  Optional<Estadistica> findTopByTipoOrderByFechaAcontecimientoDesc(String tipo);

  // obtener el registro de un tipo con mayor valor (útil para "top")
  Optional<Estadistica> findTopByTipoOrderByValorDesc(String tipo);

  // historial por tipo (más reciente primero)
  List<Estadistica> findByTipoOrderByFechaAcontecimientoDesc(String tipo);
}
