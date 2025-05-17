package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Etiqueta;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.repositories.ICategoriaRepository;
import ar.utn.ba.ddsi.services.IHechoService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import ar.utn.ba.ddsi.models.entities.Ubicacion;

import ar.utn.ba.ddsi.models.repositories.IHechoRepository;

@Service
public class HechoService implements IHechoService {

  @Autowired
  private IHechoRepository hechoRepository;
  @Autowired
  private ICategoriaRepository categoriaRepository;

  @Override
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO) {

    Categoria categoria = this.categoriaRepository.findById(hechoInputDTO.getIdCategoria());

    if (categoria == null) {
      throw new RuntimeException("Categoria no encontrada");

      // TODO ver de hacer expecion personalizada
      // TODO si no encuentra la categoria, que pueda crear una nueva (preguntar)
    }
    Ubicacion ubicacion = determinarUbicacion(hechoInputDTO.getCiudad());

    Hecho hecho = new Hecho(
        hechoInputDTO.getTitulo(),
        hechoInputDTO.getDescripcion(),
        categoria,
        ubicacion,
        hechoInputDTO.getFechaAcontecimiento(),
        Origen.CARGA_MANUAL);

    this.hechoRepository.save(hecho);

    return this.hechoOutputDTO(hecho);
  }

  private HechoOutputDTO hechoOutputDTO(Hecho hecho) {
    HechoOutputDTO dto = new HechoOutputDTO();
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setIdCategoria(hecho.getCategoria().getId());
    dto.setUbicacion(hecho.getUbicacion());
    dto.setFechaAcontecimiento(hecho.getFechaAcontecimiento());
    dto.setFechaCarga(hecho.getFechaCarga());
    dto.setOrigen(hecho.getOrigen());
    dto.setIdEtiquetas(hecho.getEtiquetas().stream().map(Etiqueta::getId).collect(Collectors.toSet()));
    return dto;                             // extrae el id de cada etiqueta y los junta en un Set<Integer>
  }

  public void eliminar(Long id) {
    var hecho = this.hechoRepository.findById(id);
    if (hecho != null) {
      this.hechoRepository.delete(hecho);
    }
  }
}

/* TODO decidir como implementar: setIdEtiquetas guardo en mi DTO una lista de los IDs de etiquetas,
   Otra forma es mostrando los nombres de las etiquetas
 */