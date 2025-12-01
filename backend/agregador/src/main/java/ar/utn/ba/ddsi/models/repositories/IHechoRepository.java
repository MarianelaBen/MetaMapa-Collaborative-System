package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<Hecho> findByContribuyente_Id(Long id);

    Optional<Hecho> findByTitulo(String titulo);
    //List<Hecho> findByContribuyente_IdAndFueEliminadoFalseOrderByFechaCargaDesc(Long contribuyenteId);
    //List<Hecho> findAllByContribuyenteWithJoins(@Param("contribuyenteId") Long contribuyenteId);
    Optional<Hecho> findByIdEnFuente(Long idEnFuente);
}
