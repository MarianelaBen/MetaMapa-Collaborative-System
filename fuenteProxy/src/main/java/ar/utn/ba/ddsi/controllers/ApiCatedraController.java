package ar.utn.ba.ddsi.controllers;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.services.impl.ApiCatedraService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/api/hechos")
public class ApiCatedraController {

  private final ApiCatedraService apiCatedraService;

  public ApiCatedraController(ApiCatedraService apiCatedraService) {
    this.apiCatedraService = apiCatedraService;
  }

  @GetMapping
  public Mono<List<HechoInputDTO>> obtenerHechos() {
    return apiCatedraService.obtenerHechos();
  }
  @GetMapping("/{id}")
  public Mono<HechoInputDTO> obtenerHechoPorId(@PathVariable long id) {
    return apiCatedraService.obtenerHechoPorId(id);
  }
}
