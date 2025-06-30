package ar.utn.ba.ddsi.services;

import ar.utn.ba.ddsi.models.dtos.input.ColeccionInputDTO;
import ar.utn.ba.ddsi.models.dtos.input.FuenteInputDTO;

import ar.utn.ba.ddsi.models.dtos.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.HechoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.output.SolicitudOutputDTO;

/* TODO DTOs Consenso
import ar.utn.ba.ddsi.models.dtos.output.ConsensoOutputDTO;
import ar.utn.ba.ddsi.models.dtos.input.ConsensoInputDTO;
*/

import java.util.List;
import java.util.Optional;

public interface IAdminService {
  List<ColeccionOutputDTO> getColecciones();
  ColeccionOutputDTO crearColeccion(ColeccionInputDTO dto);
  Optional<ColeccionOutputDTO> actualizarColeccion(Long id, ColeccionInputDTO dto);
  void eliminarColeccion(Long id);

  List<HechoOutputDTO> getHechos(Long coleccionId);

  FuenteInputDTO agregarFuente(Long coleccionId, FuenteInputDTO dto);
  void eliminarFuente(Long fuenteId);
/*
  ConsensoResponseDTO configurarConsenso(Long coleccionId, ConsensoDTO dto);
  Optional<ConsensoResponseDTO> obtenerConsenso(Long coleccionId);
*/
  List<SolicitudOutputDTO> getSolicitudes(String estado);
  SolicitudOutputDTO aprobarSolicitud(Long id);
  SolicitudOutputDTO denegarSolicitud(Long id);
}