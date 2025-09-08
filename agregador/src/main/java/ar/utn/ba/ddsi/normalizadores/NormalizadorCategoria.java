package ar.utn.ba.ddsi.normalizadores;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import ar.utn.ba.ddsi.services.IRaeService;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class NormalizadorCategoria {

  private final ICategoriaRepository categoriaRepository;
  private final Normalizador normalizador;
  private final IRaeService diccionario;

  private static final Map<String ,String> categoriaPorSinonimo = Map.ofEntries(
      Map.entry("fuego", "incendio"),
      Map.entry("fuego forestal", "incendio forestal")
  );

  public NormalizadorCategoria(ICategoriaRepository categoriaRepository, Normalizador normalizador, IRaeService diccionario){
    this.categoriaRepository = categoriaRepository;
    this.normalizador = normalizador;
    this.diccionario = diccionario;
  }

  public Categoria normalizarCategoria(Categoria categoria) {
    if(categoria == null) {
      return null;
    }
    String normalizada = normalizador.normalizar(categoria.getNombre());
    if(normalizada == null || normalizada.isBlank()){
      throw new IllegalArgumentException("Nombre de categoria invalido ");
    }

    //primero se fija en nuestro diccionario
    String candidato = categoriaPorSinonimo.getOrDefault(normalizada, normalizada);

    //busca en BD (ya no hay tildes)
    var encontrada = categoriaRepository.findByNombreIgnoreCase(candidato);
    if(encontrada.isPresent()) return encontrada.get();

  /*  si es una sola palabra, agrega tilde
    if (!candidato.contains(" ")) {
      candidato = diccionario.lemaConAcento(candidato).orElse(candidato);
    }*/

    for (String s : diccionario.sinonimos(normalizada)) {               // ya vienen normalizados por tu servicio
      String alt = categoriaPorSinonimo.getOrDefault(s, s);      // se fija si alguno de los sinonimos esta en nuestro diccionario
      var porSinonimo = categoriaRepository.findByNombreIgnoreCase(alt); //se fija si alguno de los sinonimos esta en el repositorio
      if (porSinonimo.isPresent()) return porSinonimo.get();
    }

    //TODO save
    throw new IllegalArgumentException("Categoria invalida: " + categoria.getNombre());

  }

}
