package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IHechoRepository extends JpaRepository<Hecho,Long> {

  @Query("""
    SELECT h FROM Hecho h
    WHERE (:contribuyenteId IS NULL OR h.contribuyente.idContribuyente = :contribuyenteId)
      AND (:titulo IS NULL OR LOWER(h.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
      AND (:categoria IS NULL OR h.categoria.nombre = :categoria)
      AND h.fueEliminado = FALSE
""")
  List<Hecho> buscarFiltrado(
      Long contribuyenteId,
      String titulo,
      String categoria
  );


}
