package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.fuentes.Fuente;
import ar.utn.ba.ddsi.models.entities.fuentes.TipoFuente;
import java.util.List;

public interface IAgregadorService {
  public List<Hecho> obtenerHechosDeFuentes(List<Fuente> fuentes);
  public List <Hecho> obtenerHechosPorTipoDeFuente(TipoFuente tipo);
}
