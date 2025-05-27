package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.input.LoginDTO;
import ar.utn.ba.ddsi.models.dtos.input.LoginResponseDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public interface ILoginService {
  Mono<LoginResponseDTO> loginAndStore(LoginDTO creds);
  String getToken();
}
