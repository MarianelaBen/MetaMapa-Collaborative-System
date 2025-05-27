package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import reactor.core.publisher.Mono;
import java.util.List;

public interface IApiCatedraService {
  Mono<List<HechoInputDTO>> obtenerHechos();
  Mono<HechoInputDTO> obtenerHechoPorId(long id);
}
