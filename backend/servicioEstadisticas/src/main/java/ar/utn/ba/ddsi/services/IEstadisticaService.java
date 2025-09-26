package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.output.CategoriaOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HoraOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ProvinciaOutputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import java.util.List;
import java.util.Set;

public interface IEstadisticaService {
  public void recalcularEstadisticas();
  ProvinciaOutputDTO provinciaConMasHechosEnColeccion(String coleccionHandle);
  CategoriaOutputDTO categoriaConMasHechos(List<Hecho> hechos);
  ProvinciaOutputDTO provinciaConMasHechosParaCategoria(Long categoriaId, List<Hecho> hechos);
  HoraOutputDTO horaConMasHechosParaCategoria(Long categoriaId, List<Hecho> hechos);
  long contarSolicitudesEliminacionSpam();
}
