package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.adapters.AdapterFuenteDinamica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteEstatica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteProxy;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.services.IAgregadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AgregadorService implements IAgregadorService {
  @Autowired ColeccionService coleccionService;
private final AdapterFuenteDinamica adapterFuenteDinamica;
private final AdapterFuenteEstatica adapterFuenteEstatica;
private final AdapterFuenteProxy adapterFuenteProxy;

public AgregadorService(AdapterFuenteDinamica adapterFuenteDinamica, AdapterFuenteEstatica adapterFuenteEstatica, AdapterFuenteProxy adapterFuenteProxy){
  this.adapterFuenteDinamica = adapterFuenteDinamica;
  this.adapterFuenteEstatica = adapterFuenteEstatica;
  this.adapterFuenteProxy = adapterFuenteProxy;
}

  @Override
  public List<Hecho> obtenerTodosLosHechos(Set<Fuente> fuentes) {
    //TODO pensar si aca deberia llamar a findAll de un repositorio de fuentes en vez de que se pasen por parametro
    List<Hecho> hechos = fuentes.stream()
        .flatMap( f-> obtenerTodosLosHechosDeFuente(f)
            .stream())
        .collect(Collectors.toList());
    return hechos;
  }

  @Override
  public List<Hecho> obtenerTodosLosHechosDeFuente(Fuente fuente) {
    List<Hecho> hechos = new ArrayList<>();
    switch (fuente.getTipo()){
      case ESTATICA:
        hechos.addAll(adapterFuenteEstatica.obtenerHechos(fuente.getUrl()));
        break;
      case PROXY:

        hechos.addAll(adapterFuenteProxy.obtenerHechos(fuente.getUrl()));
        break;
      case DINAMICA:

        hechos.addAll(adapterFuenteDinamica.obtenerHechos(fuente.getUrl()));
        break;
    }


    return hechos;
  }

  @Override
  public List<HechoOutputDTO> obtenerHechosPorColeccion(String coleccionId, TipoDeModoNavegacion modo){
    List<Hecho> hechos = coleccionService.obtenerHechosPorColeccion(coleccionId, modo);
    return hechos.stream()
        .map(this::hechoOutputDTO).
        toList();
  }

  @Override
  public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    return new HechoOutputDTO(hecho);
  }

}