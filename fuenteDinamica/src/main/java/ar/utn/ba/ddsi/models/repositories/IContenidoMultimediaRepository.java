package ar.utn.ba.ddsi.models.repositories;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.ContenidoMultimedia;

public interface IContenidoMultimediaRepository {
  ContenidoMultimedia findById(Long id);
  Long save(ContenidoMultimedia contenidoMultimedia);
  Long generarNuevoId();
  void delete(Long idContenidoMultimedia);
}
