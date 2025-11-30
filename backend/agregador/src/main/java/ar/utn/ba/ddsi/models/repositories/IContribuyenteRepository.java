package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IContribuyenteRepository extends JpaRepository<Contribuyente,Long> {
}
