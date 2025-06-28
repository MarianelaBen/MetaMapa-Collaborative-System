package ar.utn.ba.ddsi.modosDeNavegacion.impl;

import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.modosDeNavegacion.IModoDeNavegacion;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModoCurado implements IModoDeNavegacion {

  @Override
  public List<Hecho> aplicarModo(List<Hecho> hechos) {
    return hechos.stream()
        .filter(Hecho::isConsensuado)
        .collect(Collectors.toList());
  }

  @Override
  public TipoDeModoNavegacion getTipo(){
    return TipoDeModoNavegacion.CURADO;
  }
}
