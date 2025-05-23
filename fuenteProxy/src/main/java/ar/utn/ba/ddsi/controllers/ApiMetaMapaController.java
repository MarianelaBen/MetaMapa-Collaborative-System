package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.HechoDTO;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.services.impl.ApiMetaMapaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public Mono<List<HechoDTO>> obtenerHechos(){
    return apiMetaMapaService.obtenerHechos();
  }

  @GetMapping("/colecciones")
  public Mono<List<Coleccion>> obtenerColecciones(){
    return apiMetaMapaService.obtenerColeccion();
  }



  @GetMapping("coleccion/:identificador/hechos")
  public Mono<List<HechoDTO>> obtenerHechosDeColeccion(@PathVariable long id){

  }
}

//ENDPOINTS
/*GET /hechos
GET /colecciones
GET /colecciones/:identificador/hechos
POST /solicitudes
*/

