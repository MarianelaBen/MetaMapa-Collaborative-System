package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.input.HechoDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.services.impl.ApiCatedraService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//ADAPTADOR
public class AdapterCatedra implements IFuenteProxyAdapter {


  private ApiCatedraService apiCatedraService;
  private List<Hecho> hechos = new ArrayList<>();

  public AdapterCatedra(ApiCatedraService apiCatedraService, IFuenteProxyAdapter adapter) {
    this.apiCatedraService = apiCatedraService;
  }

  @Override
  public List<Hecho> getHechos() {
    List<HechoDTO> hechosDTO = apiCatedraService.obtenerHechos().block();
    //  bloqueamos para esperar el resultado porque obtenerHechos devuelve un Mono<List<HechoDTO>>

    if (hechosDTO != null) {
      return hechosDTO.stream().map(hDTO -> hDTO.toHecho()).collect(Collectors.toList());
    }
    else {
      return List.of();
    }
  }
}
