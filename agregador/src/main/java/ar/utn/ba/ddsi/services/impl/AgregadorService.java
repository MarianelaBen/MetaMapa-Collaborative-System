package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.adapters.AdapterFuenteDinamica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteEstatica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteProxy;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.services.IAgregadorService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgregadorService implements IAgregadorService {
private final AdapterFuenteDinamica adapterFuenteDinamica;
private final AdapterFuenteEstatica adapterFuenteEstatica;
private final AdapterFuenteProxy adapterFuenteProxy;

public AgregadorService(AdapterFuenteDinamica adapterFuenteDinamica, AdapterFuenteEstatica adapterFuenteEstatica, AdapterFuenteProxy adapterFuenteProxy){
  this.adapterFuenteDinamica = adapterFuenteDinamica;
  this.adapterFuenteEstatica = adapterFuenteEstatica;
  this.adapterFuenteProxy = adapterFuenteProxy;
}

  @Override
  public List<Hecho> obtenerTodosLosHechos() {
    List<Hecho> hechos = new ArrayList<>();
    hechos.addAll(adapterFuenteDinamica.obtenerHechos());
    hechos.addAll(adapterFuenteEstatica.obtenerHechos());
    hechos.addAll(adapterFuenteProxy.obtenerHechos());
    return hechos;
}

}