package ar.utn.ba.ddsi.algoritmos.impl;

import ar.utn.ba.ddsi.algoritmos.IAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoAlgoritmoDeConsenso;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ConsensoMayoriaSimple implements IAlgoritmoDeConsenso {

  @Override
  public void calcularConsenso(Coleccion coleccion, Map<Fuente, List<Hecho>> hechosPorFuente) {
    Set<Fuente> fuentesColeccion = coleccion.getFuentes();

    if (fuentesColeccion.isEmpty()) {
      marcarTodosLosHechosComoNoConsensuados(coleccion);
      return;
    }

    // Calcular la mayoría simple
    int totalFuentes = fuentesColeccion.size();
    int mayoriaRequerida = (totalFuentes / 2) + (totalFuentes % 2); // Redondeo para arriba

    // Agrupar hechos por título
    Map<String, List<Hecho>> hechosPorTitulo = new HashMap<>();

    for (Fuente fuente : fuentesColeccion) {
      List<Hecho> hechosDeFuente = hechosPorFuente.getOrDefault(fuente, new ArrayList<>());
      for (Hecho hecho : hechosDeFuente) {
        if (!hecho.isFueEliminado()) {
          hechosPorTitulo.computeIfAbsent(hecho.getTitulo(), k -> new ArrayList<>()).add(hecho);
        }
      }
    }

    // Verificar consenso por mayoría simple para cada grupo de hechos
    for (Map.Entry<String, List<Hecho>> entry : hechosPorTitulo.entrySet()) {
      String titulo = entry.getKey();
      List<Hecho> hechosConMismoTitulo = entry.getValue();

      // Contar cuántas fuentes diferentes tienen este hecho
      //Map<String, Integer> conteoHechosPorContenido = contarHechosPorContenido(titulo, fuentesColeccion, hechosPorFuente);
      Map<Hecho, Integer> conteoHechosPorContenido = contarHechosPorContenido(titulo, fuentesColeccion, hechosPorFuente);

      // Verificar si alguna variante del hecho alcanza la mayoría
      boolean hayMayoria = conteoHechosPorContenido.values().stream()
          .anyMatch(count -> count >= mayoriaRequerida);

      // Marcar consenso solo para los hechos que alcancen la mayoría
      if (hayMayoria) {
        marcarHechosConMayoria(hechosConMismoTitulo, conteoHechosPorContenido, mayoriaRequerida);
      } else {
        hechosConMismoTitulo.forEach(hecho -> hecho.setConsensoParaAlgoritmo(TipoAlgoritmoDeConsenso.MAYORIA_SIMPLE,false));
      }
    }
  }

  private Map<Hecho, Integer> contarHechosPorContenido(String titulo, Set<Fuente> fuentesColeccion, Map<Fuente, List<Hecho>> hechosPorFuente) {
    //cambie string por hecho
    //Map<String, Integer> conteo = new HashMap<>();
    Map<Hecho, Integer> conteo = new HashMap<>();

    for (Fuente fuente : fuentesColeccion) {
      List<Hecho> hechosDeFuente = hechosPorFuente.getOrDefault(fuente, new ArrayList<>());

      // Buscar el hecho con este título en esta fuente
      Hecho hechoEnFuente = hechosDeFuente.stream()
          .filter(h -> titulo.equals(h.getTitulo()) && !h.isFueEliminado())
          .findFirst()
          .orElse(null);

      if (hechoEnFuente != null) {
        Hecho hechoExistente = conteo.keySet().stream()
            .filter(h -> h.esIgualContenido(hechoEnFuente))
            .findFirst()
            .orElse(null);
       /* String claveContenido = generarClaveContenido(hechoEnFuente);
        conteo.put(claveContenido, conteo.getOrDefault(claveContenido, 0) + 1);*/

        if (hechoExistente != null) {
          // Ya existe un hecho con el mismo contenido, incrementar el contador
          conteo.put(hechoExistente, conteo.get(hechoExistente) + 1);
        } else {
          // Es un nuevo tipo de contenido, agregar con contador 1
          conteo.put(hechoEnFuente, 1);
        }
      }
    }

    return conteo;
  }

  /*private String generarClaveContenido(Hecho hecho) {
    // Generar una clave única basada en el contenido del hecho
    return String.format("%s|%s|%s|%s|%s",
        hecho.getTitulo(),
        hecho.getDescripcion(),
        hecho.getCategoria() != null ? hecho.getCategoria().toString() : "null",
        hecho.getUbicacion() != null ? hecho.getUbicacion().toString() : "null",
        hecho.getFechaAcontecimiento() != null ? hecho.getFechaAcontecimiento().toString() : "null"
    );
  }*/

  private void marcarHechosConMayoria(List<Hecho> hechos, Map<Hecho, Integer> conteoHechosPorContenido, int mayoriaRequerida) {
                                                            //cambie string por hecho
    for (Hecho hecho : hechos) {
      /*String claveContenido = generarClaveContenido(hecho);
      int conteo = conteoHechosPorContenido.getOrDefault(claveContenido, 0);
      hecho.setConsensuado(conteo >= mayoriaRequerida);*/
      boolean tieneConsenso = conteoHechosPorContenido.entrySet().stream()
          .anyMatch(entry -> entry.getKey().esIgualContenido(hecho) && entry.getValue() >= mayoriaRequerida);

      hecho.setConsensoParaAlgoritmo(TipoAlgoritmoDeConsenso.MAYORIA_SIMPLE, tieneConsenso);
    }
  }

  private void marcarTodosLosHechosComoNoConsensuados(Coleccion coleccion) {
    coleccion.getHechos().forEach(hecho -> hecho.setConsensoParaAlgoritmo(TipoAlgoritmoDeConsenso.MAYORIA_SIMPLE,false));
  }

  @Override
  public TipoAlgoritmoDeConsenso getTipo() {
    return TipoAlgoritmoDeConsenso.MAYORIA_SIMPLE;
  }
}

