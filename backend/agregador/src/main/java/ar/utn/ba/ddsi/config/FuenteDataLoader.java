package ar.utn.ba.ddsi.config;

import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoFuente;
import ar.utn.ba.ddsi.models.repositories.IFuenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FuenteDataLoader implements CommandLineRunner{
  private final IFuenteRepository fuenteRepository;

  @Override
  public void run(String... args) {

    if (fuenteRepository.count() > 0) {
      return;
    }

    Fuente dinamica = new Fuente();
    dinamica.setTipo(TipoFuente.DINAMICA);
    dinamica.setUrl("http://localhost:8084/api/hechos");
    fuenteRepository.save(dinamica);

    Fuente estatica = new Fuente();
    estatica.setTipo(TipoFuente.ESTATICA);
    estatica.setUrl("http://localhost:8081/api/hechos");
    fuenteRepository.save(estatica);

    Fuente proxy = new Fuente();
    proxy.setTipo(TipoFuente.PROXY);
    proxy.setUrl("http://localhost:8082/api/hechos");
    fuenteRepository.save(proxy);

    System.out.println("=== Fuentes iniciales creadas ===");
  }
}
