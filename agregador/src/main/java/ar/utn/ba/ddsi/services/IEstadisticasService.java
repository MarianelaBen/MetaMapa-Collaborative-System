package ar.utn.ba.ddsi.services;


import ar.utn.ba.ddsi.models.dtos.output.CategoriaOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HoraOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ProvinciaOutputDTO;
import ar.utn.ba.ddsi.models.entities.Categoria;

public interface IEstadisticasService {
  public void recalcularEstadisticas();
  ProvinciaOutputDTO provinciaConMasHechosEnColeccion(String coleccionHandle);
  CategoriaOutputDTO categoriaConMasHechos();
  ProvinciaOutputDTO provinciaConMasHechosParaCategoria(Long categoriaId);
  HoraOutputDTO horaConMasHechosParaCategoria(Long categoriaId);
  long contarSolicitudesEliminacionSpam();
}
