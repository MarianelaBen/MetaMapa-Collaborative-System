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
    //TODO tenemos que ver si se tienen que generar handlers de forma automatica
    //TODO coleccion.setHandle();
    colecciones.add(coleccion);
  }

  @Override
  public List<Coleccion> findAll(){
    return colecciones;
  }


}
