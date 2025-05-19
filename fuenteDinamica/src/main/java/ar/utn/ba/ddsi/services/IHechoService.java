package ar.utn.ba.ddsi.services;
import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Ubicacion;

public interface IHechoService {
  HechoOutputDTO crear(HechoInputDTO hechoInputDTO);
  HechoOutputDTO hechoOutputDTO(Hecho hecho);
  void eliminar(Long id);
}
