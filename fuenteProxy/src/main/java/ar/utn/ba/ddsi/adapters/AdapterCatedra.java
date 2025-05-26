package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.services.impl.ApiCatedraService;
import java.util.List;

//ADAPTADOR
public class AdapterCatedra implements IFuenteProxyAdapter {


  private ApiCatedraService apiCatedraService;


  public AdapterCatedra(ApiCatedraService apiCatedraService, IFuenteProxyAdapter adapter) {
    this.apiCatedraService = apiCatedraService;
  }

  @Override
  public List<HechoInputDTO> getHechos() {
    List<HechoInputDTO> hechosDTO = apiCatedraService.obtenerHechos().block();
    //  bloqueamos para esperar el resultado porque obtenerHechos devuelve un Mono<List<HechoDTO>>

    if (hechosDTO != null) {
      return hechosDTO;
    }
    else {
      return List.of();
    }
  }
}
