package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.repositories.IHechoRepository;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IColeccionService;
import ar.utn.ba.ddsi.services.IDetectorDeSpam;
import ar.utn.ba.ddsi.services.ISolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class SolicitudService implements ISolicitudService {

  private ISolicitudRepository solicitudRepository;
  private IDetectorDeSpam detectorDeSpam;
  private IHechoRepository hechoRepository;

public SolicitudService(ISolicitudRepository solicitudRepository, IDetectorDeSpam detectorDeSpam, IHechoRepository hechoRepository) {
  this.solicitudRepository = solicitudRepository;
  this.detectorDeSpam = detectorDeSpam;
  this.hechoRepository = hechoRepository;
}
  //al crear la solicitud se llama a este metodo, que filtra spams
  //metodo del agregador, que suponemos que va antes de que se meta el administrador
  @Override
  public SolicitudDeEliminacion RechazadoPorSpam(SolicitudDeEliminacion solicitud) {

    if (solicitud.getHecho() == null || detectorDeSpam.esSpam(solicitud)) {
      solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
      solicitud.setFechaAtencion(LocalDateTime.now());
    }
    solicitudRepository.save(solicitud);
    return solicitud;
  }


  // metodos que va a llamar el administrador cuando acepte / rechace una solicitud
  @Override
  public void aceptarSolicitud(SolicitudDeEliminacion solicitud){
    Hecho hecho = solicitud.getHecho();
    solicitud.setFechaAtencion(LocalDateTime.now());
    solicitud.cambiarEstado(EstadoSolicitud.ACEPTADA);
    hecho.setFueEliminado(true);
  }
  @Override
  public void rechazarSolicitud(SolicitudDeEliminacion solicitud){
    solicitud.setFechaAtencion(LocalDateTime.now());
    solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
  }

    @Override
    public SolicitudDeEliminacion crearSolicitud(SolicitudInputDTO solicitudDTO) {
        if (solicitudDTO == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (solicitudDTO.getHechoId() == null) {
            throw new IllegalArgumentException("hechoId es requerido");
        }
        if (solicitudDTO.getJustificacion() == null || solicitudDTO.getJustificacion().trim().length() < 500) {
            throw new IllegalArgumentException("La justificación debe tener al menos 500 caracteres");
        }

        Hecho hecho = hechoRepository.findById(solicitudDTO.getHechoId())
                .orElseThrow(() -> new IllegalArgumentException("No existe el hecho con id " + solicitudDTO.getHechoId()));

        SolicitudDeEliminacion entidad = new SolicitudDeEliminacion();
        entidad.setHecho(hecho);
        entidad.setJustificacion(solicitudDTO.getJustificacion().trim());
        entidad.setEstado(EstadoSolicitud.PENDIENTE);
        entidad.setFechaEntrada(LocalDateTime.now());
        entidad.setEsSpam(false);

        SolicitudDeEliminacion saved = solicitudRepository.save(entidad);
        return saved;
    }

    @Override
    public Long contarPorEstado(EstadoSolicitud estado){
    return this.solicitudRepository.countByEstado(estado);
    }


}




