package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.input.HechoDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.services.impl.ApiMetaMapaService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

 //ADAPTADOR
public class AdapterInstanciaMetaMapa implements IFuenteProxyAdapter {

    private ApiMetaMapaService apiMetaMapaService;

    public AdapterInstanciaMetaMapa(ApiMetaMapaService apiMetaMapaService) {
      this.apiMetaMapaService= apiMetaMapaService;
    }

@Override
    public List<HechoDTO> getHechos() {
      List<HechoDTO> hechosDTO =  apiMetaMapaService.obtenerHechos().block();
      //  bloqueamos para esperar el resultado porque obtenerHechos devuelve un Mono<List<HechoDTO>>
        if (hechosDTO!=null) {
          return hechosDTO;
          //saco .stream().map(hDTO -> hDTO.toHecho()).collect(Collectors.toList());
      }
      else {
        return List.of();
      }
    }
}
