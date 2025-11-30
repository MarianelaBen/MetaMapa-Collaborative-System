package ar.utn.ba.ddsi.Metamapa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Estrategia de memoria compartida (16MB)
    private final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
            .build();


    @Bean("webClientPublic")
    public WebClient webClientPublic() {
        return WebClient.builder()
                .baseUrl("http://localhost:8083/api/public")
                .exchangeStrategies(strategies)
                .build();
    }


    @Bean("webClientAdmin")
    public WebClient webClientAdmin() {
        return WebClient.builder()
                .baseUrl("http://localhost:8083/api/admin")
                .exchangeStrategies(strategies)
                .build();
    }
}