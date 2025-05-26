package ar.utn.ba.ddsi.adapters;
/*
import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.services.impl.ApiMetaMapaService;
import java.util.List;

//ADAPTADOR
public class AdapterInstanciaMetaMapa implements IFuenteProxyAdapter {

    private ApiMetaMapaService apiMetaMapaService;

    public AdapterInstanciaMetaMapa(ApiMetaMapaService apiMetaMapaService) {
      this.apiMetaMapaService= apiMetaMapaService;
    }

@Override
    public List<HechoInputDTO> getHechos() {
      List<HechoInputDTO> hechosDTO =  apiMetaMapaService.obtenerHechos().block();
      //  bloqueamos para esperar el resultado porque obtenerHechos devuelve un Mono<List<HechoDTO>>

    if (hechosDTO!=null) {
          return hechosDTO;
      }
      else {
        return List.of();
      }
    }
}
*/