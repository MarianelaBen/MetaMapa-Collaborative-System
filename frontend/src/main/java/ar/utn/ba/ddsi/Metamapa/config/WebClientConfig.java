package ar.utn.ba.ddsi.Metamapa.config;

import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.List;

/*
public class WebClientConfig {

  @Configuration
  public class WebClientConfig {

    @Bean
    public WebClient backendWebClient(@Value("${backend.base-url}") String baseUrl) {
      return WebClient.builder()
          .baseUrl(baseUrl)
          .defaultHeaders(h -> {
            h.setAccept(List.of(MediaType.APPLICATION_JSON));
          })
          .build();
    }
  }
}
*/