package ar.utn.ba.ddsi.normalizadores;

import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class NormalizadorCategoria {

  private final ICategoriaRepository categoriaRepository;
  private final Normalizador normalizador;

  private static final Map<String ,String> categoriaPorSinonimo = Map.ofEntries(
      Map.entry("fuego", "incendio"),
      Map.entry("fuego forestal", "incendio forestal")
  );

  public NormalizadorCategoria(ICategoriaRepository categoriaRepository, Normalizador normalizador){
    this.categoriaRepository = categoriaRepository;
    this.normalizador = normalizador;
  }
  public Categoria normalizarCategoria(Categoria categoria) {
    if(categoria == null) {
      return null;
    }
    String normalizada = normalizador.normalizar(categoria.getNombre());
    if(normalizada == null){
      throw new IllegalArgumentException("Nombre de categoria invalido ");
    }

    String sinonimo = categoriaPorSinonimo.getOrDefault(normalizada, normalizada);
    return categoriaRepository.findByNombreIgnoreCase(sinonimo)
        .orElseThrow(() -> new IllegalArgumentException("Categoria invalida: " + categoria.getNombre() ));
  }

}
