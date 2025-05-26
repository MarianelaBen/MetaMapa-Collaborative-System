package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;

public interface ICategoriaRepository {

 Categoria findById(Long id);
 Long save(Categoria categoria);
 Long generarNuevoId();
}
