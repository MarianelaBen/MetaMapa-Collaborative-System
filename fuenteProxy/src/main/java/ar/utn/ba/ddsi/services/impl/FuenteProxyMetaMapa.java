package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.adapters.IFuenteProxyAdapter;
import ar.utn.ba.ddsi.models.dtos.input.HechoDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

 //ADAPTADOR
public class FuenteProxyMetaMapa implements IFuenteProxyAdapter {

    private ApiMetaMapaService apiMetaMapaService;
    private List<Hecho> hechos = new ArrayList<>();

    public FuenteProxyMetaMapa(ApiMetaMapaService apiMetaMapaService) {
      this.apiMetaMapaService= apiMetaMapaService;
    }

    @Override
    public List<Hecho> getHechos() {
      List<HechoDTO> hechosDTO =  apiMetaMapaService.obtenerHechos().block();
      //  bloqueamos para esperar el resultado porque obtenerHechos devuelve un Mono<List<HechoDTO>>

      if (hechosDTO!=null) {
        return this.hechos = hechosDTO.stream().map(hDTO -> hDTO.toHecho()).collect(Collectors.toList());
      }
      else {
        return List.of();
      }
    }
}
