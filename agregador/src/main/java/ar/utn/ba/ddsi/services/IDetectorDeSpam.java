package ar.utn.ba.ddsi.services;

import org.springframework.stereotype.Component;

@Component
public interface IDetectorDeSpam {
  boolean esSpam(String texto);
}
