package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.output.CategoriaOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HoraOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.ProvinciaOutputDTO;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IColeccionService;
import ar.utn.ba.ddsi.services.IEstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

//TODO completar los métodos, posible estructura enviada al discord (chat: codigo-muerto)
@Service
public class EstadisticasService implements IEstadisticasService {

  private final IAgregadorService agregadorService;
  private final IColeccionRepository coleccionRepo;
  private final ISolicitudRepository solicitudRepo;

  @Autowired
  public EstadisticasService(IAgregadorService agregadorService, IColeccionRepository coleccionRepository, ISolicitudRepository solicitudRepo) {
    this.agregadorService = agregadorService;
    this.coleccionRepo = coleccionRepository;
    this.solicitudRepo = solicitudRepo;
  }

  @Override
  public void recalcularEstadisticas() {

  }

  @Override
  public ProvinciaOutputDTO provinciaConMasHechosEnColeccion(String coleccionHandle) {
    Coleccion coleccion = coleccionRepo.findById(coleccionHandle)
        .orElseThrow(() -> new NoSuchElementException("Coleccion no encontrada: " + coleccionHandle));

    List<Hecho> hechos = coleccion.getHechos()
        .stream()
        .filter(h-> Boolean.FALSE.equals(h.getFueEliminado())) //si tira null, no pasa el filtro y NO tira nullPointerException
        .filter(h -> h.getUbicacion() != null && h.getUbicacion().getProvincia() != null)
        //otra opcion si queremos que tire la excepcion si encuentra null: .filter(h-> !h.getFueEliminado())
        .toList();
    return hechos.stream()
        .collect(Collectors.groupingBy(h -> h.getUbicacion().getProvincia(), Collectors.counting()))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> new ProvinciaOutputDTO(e.getKey(), e.getValue()) )
        .orElse(null);
  }

  @Override
  public CategoriaOutputDTO categoriaConMasHechos() {
  return null;
    /*  List<Hecho> hechos = agregadorService.obtenerTodosLosHechos(Set.of());
    //TODO problema porque este metodo devuelve HechoOutputDTO no Hecho

    return hechos.stream()
        .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre() != null)
        .collect(Collectors.groupingBy(h -> h.getCategoria().getNombre(), Collectors.counting()))
        .entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(e -> new CategoriaOutputDTO(e.getKey(), e.getValue()))
        .orElse(null);*/

  }

  @Override
  public ProvinciaOutputDTO provinciaConMasHechosParaCategoria(Long categoriaId) {
    return null;
  }

  @Override
  public HoraOutputDTO horaConMasHechosParaCategoria(Long categoriaId) {
    return null;
  }

  @Override
  public long contarSolicitudesEliminacionSpam() {
    return 0;
  }
}
