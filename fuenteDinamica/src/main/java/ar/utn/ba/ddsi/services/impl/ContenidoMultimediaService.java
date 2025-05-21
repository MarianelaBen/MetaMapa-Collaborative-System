package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.ContenidoMultimedia;
import ar.utn.ba.ddsi.models.repositories.IContenidoMultimediaRepository;
import ar.utn.ba.ddsi.services.IContenidoMultimediaService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

public class ContenidoMultimediaService implements IContenidoMultimediaService {

  @Autowired
  private IContenidoMultimediaRepository contenidoMultimediaRepository;

  @Override
  public List<ContenidoMultimedia> mapeosMultimedia(HechoInputDTO hechoInputDTO) {
    return hechoInputDTO.getDatosMultimedia().stream()
        .map(datos -> {
          ContenidoMultimedia nuevoContenido = new ContenidoMultimedia();
          nuevoContenido.setDatosMultimedia(datos);
          nuevoContenido.setIdContenidoMultimedia(contenidoMultimediaRepository.save(nuevoContenido));
          return nuevoContenido;
        })
        .collect(Collectors.toList());
  }
}
