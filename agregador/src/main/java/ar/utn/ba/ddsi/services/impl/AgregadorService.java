package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.adapters.AdapterFuenteDinamica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteEstatica;
import ar.utn.ba.ddsi.adapters.AdapterFuenteProxy;
import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Etiqueta;
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
import java.util.NoSuchElementException;
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
    if (fuentes == null || fuentes.isEmpty()) {
      throw new NoSuchElementException("No se especificaron fuentes.");
    }
    List<Hecho> hechos = fuentes.stream()
        .flatMap( f-> obtenerTodosLosHechosDeFuente(f)
            .stream())
            .collect(Collectors.toList());

    if (hechos.isEmpty()) {
      throw new NoSuchElementException("No se encontraron hechos para las fuentes indicadas.");
    }
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

  /*@Override
  public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    return new HechoOutputDTO(hecho);
  }*/ // No sirve lo hago con modo tipico, se pierden datos sino

  @Override
  public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    HechoOutputDTO hechoOutputDTO = new HechoOutputDTO();
    hechoOutputDTO.setTitulo(hecho.getTitulo());
    hechoOutputDTO.setDescripcion(hecho.getDescripcion());
    hechoOutputDTO.setFechaCarga(hecho.getFechaCarga());
    hechoOutputDTO.setLatitud(hecho.getUbicacion().getLatitud());
    hechoOutputDTO.setLongitud(hecho.getUbicacion().getLongitud());
    hechoOutputDTO.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    hechoOutputDTO.setCategoria(hecho.getCategoria().getNombre());
    System.out.println(hecho.getCategoria().getNombre());
    hechoOutputDTO.setFuenteExterna(hecho.getFuenteExterna());
    if(hecho.getEtiquetas() != null){
      hechoOutputDTO.setIdEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getNombre).collect(Collectors.toSet()));
    }
    if(hecho.getPathMultimedia() != null){
      hechoOutputDTO.setIdContenidoMultimedia(new ArrayList<>(hecho.getPathMultimedia()));
    }
    if(hecho.getContribuyente() != null){
      hechoOutputDTO.setContribuyente(hecho.getContribuyente());
    }
    if(hecho.getFuenteExterna() != null){
      hechoOutputDTO.setFuenteExterna(hecho.getFuenteExterna());
    }

    return hechoOutputDTO;
  }


  //API PUBLICA

  //Consulta de hechos dentro de una colección.
  @Override
  public List<HechoOutputDTO> obtenerHechosPorColeccion(String coleccionId, TipoDeModoNavegacion modo){
    List<Hecho> hechos = coleccionService.obtenerHechosPorColeccion(coleccionId, modo);
    if (hechos == null) {
      throw new NoSuchElementException("Coleccion no encontrada: " + coleccionId);
    }
    return hechos
        .stream()
        .map(this::hechoOutputDTO)
        .toList();
  }


  //Navegación filtrada sobre una colección.
  @Override
  public List<HechoOutputDTO> obtenerHechosFiltrados(String coleccionId,String categoria, String fechaDesde, String fechaHasta){
    List<HechoOutputDTO> hechos = obtenerHechosPorColeccion(coleccionId, TipoDeModoNavegacion.IRRESTRICTA);
    if (categoria != null ) {
      hechos = hechos.stream()
          .filter(h -> h.getCategoria() != null && categoria.trim().equalsIgnoreCase(h.getCategoria().trim()))
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