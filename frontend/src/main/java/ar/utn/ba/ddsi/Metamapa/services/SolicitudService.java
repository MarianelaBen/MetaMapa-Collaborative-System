package ar.utn.ba.ddsi.Metamapa.services;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class SolicitudService {
  public String crearSolicitudEliminacion(Long hechoId, String justificacion) { //TODO POR AHORA AHARDCODEADO DSP CONECTAR CONM BACK
    String shortId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    return "SOL-" + shortId;
  }
}
