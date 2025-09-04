package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IEtiquetaRepository extends JpaRepository<Etiqueta, Long> {
  Optional<Etiqueta> findByNombreIgnoreCase(String nombre);

}
