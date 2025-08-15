package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.ISolicitudService;
import ar.utn.ba.ddsi.services.impl.SolicitudService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class AgregadorController {

  private final IAgregadorService agregadorService;
  private final ISolicitudService solicitudService;

  public AgregadorController(IAgregadorService agregadorService, ISolicitudService solicitudService){
    this.agregadorService = agregadorService;
    this.solicitudService = solicitudService;
  }


  @GetMapping("/hechos")
  public List<HechoOutputDTO> getHechos(Set<Fuente> fuentes){
    return agregadorService.obtenerTodosLosHechos(fuentes)
        .stream()
        .map(this::hechoOutputDTO).toList();
  }

  public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    return new HechoOutputDTO(hecho);
  }

  @GetMapping("/colecciones/{coleccionId}/hechos")
  public List<HechoOutputDTO> getHechosPorColeccion(@PathVariable String coleccionId, @RequestParam(value = "modo", defaultValue = "IRRESTRICTA") String modoStr) { //valor predeterminado IRRESTRICTA por si no se especifica nada de cuial se quiere usar

    TipoDeModoNavegacion modo = TipoDeModoNavegacion.valueOf(modoStr);
    List<Hecho> hechos = agregadorService.obtenerHechosPorColeccion(coleccionId, modo);

    return hechos.stream().map(HechoOutputDTO::fromEntity).toList();
  }

  //navegacion filtrada sobre una coleccion
  @GetMapping("/colecciones/{coleccionId}/filtrados")
  public List<HechoOutputDTO> getHechosFiltrados(@PathVariable String coleccionId,
                                                 @RequestParam(required = false) String categoria,
                                                 @RequestParam(required = false) String fechaDesde,
                                                 @RequestParam(required = false) String fechaHasta) {
    return agregadorService.obtenerHechosFiltrados(coleccionId, categoria, fechaDesde, fechaHasta)
        .stream()
        .map(HechoOutputDTO::fromEntity)
        .collect(Collectors.toList());
  }

  @PostMapping("/solicitudes")
  public void crearSolicitudDeEliminacion(@RequestBody SolicitudDeEliminacion solicitud) {
    solicitudService.crearSolicitud(solicitud);
  }



}
