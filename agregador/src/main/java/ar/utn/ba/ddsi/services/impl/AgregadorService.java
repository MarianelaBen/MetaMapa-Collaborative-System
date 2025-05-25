package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoFuente;
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
  public List<HechoInputDTO> obtenerHechosDeFuentes(List<Fuente> fuentes) {
    return fuentes.stream().flatMap(f -> f.getHechos().stream()).collect(Collectors.toList());
  }
  @Override
  public List<HechoInputDTO> obtenerHechosPorTipoDeFuente(TipoFuente tipo) {
    return fuentes.stream().filter(f -> f.getTipo() == tipo).flatMap(f -> f.getHechos().stream()).collect(Collectors.toList());
  }



}
