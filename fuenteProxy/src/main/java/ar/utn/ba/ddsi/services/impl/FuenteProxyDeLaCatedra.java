package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.fuentes.Fuente;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FuenteProxyDeLaCatedra extends Fuente {
  //despues tendriamos que hacer otra que se llame FuenteProxyMetaMapa y va a poder usar los DTOS ya implementados
  private ApiCatedraService apiCatedraService;
  private List<Hecho> hechos = new ArrayList<>();

  public FuenteProxyDeLaCatedra(ApiCatedraService apiCatedraService) {
    this.apiCatedraService = apiCatedraService;
  }

  @Override
  public void leerHechos() {
    List<HechoDTO> hechosDTO =  apiCatedraService.obtenerHechos().block();
    //  bloqueamos para esperar el resultado porque obtenerHechos devuelve un Mono<List<HechoDTO>>

    if (hechosDTO!=null) {
      this.hechos = hechosDTO.stream().map(hDTO -> hDTO.toHecho()).collect(Collectors.toList());
    }
  }

  @Override
  public List<Hecho> getHechos() {
    return this.hechos;
    //a este metodo llama el agregador
  }
}

//TODO chequear donde va esta clase