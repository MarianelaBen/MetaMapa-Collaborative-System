package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Categoria;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.services.IHechoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import java.time.LocalDate;
import ar.utn.ba.ddsi.models.entities.enumerados.Origen;
import ar.utn.ba.ddsi.models.entities.Ubicacion;

@Service
public class HechoService implements IHechoService {

  @Autowired
  private IHechoRepository hechoRepository;

  @Override
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO) {

    Categoria categoria = this.categoriaRepository.findById(hechoInputDTO.getIdCategoria());
    // TODO tirar exception si no lo encuentra y que pueda crear una nueva

    Ubicacion ubicacion = determinarUbicacion(hechoInputDTO.getCiudad());
    var hecho = new Hecho(
        hechoInputDTO.getTitulo(),
        hechoInputDTO.getDescripcion(),
        categoria,
        ubicacion,
        hechoInputDTO.getFechaAcontecimiento(),
        Origen.CARGA_MANUAL);

      //TODO guardar hecho en repositorio y la traduccion para devolver outpout
  }

  // TODO agregar metodo HechoOutputDTO (ver repo diseflix)
}
