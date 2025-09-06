package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.output.CategoriaOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HoraOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ProvinciaOutputDTO;
import ar.utn.ba.ddsi.services.IEstadisticasService;

public class EstadisticasService implements IEstadisticasService {

  @Override
  public void recalcularEstadisticas() {

  }

  @Override
  public ProvinciaOutputDTO provinciaConMasHechosEnColeccion(String coleccionHandle) {
    return null;
  }

  @Override
  public CategoriaOutputDTO categoriaConMasHechos() {
    return null;
  }

  @Override
  public ProvinciaOutputDTO provinciaConMasHechosParaCategoria(Long categoriaId) {
    return null;
  }

  @Override
  public HoraOutputDTO horaConMasHechosParaCategoria(Long categoriaId) {
    return null;
  }

  @Override
  public long contarSolicitudesEliminacionSpam() {
    return 0;
  }
}
