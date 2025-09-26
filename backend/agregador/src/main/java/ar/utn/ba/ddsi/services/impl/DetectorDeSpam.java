package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.services.IDetectorDeSpam;
import org.springframework.stereotype.Service;

@Service
public class DetectorDeSpam implements IDetectorDeSpam {

  @Override
  public boolean esSpam(SolicitudDeEliminacion solicitudDeEliminacion) {
    if (solicitudDeEliminacion.getJustificacion().length() < 5) {
      solicitudDeEliminacion.setEsSpam(true);
      return true;
    }
    return false;
  }
}
