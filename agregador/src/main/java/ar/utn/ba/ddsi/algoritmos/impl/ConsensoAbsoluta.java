package ar.utn.ba.ddsi.algoritmos.impl;

import ar.utn.ba.ddsi.algoritmos.IAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConsensoAbsoluta implements IAlgoritmoDeConsenso {

  @Override
  public void calcularConsenso(Coleccion coleccion, Map<Fuente, List<Hecho>> hechosPorFuente) {
    Set<Fuente> fuentesColeccion = coleccion.getFuentes();

    if (fuentesColeccion.isEmpty()) {
      marcarTodosLosHechosComoNoConsensuados(coleccion);
      return;
    }

    Map<String, List<Hecho>> hechosPorTitulo = new HashMap<>();

    // Recopilar todos los hechos de las fuentes de esta colección
    for (Fuente fuente : fuentesColeccion) {
      List<Hecho> hechosDeFuente = hechosPorFuente.getOrDefault(fuente, new ArrayList<>());
      for (Hecho hecho : hechosDeFuente) {
        if (!hecho.isFueEliminado()) {
          hechosPorTitulo.computeIfAbsent(hecho.getTitulo(), k -> new ArrayList<>()).add(hecho);
        }
      }
    }

    // Verificar consenso absoluto para cada grupo de hechos con el mismo título
    for (Map.Entry<String, List<Hecho>> entry : hechosPorTitulo.entrySet()) {
      String titulo = entry.getKey();
      List<Hecho> hechosConMismoTitulo = entry.getValue();

      boolean hayConsensoAbsoluto = verificarConsensoAbsoluto(titulo, fuentesColeccion, hechosPorFuente);

      // Marcar todos los hechos con este título según el resultado del consenso
      for (Hecho hecho : hechosConMismoTitulo) {
        hecho.setConsensuado(hayConsensoAbsoluto);
      }
    }

    /*Marcar como no consensuados los hechos que no aparecen en todas las fuentes
    marcarHechosNoPresentes(coleccion, hechosPorTitulo, fuentesColeccion);*/
  }

  private boolean verificarConsensoAbsoluto(String titulo, Set<Fuente> fuentesColeccion, Map<Fuente, List<Hecho>> hechosPorFuente) {
    Hecho hechoReferencia = null;

    // Verificar que todas las fuentes tengan un hecho con este título
    for (Fuente fuente : fuentesColeccion) {
      List<Hecho> hechosDeFuente = hechosPorFuente.getOrDefault(fuente, new ArrayList<>());
      Hecho hechoEnFuente = hechosDeFuente.stream()
          .filter(h -> titulo.equals(h.getTitulo()) && !h.isFueEliminado())
          .findFirst()
          .orElse(null);

      if (hechoEnFuente == null) {
        return false; // Si alguna fuente no tiene el hecho, no hay consenso absoluto
      }

      if (hechoReferencia == null) {
        hechoReferencia = hechoEnFuente;
      } else {
        // Verificar que los hechos sean equivalentes (mismo contenido)
        if (!sonHechosEquivalentes(hechoReferencia, hechoEnFuente)) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean sonHechosEquivalentes(Hecho hecho1, Hecho hecho2) {
    return Objects.equals(hecho1.getTitulo(), hecho2.getTitulo()) &&
        Objects.equals(hecho1.getDescripcion(), hecho2.getDescripcion()) &&
        Objects.equals(hecho1.getCategoria(), hecho2.getCategoria()) &&
        Objects.equals(hecho1.getUbicacion(), hecho2.getUbicacion()) &&
        Objects.equals(hecho1.getFechaAcontecimiento(), hecho2.getFechaAcontecimiento());
  }

  private void marcarTodosLosHechosComoNoConsensuados(Coleccion coleccion) {
    coleccion.getHechos().forEach(hecho -> hecho.setConsensuado(false));
  }

  /*private void marcarHechosNoPresentes(Coleccion coleccion, Map<String, List<Hecho>> hechosPorTitulo, Set<Fuente> fuentesColeccion) {

  } */

  @Override
  public TipoAlgoritmoDeConsenso getTipo() {
    return TipoAlgoritmoDeConsenso.CONSENSO_ABSOLUTO;
  }
}
