package ar.utn.ba.ddsi.adapters;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoFuenteExterna;
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

    if (hechosDTO!=null) {
      hechosDTO.forEach(h -> h.setFuenteExterna(TipoFuenteExterna.INSTANCIA_META_MAPA));
      return hechosDTO;
      }
      else {
        return List.of();
      }
    }
}
