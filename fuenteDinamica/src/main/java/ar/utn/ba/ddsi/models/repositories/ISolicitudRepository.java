package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ISolicitudRepository extends JpaRepository<Solicitud,Long> {
  /*
  void save(Solicitud solicitud);
  Solicitud findById(Long id);
   */
}
