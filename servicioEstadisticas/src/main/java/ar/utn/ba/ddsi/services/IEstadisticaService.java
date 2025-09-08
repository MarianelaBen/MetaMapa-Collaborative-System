package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.output.CategoriaOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HoraOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ProvinciaOutputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import java.util.Set;

public interface IEstadisticaService {
  public void recalcularEstadisticas();
  ProvinciaOutputDTO provinciaConMasHechosEnColeccion(String coleccionHandle);
  CategoriaOutputDTO categoriaConMasHechos(Set<Fuente> fuentes);
  ProvinciaOutputDTO provinciaConMasHechosParaCategoria(Long categoriaId, Set<Fuente> fuentes);
  HoraOutputDTO horaConMasHechosParaCategoria(Long categoriaId, Set<Fuente> fuentes);
  long contarSolicitudesEliminacionSpam();
}
