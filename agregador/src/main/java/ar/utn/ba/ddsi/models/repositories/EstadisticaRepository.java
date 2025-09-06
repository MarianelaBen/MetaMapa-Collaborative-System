package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {

  // Última estadística calculada de un tipo
  Optional<Estadistica> findTopByTipoOrderByFechaAcontecimiento(String tipo);

  // Todas las estadísticas de un tipo, ordenadas
  List<Estadistica> findByTipoOrderByFechaAcontecimiento(String tipo);

}
