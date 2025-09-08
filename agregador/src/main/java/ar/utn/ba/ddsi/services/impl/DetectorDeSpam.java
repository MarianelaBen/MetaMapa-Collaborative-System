package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.services.IDetectorDeSpam;
import org.springframework.stereotype.Service;

@Service
public class DetectorDeSpam implements IDetectorDeSpam {

  @Override
  public boolean esSpam(String texto) {
    if (texto.length() < 5) {
      return false;
    }
    return true;
  }
}
