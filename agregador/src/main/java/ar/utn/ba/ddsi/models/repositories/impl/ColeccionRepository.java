package ar.utn.ba.ddsi.models.repositories.impl;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ColeccionRepository implements IColeccionRepository {
  private List<Coleccion> colecciones = new ArrayList<>();

  @Override
  public void save(Coleccion coleccion){
    colecciones.add(coleccion);
    //TODO no se como funciona el handle, si se pone como atributo en la entidad de dominio o donde
  }

  @Override
  public List<Coleccion> findAll(){
    return colecciones;
  }
}
