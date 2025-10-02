package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IHechoRepository extends JpaRepository<Hecho,Long> {
    @Query(value = "" +
            "SELECT h.* " +
            "FROM hecho h " +
            "JOIN coleccion_hecho ch ON h.id = ch.hecho_id " +
            "WHERE TRIM(ch.coleccion_handle) = TRIM(:coleccionHandle)",
            nativeQuery = true)
    List<Hecho> findByColeccionHandle(@Param("coleccionHandle") String coleccionHandle);
}
