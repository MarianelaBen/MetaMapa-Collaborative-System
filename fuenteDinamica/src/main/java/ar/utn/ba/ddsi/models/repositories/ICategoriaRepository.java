package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Categoria;

public interface ICategoriaRepository {

 Categoria findById(Integer id);
}
