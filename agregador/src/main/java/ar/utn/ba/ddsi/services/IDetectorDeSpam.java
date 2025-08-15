package ar.utn.ba.ddsi.services;

import org.springframework.stereotype.Service;

@Service
public interface IDetectorDeSpam {
  boolean esSpam(String texto);
}
