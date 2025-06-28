package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.impl.AgregadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping
public class AgregadorController {

  private final IAgregadorService agregadorService;

  public AgregadorController(IAgregadorService agregadorService){
    this.agregadorService = agregadorService;
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
  public List<HechoOutputDTO> getHechosPorColeccion(@PathVariable String coleccionId,
      @RequestParam(value = "modo", defaultValue = "IRRESTRICTA") String modoStr) { //valor predeterminado IRRESTRICTA por si no se especifica nada de cuial se quiere usar

    TipoDeModoNavegacion modo = TipoDeModoNavegacion.valueOf(modoStr);
    return agregadorService.obtenerHechosPorColeccion(coleccionId, modo);
  }
}
