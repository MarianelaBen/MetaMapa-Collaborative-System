package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.PaginaDTO;
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
    PaginaDTO<HechoOutputDTO> obtenerHechosPorColeccion(
            String handle,
            TipoDeModoNavegacion modo,
            String categoria,
            String fuente,
            String ubicacion,
            String keyword,
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            int page, int size

    );
    HechoOutputDTO obtenerDetalleHecho(String handle, Long hechoId);
    List<Hecho> obtenerTodosLosHechosDeFuente(Fuente fuente);
    List<Hecho> obtenerTodosLosHechos(Set<Fuente> fuentes);
    HechoOutputDTO hechoOutputDTO(Hecho hecho);
    List<SolicitudOutputDTO> getSolicitudes();
    List<HechoOutputDTO> obtenerHechos();
    void sumarVistaColeccion(String handle);
    public void sumarVistaHecho(Long id);
    List<HechoOutputDTO> top3Hechos();
    List<ColeccionOutputDTO> top4Colecciones();
    Page<HechoOutputDTO> obtenerHechosConPaginacion(Pageable pageable);
    List<HechoOutputDTO> obtenerHechosPorContribuyente(Long contribuyenteId);
    List<HechoOutputDTO> obtenerHechosPorContribuyenteFiltrado(
      Long contribuyenteId, String titulo, String categoria, String estado);

}
