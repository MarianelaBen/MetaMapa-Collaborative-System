package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEstadisticaRepository extends JpaRepository<Estadistica, Long> {

  List<Estadistica> findAllByHechosPorProvinciaEnColeccion_ColeccionHandleOrderByFechaDeCalculoDesc(String coleccionHandle);
  Optional<Estadistica> findTopByHechosPorProvinciaEnColeccion_ColeccionHandleOrderByFechaDeCalculoDesc(String coleccionHandle);

  List<Estadistica> findAllByProvinciaTopPorCategoria_CategoriaOrderByFechaDeCalculoDesc(Long categoriaId);
  Optional<Estadistica> findTopByProvinciaTopPorCategoria_CategoriaOrderByFechaDeCalculoDesc(Long categoriaId);

  List<Estadistica> findAllByHorarioPicoPorCategoria_CategoriaOrderByFechaDeCalculoDesc(Long categoriaId);
  Optional<Estadistica> findTopByHorarioPicoPorCategoria_CategoriaOrderByFechaDeCalculoDesc(Long categoriaId);

  Optional<Estadistica> findTopByTopCategoriaGlobal_CategoriaGanadoraIsNotNullOrderByFechaDeCalculoDesc();

  Optional<Estadistica> findTopBySolicitudesEliminacionResumen_TotalIsNotNullOrderByFechaDeCalculoDesc();
}
