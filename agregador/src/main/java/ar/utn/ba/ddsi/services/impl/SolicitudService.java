package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.entities.enumerados.EstadoSolicitud;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import ar.utn.ba.ddsi.services.IDetectorDeSpam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public abstract class SolicitudService implements IDetectorDeSpam {

  @Autowired
  private ISolicitudRepository solicitudRepository;
  //TODO preguntar si acá deberíamos pasarle un colecciónService o si está bien que se pase el repository
  private IColeccionRepository coleccionRepository;


  //al crear la solicitud se llama a este metodo, que filtra spams
  //metodo del agregador, que suponemos que va antes de que se meta el administrador
  public Solicitud filtrarSpams(Solicitud solicitud) {
    //Otra opcion: cada cierto tiempo se ejecuta este metodo y se deberia traer solo las pendientes para gestionarlas
    if (solicitud.getHecho() == null || esSpam(solicitud.getHecho().getTitulo())) {
      solicitud.cambiarEstado(EstadoSolicitud.RECHAZADA);
    }
    solicitudRepository.save(solicitud); //guarda en un repositorio propio
    return solicitud;
  }


  // metodo que va a llamar el administrador cuando acepte una solicitud
  // el administrador mismo va a cambiar el estado y setear fechaDeAtencion
  public void ocultarHecho(Solicitud solicitud) { //solicitudes aceptadas
    Hecho hecho = solicitud.getHecho();
      hecho.setFueEliminado(true);
      coleccionRepository.update(hecho);
  }
}


