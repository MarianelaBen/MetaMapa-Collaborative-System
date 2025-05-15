package ar.utn.ba.ddsi.models.repositories.impl;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ColeccionRepository implements IColeccionRepository {
  private List<Coleccion> colecciones = new ArrayList<>();

  @Override
    public Coleccion save(Coleccion coleccion){
    if (coleccion.getHandle() == null) {
      // Handle -> título sin espacios
      String nombreSinEspacios = coleccion.getTitulo().replaceAll("\\s+", "");
      String handle = nombreSinEspacios;
      int i = 1;
      // si ya existe, le agregamos un sufijo numérico
      while (existsHandle(handle)) {
        handle = nombreSinEspacios + i++;
      }
      coleccion.setHandle(handle);
    }
    colecciones.add(coleccion);
    return coleccion;
  }

  private boolean existsHandle(String h) {
    return colecciones.stream().anyMatch(c -> h.equals(c.getHandle()));
  }

  @Override
  public List<Coleccion> findAll(){
    return colecciones;
  }

  public void update(Hecho hechoEliminado){
    for(Coleccion coleccion : colecciones){
      coleccion.getHechosDeLaColeccion().remove(hechoEliminado);
    }
  }


}
