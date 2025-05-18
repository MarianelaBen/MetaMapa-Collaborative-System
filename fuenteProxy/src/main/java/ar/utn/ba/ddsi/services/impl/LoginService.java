package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.LoginDTO;
import ar.utn.ba.ddsi.models.dtos.input.LoginResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LoginService {

  private final WebClient authClient;
  private String token;

  public LoginService(WebClient.Builder webClientBuilder) {
    this.authClient = webClientBuilder
        .baseUrl("https://api-ddsi.disilab.ar/public/api")
        .defaultHeader("Accept", "application/json")
        .defaultHeader("Content-Type", "application/json")
        .build();
  }

  public Mono<LoginResponseDTO> loginAndStore(LoginDTO creds) {
    return authClient.post()
        .uri("/login")
        .bodyValue(creds)
        .retrieve()
        .bodyToMono(LoginResponseDTO.class)
        .map(resp -> {
          // almaceno token
          this.token  = resp.getData().getAccessToken();
          return resp;
        });
  }

  public String getToken() {
    if (token == null) {
      throw new IllegalStateException("No se ha hecho login todavía");
    }
    return token;
  }
}