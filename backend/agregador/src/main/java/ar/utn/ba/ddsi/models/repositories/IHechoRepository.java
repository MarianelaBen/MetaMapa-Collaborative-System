package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IHechoRepository extends JpaRepository<Hecho,Long> {
    @Query(value = "" +
            "SELECT h.* " +
            "FROM hecho h " +
            "JOIN coleccion_hecho ch ON h.id = ch.hecho_id " +
            "WHERE TRIM(ch.coleccion_handle) = TRIM(:coleccionHandle)",
            nativeQuery = true)
    List<Hecho> findByColeccionHandle(@Param("coleccionHandle") String coleccionHandle);
    List<Hecho> findTop3ByFueEliminadoFalseOrderByCantVistasDesc();
    List<Hecho> findByCategoriaId(Long categoriaId);
    List<Hecho> findByCategoria(Categoria categoria);

    @Query("SELECT h FROM Hecho h WHERE h.contribuyente.idContribuyente = :id")
    List<Hecho> buscarPorIdContribuyente(@Param("id") Long id);

    Optional<Hecho> findByTitulo(String titulo);
    //List<Hecho> findByContribuyente_IdAndFueEliminadoFalseOrderByFechaCargaDesc(Long contribuyenteId);
    //List<Hecho> findAllByContribuyenteWithJoins(@Param("contribuyenteId") Long contribuyenteId);
    Optional<Hecho> findByIdEnFuenteAndOrigen(Long idEnFuente, Origen origen);
    @Query(value = "SELECT * FROM hecho h WHERE h.fue_eliminado = false ORDER BY h.fecha_carga DESC LIMIT :limit", nativeQuery = true)
    List<Hecho> findUltimosHechos(@Param("limit") int limit);

    List<Hecho> findTop1000ByFueEliminadoFalseOrderByFechaCargaDesc();
    // En IHechoRepository.java

    @Query("SELECT h FROM Hecho h WHERE " +
            "(:id IS NULL OR h.id = :id) AND " +
            "(:ubicacion IS NULL OR LOWER(h.ubicacion.provincia) LIKE LOWER(CONCAT('%', :ubicacion, '%'))) AND " +
            "(:eliminado IS NULL OR h.fueEliminado = :eliminado) AND " +
            "(:fecha IS NULL OR CAST(h.fechaAcontecimiento AS LocalDate) = :fecha)")
    Page<Hecho> buscarConFiltros(
            @Param("id") Long id,
            @Param("ubicacion") String ubicacion,
            @Param("eliminado") Boolean eliminado,
            @Param("fecha") LocalDate fecha,
            Pageable pageable
    );
}
