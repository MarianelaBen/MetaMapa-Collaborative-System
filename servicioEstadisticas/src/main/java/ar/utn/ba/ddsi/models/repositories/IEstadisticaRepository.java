package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEstadisticaRepository extends JpaRepository<Estadistica, Long> {

  List<Estadistica> findAllByPregunta_PreguntaOrderByFechaDeCalculoDesc(String pregunta);

  Optional<Estadistica> findTopByPregunta_PreguntaOrderByFechaDeCalculoDesc(String pregunta);

  List<Estadistica> findAllByPreguntaIdOrderByFechaDeCalculoDesc(Long preguntaId);

  Optional<Estadistica> findTopByPreguntaIdOrderByFechaDeCalculoDesc(Long preguntaId);

  List<Estadistica> findByFechaDeCalculo(LocalDateTime fechaDeCalculo);

  List<Estadistica> findAllByPreguntaIdAndColeccionHandleOrderByFechaDeCalculoDesc(Long preguntaId, String coleccionHandle);

}
