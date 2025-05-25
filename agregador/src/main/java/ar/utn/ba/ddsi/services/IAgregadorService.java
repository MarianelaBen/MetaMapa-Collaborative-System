package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoFuente;
import java.util.List;

public interface IAgregadorService {
  public List<HechoInputDTO> obtenerHechosDeFuentes();
  public List <HechoInputDTO> obtenerHechosPorTipoDeFuente(TipoFuente tipo);
  public void agregarFuente(Fuente fuente);
}
