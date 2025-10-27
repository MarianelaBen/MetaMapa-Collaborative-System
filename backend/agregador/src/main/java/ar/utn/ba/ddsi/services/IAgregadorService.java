package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoFuente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface IAgregadorService {
  List<HechoOutputDTO> obtenerHechosPorColeccion(String coleccionId, TipoDeModoNavegacion modo);
  public List<Hecho> obtenerTodosLosHechosDeFuente(Fuente fuente);
  public List<Hecho> obtenerTodosLosHechos(Set<Fuente> fuentes);
  public HechoOutputDTO hechoOutputDTO(Hecho hecho);
  List<HechoOutputDTO> obtenerHechosFiltrados(String coleccionId, String categoria, String fechaDesde, String fechaHasta);
    List<SolicitudOutputDTO> getSolicitudes();
    List<HechoOutputDTO> obtenerHechos();
    void sumarVistaColeccion(String handle);
    public void sumarVistaHecho(Long id);
    List<HechoOutputDTO> top3Hechos();
    List<ColeccionOutputDTO> top4Colecciones();
    Page<HechoOutputDTO> obtenerHechosConPaginacion(Pageable pageable);
}
