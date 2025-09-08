package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEstadisticaRepository extends JpaRepository<Estadistica, Long> {

  List<Estadistica> findByTipo(String tipo);
}
