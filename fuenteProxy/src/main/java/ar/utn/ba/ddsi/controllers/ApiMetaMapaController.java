package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.ColeccionDTO;
import ar.utn.ba.ddsi.models.dtos.input.HechoDTO;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudDTO;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
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
  public Mono<List<HechoDTO>> obtenerHechos(){
    return apiMetaMapaService.obtenerHechos();
  }

  @GetMapping("/colecciones")
  public Mono<List<ColeccionDTO>> obtenerColecciones(){
    return apiMetaMapaService.obtenerColecciones();
  }

  @GetMapping("coleccion/:identificador/hechos")
  public Mono<List<HechoDTO>> obtenerHechosDeColeccion(@PathVariable long id){
    return apiMetaMapaService.obtenerColeccionPorId(id)
        .map(ColeccionDTO::getHechos)
        .map(hechos -> hechos.stream()
            .map(this::convertirAHechoDTO)
            .toList());
  }

  @PostMapping("/solicitudes")
  public Mono<List<SolicitudDTO>> crearSolicitud(@RequestBody SolicitudDTO solicitud) {
    return apiMetaMapaService.crearSolicitud(solicitud);
  }

  private HechoDTO convertirAHechoDTO(Hecho hecho) {
    HechoDTO dto = new HechoDTO();
    dto.setId(hecho.getId());
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setCategoria(hecho.getCategoria().getNombre());
    dto.setLatitud(hecho.getUbicacion().getLatitud());
    dto.setLongitud(hecho.getUbicacion().getLongitud());
    dto.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    dto.setFechaCarga(hecho.getFechaCarga());
    return dto;
  }

}

//ENDPOINTS
/*GET /hechos
GET /colecciones
GET /colecciones/:identificador/hechos
POST /solicitudes
*/

