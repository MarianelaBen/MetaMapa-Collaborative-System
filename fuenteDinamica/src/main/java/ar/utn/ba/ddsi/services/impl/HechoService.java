package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoSolicitud;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import ar.utn.ba.ddsi.models.repositories.IContenidoMultimediaRepository;
import ar.utn.ba.ddsi.services.ICategoriaService;
import ar.utn.ba.ddsi.services.IContenidoMultimediaService;
import ar.utn.ba.ddsi.services.IHechoService;
import ar.utn.ba.ddsi.services.ISolicitudService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import ar.utn.ba.ddsi.models.entities.enumerados.Origen;

import ar.utn.ba.ddsi.models.repositories.IHechoRepository;

import java.time.temporal.ChronoUnit; // biblioteca que permite la comparacion de dias NUEVO

@Service
public class HechoService implements IHechoService {

  @Autowired
  private IHechoRepository hechoRepository;
  @Autowired
  private ICategoriaRepository categoriaRepository;
  @Autowired
  private ICategoriaService categoriaService;
  @Autowired
  private IContenidoMultimediaRepository contenidoMultimediaRepository;
  @Autowired
  private IContenidoMultimediaService contenidoMultimediaService;
  @Autowired
  private ISolicitudService solicitudService;

  @Override
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO) {
    try {
      Categoria categoria = this.categoriaService.findCategory(hechoInputDTO.getCategoria());
      Ubicacion ubicacion = hechoInputDTO.getCiudad();

      List<ContenidoMultimedia> contenidosMultimedia = contenidoMultimediaService.mapeosMultimedia(hechoInputDTO.getPathsMultimedia());

      //TODO agregar esto al hecho builder
      Hecho hecho = new Hecho(
          hechoInputDTO.getTitulo(),
          hechoInputDTO.getDescripcion(),
          categoria,
          ubicacion,
          hechoInputDTO.getFechaAcontecimiento(),
          Origen.CARGA_MANUAL);

      hecho.agregarEtiqueta(new Etiqueta("prueba")); // Mas adelante cambiar por DTO

      hecho.setContenidosMultimedia(contenidosMultimedia);
      hecho.setIdContribuyente(hechoInputDTO.getIdContribuyente());

      this.hechoRepository.save(hecho);
      this.solicitudService.create(hecho, TipoSolicitud.EDICION);

      return this.hechoOutputDTO(hecho);
    } catch (Exception e) {
      throw new HechoCreacionException("Error al crear el hecho: " + e.getMessage(), e);
    }
  }

  @Override
  public HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    HechoOutputDTO dto = new HechoOutputDTO();
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setIdCategoria(hecho.getCategoria().getId());
    dto.setUbicacion(hecho.getUbicacion());
    dto.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    dto.setFechaCarga(hecho.getFechaCarga());
    dto.setOrigen(hecho.getOrigen());       // extrae el id de cada etiqueta y los junta en un Set<Integer>
    dto.setIdEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getId).collect(Collectors.toSet()));
    dto.setIdContribuyente(hecho.getIdContribuyente());
    dto.setIdContenidoMultimedia(hecho.getContenidosMultimedia().stream().map(ContenidoMultimedia::getIdContenidoMultimedia).collect(Collectors.toList()));
    return dto;
  }

  @Override
  public void eliminar(Long id) {
    var hecho = this.hechoRepository.findById(id);
    if (hecho == null) {
      throw new HechoNotFoundException("Hecho no encontrado ID: " + id);
    }
      this.hechoRepository.delete(hecho);
  }

  @Override
  public boolean puedeEditar(Long id1 , Long id2, LocalDate fecha){
    return Objects.equals(id1, id2) && ChronoUnit.DAYS.between(fecha, LocalDate.now()) <= 7;
  }

  @Override
  public HechoOutputDTO permisoDeEdicion(Long idEditor, Long idHecho) {
    Hecho hecho = this.hechoRepository.findById(idHecho);
    if (puedeEditar(idEditor, hecho.getIdContribuyente(), hecho.getFechaCarga())) {
      return this.hechoOutputDTO(hecho);
    }
    else{
      //TODO excepcion
      return null;
    }
  }

  @Override
  public HechoOutputDTO edicion(Long idEditor, HechoInputDTO hechoInputDTO, Long idHecho) {
    Hecho hecho = this.hechoRepository.findById(idHecho);
    HechoEstadoPrevio estadoPrevio = new HechoEstadoPrevio(hecho);

    Categoria categoria = this.categoriaService.findCategory(hechoInputDTO.getCategoria());
    List<ContenidoMultimedia> contenidosMultimedia = contenidoMultimediaService.mapeosMultimedia(hechoInputDTO.getPathsMultimedia());

    if (hecho.getContenidosMultimedia() != null) {
      hecho.getContenidosMultimedia().forEach(
          c -> contenidoMultimediaRepository.delete(c.getIdContenidoMultimedia()));
    }

    hecho.actualizarHecho(
        hechoInputDTO.getTitulo(),
        hechoInputDTO.getDescripcion(),
        categoria, hechoInputDTO.getCiudad(),
        hechoInputDTO.getFechaAcontecimiento());

    if (contenidosMultimedia != null) {
      hecho.setContenidosMultimedia(contenidosMultimedia);
    }
    hecho.setEstadoPrevio(estadoPrevio);

    this.solicitudService.create(hecho, TipoSolicitud.EDICION);

    this.hechoRepository.save(hecho);
    return this.hechoOutputDTO(hecho);
  }

  @Override
  public void creacionRechazada(Hecho hecho){
    hecho.setFueEliminado(true);
    this.hechoRepository.save(hecho);
  }

  @Override
  public void edicionRechazada(Hecho hecho){
    HechoEstadoPrevio estadoPrevio = hecho.getEstadoPrevio();
    hecho.setEstadoPrevio(null);
    hecho.actualizarHecho(estadoPrevio.getTitulo(), estadoPrevio.getDescripcion(), estadoPrevio.getCategoria(), estadoPrevio.getUbicacion(), estadoPrevio.getFechaAcontecimiento());
    List<ContenidoMultimedia> contenidosMultimedia = contenidoMultimediaService.mapeosMultimedia(hecho.getPathsMultimedia(hecho.getContenidosMultimedia()));
    if (hecho.getContenidosMultimedia() != null) {
      hecho.getContenidosMultimedia().forEach(
          c -> contenidoMultimediaRepository.delete(c.getIdContenidoMultimedia()));
    }
    if (contenidosMultimedia != null) {
      hecho.setContenidosMultimedia(contenidosMultimedia);
    }

    this.hechoRepository.save(hecho);
  }
}
