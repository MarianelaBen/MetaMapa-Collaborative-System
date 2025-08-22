package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.adapters.AdapterFuenteDinamica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteEstatica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteProxy;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IColeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AgregadorService implements IAgregadorService {
  @Autowired
  IColeccionService coleccionService;
private final AdapterFuenteDinamica adapterFuenteDinamica;
private final AdapterFuenteEstatica adapterFuenteEstatica;
private final AdapterFuenteProxy adapterFuenteProxy;

public AgregadorService(AdapterFuenteDinamica adapterFuenteDinamica, AdapterFuenteEstatica adapterFuenteEstatica, AdapterFuenteProxy adapterFuenteProxy){
  this.adapterFuenteDinamica = adapterFuenteDinamica;
  this.adapterFuenteEstatica = adapterFuenteEstatica;
  this.adapterFuenteProxy = adapterFuenteProxy;
}

  @Override
  public List<Hecho> obtenerTodosLosHechos(Set<Fuente> fuentes) {
    List<Hecho> hechos = fuentes.stream()
        .flatMap( f-> obtenerTodosLosHechosDeFuente(f)
            .stream())
        .collect(Collectors.toList());
    return hechos;
  }

  @Override
  public List<Hecho> obtenerTodosLosHechosDeFuente(Fuente fuente) {
    List<Hecho> hechos = new ArrayList<>();
    switch (fuente.getTipo()){
      case ESTATICA:
        hechos.addAll(adapterFuenteEstatica.obtenerHechos(fuente.getUrl()));
        break;
      case PROXY:

        hechos.addAll(adapterFuenteProxy.obtenerHechos(fuente.getUrl()));
        break;
      case DINAMICA:

        hechos.addAll(adapterFuenteDinamica.obtenerHechos(fuente.getUrl()));
        break;
    }


    return hechos;
  }

  @Override
  public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    return new HechoOutputDTO(hecho);
  }


  //API PUBLICA

  //Consulta de hechos dentro de una colección.
  @Override
  public List<Hecho> obtenerHechosPorColeccion(String coleccionId, TipoDeModoNavegacion modo){
    return coleccionService.obtenerHechosPorColeccion(coleccionId, modo);
  }


  //Navegación filtrada sobre una colección.
  @Override
  public List<Hecho> obtenerHechosFiltrados(String coleccionId,String categoria, String fechaDesde, String fechaHasta){
    List<Hecho> hechos = obtenerHechosPorColeccion(coleccionId, TipoDeModoNavegacion.IRRESTRICTA);
    if (categoria != null ) {
      hechos = hechos.stream()
          .filter(h -> h.getCategoria() != null && categoria.trim().equalsIgnoreCase(h.getCategoria().getNombre().trim()))
          .collect(Collectors.toList());
    }

    // Filtro por fecha acontecimiento
    if (fechaDesde != null || fechaHasta != null) {
      hechos = hechos.stream()
          .filter(h -> {
            if (h.getFechaAcontecimiento() == null) return false;

            boolean afterDesde = true;
            boolean beforeHasta = true;

            if (fechaDesde != null) {
              LocalDate desde = LocalDate.parse(fechaDesde);
              afterDesde = !h.getFechaAcontecimiento().isBefore(desde);
            }
            if (fechaHasta != null) {
              LocalDate hasta = LocalDate.parse(fechaHasta);
              beforeHasta = !h.getFechaAcontecimiento().isAfter(hasta);
            }

            return afterDesde && beforeHasta;
          })
          .collect(Collectors.toList());
    }
    return hechos;
  }

  //Navegación curada o irrestricta sobre una colección.
}