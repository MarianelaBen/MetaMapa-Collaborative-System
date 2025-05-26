package ar.utn.ba.ddsi.controllers;
/*
import ar.utn.ba.ddsi.models.dtos.input.ColeccionInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.services.impl.ApiMetaMapaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping
public class ApiMetaMapaController {
  private ApiMetaMapaService apiMetaMapaService;

  public ApiMetaMapaController(ApiMetaMapaService apiMetaMapaService){
    this.apiMetaMapaService = apiMetaMapaService;
  }

  @GetMapping("/hechos")
  public Mono<List<HechoInputDTO>> obtenerHechos(){
    return apiMetaMapaService.obtenerHechos();
  }

  @GetMapping("/colecciones")
  public Mono<List<ColeccionInputDTO>> obtenerColecciones(){
    return apiMetaMapaService.obtenerColecciones();
  }

  @GetMapping("colecciones/{id}/hechos")
  public Mono<List<HechoInputDTO>> obtenerHechosDeColeccion(@PathVariable long id){
    return apiMetaMapaService.obtenerColeccionPorId(id)
        .map(ColeccionInputDTO::getHechosDeLaColeccion);
  }

  @PostMapping("/solicitudes")
  public Mono<List<SolicitudInputDTO>> crearSolicitud(@RequestBody SolicitudInputDTO solicitud) {
    return apiMetaMapaService.crearSolicitud(solicitud);
  }


}
*/

