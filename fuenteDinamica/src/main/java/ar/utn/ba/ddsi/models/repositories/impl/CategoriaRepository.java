package ar.utn.ba.ddsi.models.repositories.impl;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository implements ICategoriaRepository {

  private List<Categoria> categorias = new ArrayList<>();

  @Override
  public Categoria findById(Integer id) {
    return categorias
        .stream()
        .filter(c -> c.getId().equals(id))
        .findFirst()
        .orElse(null);
  }
}
