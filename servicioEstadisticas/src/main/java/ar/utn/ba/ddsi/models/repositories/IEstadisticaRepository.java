package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IEstadisticaRepository extends JpaRepository<Estadistica, Long> {

  List<Estadistica> findByTipo(String tipo);

  // la última estadistica de un tipo
  Estadistica findFirstByTipoOrderByFechaCalculoDesc(String tipo);

  // todas las estadisticas de una ejecucion concreta
  List<Estadistica> findByFechaCalculo(LocalDateTime fecha);

}
