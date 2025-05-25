package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoDTO;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.fuentes.Fuente;
import ar.utn.ba.ddsi.models.entities.fuentes.TipoFuente;
import ar.utn.ba.ddsi.services.IAgregadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgregadorService implements IAgregadorService {


  private List<Fuente> fuentes;

  @Autowired
  public AgregadorService(List<Fuente> fuentes){
    this.fuentes = fuentes; //inyectamos todas las fuentes en el agregador
  }

  @Override
  public List<HechoDTO> obtenerHechosDeFuentes(List<Fuente> fuentes) {
    return fuentes.stream().flatMap(f -> f.getHechos().stream()).collect(Collectors.toList());
  }
  @Override
  public List<HechoDTO> obtenerHechosPorTipoDeFuente(TipoFuente tipo) {
    return fuentes.stream().filter(f -> f.getTipo() == tipo).flatMap(f -> f.getHechos().stream()).collect(Collectors.toList());
  }

 /* @Override
  public List<Hecho> obtenerColeccionesDeFuenteProxy(Fuente fuenteProxy, List<Coleccion> colecciones) {
    return fuentes.stream().flatMap(f -> f.getColecciones().stream()).collect(Collectors.toList());
  }*/

}
