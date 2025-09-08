package ar.utn.ba.ddsi.normalizadores;

//import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;

@Component
public class RaeClient {

  @Value("${rae.base-url:https://rae-api.com}")
  private String baseUrl;

  @Value("${rae.timeout-ms:2000}")
  private long timeoutMs;

  private final WebClient web;

  public RaeClient(WebClient.Builder builder) {
    this.web = builder.baseUrl(baseUrl).build();
  }


  public Optional<RaeWord> getWord(String word) {
    try {
      return Optional.ofNullable(
          web.get().uri(uri -> uri.path("/api/words/{w}").build(word))
              .retrieve()
              .bodyToMono(RaeWord.class)
              .timeout(Duration.ofMillis(timeoutMs))
              .block()
      );
    } catch (Exception e) {
      return Optional.empty(); // si falla, degradamos sin romper el flujo
    }
  }

  // POJO mínimo
  public static class RaeWord {
    public Data data;
    public static class Data {
      public String word;              // lema con acento: "inundación"
      public List<Meaning> meanings;
    }
    public static class Meaning {
      public List<Sense> senses;
    }
    public static class Sense {
      public List<String> synonyms;    // ["incendio","fuego",...]
    }
  }
}
