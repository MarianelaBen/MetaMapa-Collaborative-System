package ar.utn.ba.ddsi.algoritmos.impl;

import ar.utn.ba.ddsi.algoritmos.IAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ConsensoMultiplesMenciones implements IAlgoritmoDeConsenso {

  @Override
  public void calcularConsenso(Coleccion coleccion, Map<Fuente, List<Hecho>> hechosPorFuente) {
    Set<Fuente> fuentesColeccion = coleccion.getFuentes();

    if (fuentesColeccion.size() < 2) {
      marcarTodosLosHechosComoNoConsensuados(coleccion);
      return;
    }

    Map<String, List<Hecho>> hechosPorTitulo = new HashMap<>();

    for (Fuente fuente : fuentesColeccion) {
      List<Hecho> hechosDeFuente = hechosPorFuente.getOrDefault(fuente, new ArrayList<>());
      for (Hecho hecho : hechosDeFuente) {
        if (!hecho.isFueEliminado()) {
          hechosPorTitulo.computeIfAbsent(hecho.getTitulo(), h -> new ArrayList<>()).add(hecho);
        }
      }
    }

    // Verificar consenso por múltiples menciones para cada grupo de hechos
    for (Map.Entry<String, List<Hecho>> entry : hechosPorTitulo.entrySet()) {
      String titulo = entry.getKey();
      List<Hecho> hechosConMismoTitulo = entry.getValue();

      boolean hayConsenso = verificarConsensoMultiplesMenciones(titulo, fuentesColeccion, hechosPorFuente);

      // Marcar todos los hechos con este título según el resultado del consenso
      hechosConMismoTitulo.forEach(hecho -> hecho.setConsensuado(hayConsenso));
    }
  }

  private boolean verificarConsensoMultiplesMenciones(String titulo, Set<Fuente> fuentesColeccion, Map<Fuente, List<Hecho>> hechosPorFuente) {
    Map<String, Set<Fuente>> fuentesPorContenido = new HashMap<>();

    // Recopilar las fuentes que tienen cada variante del hecho
    for (Fuente fuente : fuentesColeccion) {
      List<Hecho> hechosDeFuente = hechosPorFuente.getOrDefault(fuente, new ArrayList<>());

      Hecho hechoEnFuente = hechosDeFuente.stream()
          .filter(h -> titulo.equals(h.getTitulo()) && !h.isFueEliminado())
          .findFirst()
          .orElse(null);

      if (hechoEnFuente != null) {
        String claveContenido = generarClaveContenido(hechoEnFuente);
        fuentesPorContenido.computeIfAbsent(claveContenido, h -> new HashSet<>()).add(fuente);
      }
    }

    // Verificar la condición: al menos una variante debe tener 2+ fuentes y ninguna otra variante debe existir
    if (fuentesPorContenido.size() == 1) {
      // Solo hay una variante del hecho
      Set<Fuente> fuentesConHecho = fuentesPorContenido.values().iterator().next();
      return fuentesConHecho.size() >= 2;
    } else if (fuentesPorContenido.size() > 1) {
      // Hay múltiples variantes con diferente contenido, no hay consenso
      return false;
    }

    return false; // No hay hechos con este título
  }

  private String generarClaveContenido(Hecho hecho) {
    // Generar una clave única basada en el contenido del hecho
    return String.format("%s|%s|%s|%s|%s",
        hecho.getTitulo(),
        hecho.getDescripcion(),
        hecho.getCategoria() != null ? hecho.getCategoria().toString() : "null",
        hecho.getUbicacion() != null ? hecho.getUbicacion().toString() : "null",
        hecho.getFechaAcontecimiento() != null ? hecho.getFechaAcontecimiento().toString() : "null"
    );
  }

  private void marcarTodosLosHechosComoNoConsensuados(Coleccion coleccion) {
    coleccion.getHechos().forEach(hecho -> hecho.setConsensuado(false));
  }

  @Override
  public TipoAlgoritmoDeConsenso getTipo() {
    return TipoAlgoritmoDeConsenso.MULTIPLES_MENCIONES;
  }
}
