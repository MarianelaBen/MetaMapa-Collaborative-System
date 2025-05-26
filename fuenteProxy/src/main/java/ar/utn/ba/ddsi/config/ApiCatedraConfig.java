package ar.utn.ba.ddsi.config;

import ar.utn.ba.ddsi.services.impl.LoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class ApiCatedraConfig {

  private final LoginService loginService;

  public ApiCatedraConfig(LoginService loginService) {
    this.loginService = loginService;
  }

  @Bean
  public WebClient apiClient(WebClient.Builder builder) {
    return builder
        .baseUrl("https://api-ddsi.disilab.ar/public/api")
        .filter((req, next) ->
            Mono.fromSupplier(loginService::getToken)
                .flatMap(token -> {
                  ClientRequest authed = ClientRequest.from(req)
                      .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                      .build();
                  return next.exchange(authed);
                })
        )
        .build();
  }
}