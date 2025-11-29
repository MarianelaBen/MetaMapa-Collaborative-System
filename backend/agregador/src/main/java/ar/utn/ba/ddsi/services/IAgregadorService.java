package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoDeModoNavegacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IAgregadorService {
    List<HechoOutputDTO> obtenerHechosPorColeccion(
            String handle,
            TipoDeModoNavegacion modo,
            String categoria,
            String fuente,
            String ubicacion,
            String keyword,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    );
    HechoOutputDTO obtenerDetalleHecho(String handle, Long hechoId);
  public List<Hecho> obtenerTodosLosHechosDeFuente(Fuente fuente);
  public List<Hecho> obtenerTodosLosHechos(Set<Fuente> fuentes);
  public HechoOutputDTO hechoOutputDTO(Hecho hecho);
    List<SolicitudOutputDTO> getSolicitudes();
    List<HechoOutputDTO> obtenerHechos();
    void sumarVistaColeccion(String handle);
    public void sumarVistaHecho(Long id);
    List<HechoOutputDTO> top3Hechos();
    List<ColeccionOutputDTO> top4Colecciones();
    Page<HechoOutputDTO> obtenerHechosConPaginacion(Pageable pageable);
}
