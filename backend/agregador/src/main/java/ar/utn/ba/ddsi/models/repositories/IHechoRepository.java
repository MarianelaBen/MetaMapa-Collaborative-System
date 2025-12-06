package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Categoria;
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

    // ... (tus otros métodos findByColeccionHandle, etc. quedan igual) ...
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
    Optional<Hecho> findByIdEnFuenteAndOrigen(Long idEnFuente, Origen origen);

    @Query(value = "SELECT * FROM hecho h WHERE h.fue_eliminado = false ORDER BY h.fecha_carga DESC LIMIT :limit", nativeQuery = true)
    List<Hecho> findUltimosHechos(@Param("limit") int limit);
    List<Hecho> findTop1000ByFueEliminadoFalseOrderByFechaCargaDesc();


    @Query(value = "SELECT * FROM hecho h WHERE " +
            "(:id IS NULL OR h.id = :id) AND " +
            "(:eliminado IS NULL OR h.fue_eliminado = :eliminado) AND " +
            "(:fecha IS NULL OR CAST(h.fecha_acontecimiento AS DATE) = :fecha) AND " +
            "(" +
            "   -- CASO 1: Si hay coordenadas, filtramos SOLO por radio (ignoramos texto provincia) \n" +
            "   (:lat IS NOT NULL AND :lon IS NOT NULL AND :radio IS NOT NULL AND " +
            "       (6371 * acos(cos(radians(:lat)) * cos(radians(h.latitud)) * " +
            "       cos(radians(h.longitud) - radians(:lon)) + " +
            "       sin(radians(:lat)) * sin(radians(h.latitud)))) <= :radio) " +
            "   OR " +
            "   -- CASO 2: Si NO hay coordenadas, filtramos por coincidencia de texto en provincia \n" +
            "   (:lat IS NULL AND (:ubicacion IS NULL OR LOWER(h.provincia) LIKE LOWER(CONCAT('%', :ubicacion, '%'))))" +
            ")",

            countQuery = "SELECT count(*) FROM hecho h WHERE " +
                    "(:id IS NULL OR h.id = :id) AND " +
                    "(:eliminado IS NULL OR h.fue_eliminado = :eliminado) AND " +
                    "(:fecha IS NULL OR CAST(h.fecha_acontecimiento AS DATE) = :fecha) AND " +
                    "(" +
                    "   (:lat IS NOT NULL AND :lon IS NOT NULL AND :radio IS NOT NULL AND " +
                    "       (6371 * acos(cos(radians(:lat)) * cos(radians(h.latitud)) * " +
                    "       cos(radians(h.longitud) - radians(:lon)) + " +
                    "       sin(radians(:lat)) * sin(radians(h.latitud)))) <= :radio) " +
                    "   OR " +
                    "   (:lat IS NULL AND (:ubicacion IS NULL OR LOWER(h.provincia) LIKE LOWER(CONCAT('%', :ubicacion, '%'))))" +
                    ")",
            nativeQuery = true)
    Page<Hecho> buscarConFiltros(
            @Param("id") Long id,
            @Param("ubicacion") String ubicacion,
            @Param("eliminado") Boolean eliminado,
            @Param("fecha") LocalDate fecha,
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("radio") Double radio,
            Pageable pageable
    );


    @Query(value = "SELECT * FROM hecho h WHERE " +
            "h.fue_eliminado = false AND " +
            "(6371 * acos(cos(radians(:latUser)) * cos(radians(h.latitud)) * " +
            "cos(radians(h.longitud) - radians(:lonUser)) + " +
            "sin(radians(:latUser)) * sin(radians(h.latitud)))) < :radioKm",
            nativeQuery = true)
    List<Hecho> buscarPorRadio(@Param("latUser") double latUser,
                               @Param("lonUser") double lonUser,
                               @Param("radioKm") double radioKm);
}