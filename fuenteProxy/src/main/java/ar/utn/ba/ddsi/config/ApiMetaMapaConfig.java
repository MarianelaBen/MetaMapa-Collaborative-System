package ar.utn.ba.ddsi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ApiMetaMapaConfig {

  @Bean(name="apiMetaMapaClient")
  public WebClient apiClient(WebClient.Builder builder) {
    return builder
        .baseUrl("https://metaMapa.com/api").build(); //todavia no sabemos la URL

  }
}

//Si la api necesita autenticacion como la de la catedra tendriamos que cambiar esto
