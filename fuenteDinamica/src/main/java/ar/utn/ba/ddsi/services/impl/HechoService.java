package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.*;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import ar.utn.ba.ddsi.models.repositories.IContenidoMultimediaRepository;
import ar.utn.ba.ddsi.services.ICategoriaService;
import ar.utn.ba.ddsi.services.IHechoService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

  @Override
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO) {

    Categoria categoria = this.categoriaService.findCategory(hechoInputDTO.getCategoria());

    /*if (categoria == null) { // NUEVADUDA esto se va no?
      throw new RuntimeException("Categoria no encontrada");

      //ver de hacer expecion personalizada
      //si no encuentra la categoria, que pueda crear una nueva (preguntar)
    }*/
    Ubicacion ubicacion = hechoInputDTO.getCiudad();

    ContenidoMultimedia contenidoMultimedia = new ContenidoMultimedia();
    contenidoMultimedia.setDatos(hechoInputDTO.getDatos());
    contenidoMultimedia.setIdContenidoMultimedia(contenidoMultimediaRepository.save(contenidoMultimedia));

    Hecho hecho = new Hecho(
        hechoInputDTO.getTitulo(),
        hechoInputDTO.getDescripcion(),
        categoria,
        ubicacion,
        hechoInputDTO.getFechaAcontecimiento(),
        Origen.CARGA_MANUAL);
    hecho.agregarEtiqueta(new Etiqueta("prueba"));

    hecho.setIdContribuyente(hechoInputDTO.getIdContribuyente());

    hecho.setContenidoMultimedia(contenidoMultimedia);

    this.hechoRepository.save(hecho);

    return this.hechoOutputDTO(hecho);
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
    dto.setIdContenidoMultimedia(hecho.getContenidoMultimedia().getIdContenidoMultimedia());
    return dto;
  }

  @Override
  public void eliminar(Long id) {
    var hecho = this.hechoRepository.findById(id);
    if (hecho != null) {
      this.hechoRepository.delete(hecho);
    }
  }

  @Override
  public boolean puedeEditar(Long id1 , Long id2, LocalDate fecha){
    return Objects.equals(id1,id2) && ChronoUnit.DAYS.between(fecha, LocalDate.now()) > 7;
  }

  @Override
  public HechoOutputDTO permisoDeEdicion(Long idEditor, Long idHecho) {
    Hecho hecho = this.hechoRepository.findById(idHecho);
    if (puedeEditar(idEditor , hecho.getIdContribuyente(), hecho.getFechaCarga())) {
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
    Categoria categoria = this.categoriaService.findCategory(hechoInputDTO.getCategoria());
    hecho.actualizarHecho(hechoInputDTO.getTitulo(),hechoInputDTO.getDescripcion(),categoria,hechoInputDTO.getCiudad(), hechoInputDTO.getFechaAcontecimiento());
    this.hechoRepository.save(hecho);
    return this.hechoOutputDTO(hecho);
  }
}