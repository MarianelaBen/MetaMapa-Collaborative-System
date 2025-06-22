package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.algoritmos.AlgoritmoDeConsensoFactory;
import ar.utn.ba.ddsi.algoritmos.IAlgoritmoDeConsenso;
import ar.utn.ba.ddsi.models.entities.Coleccion;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.Hecho;
import ar.utn.ba.ddsi.models.repositories.IColeccionRepository;
import ar.utn.ba.ddsi.services.IAgregadorService;
import ar.utn.ba.ddsi.services.IConsensoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsensoService implements IConsensoService {
  @Autowired
  private IAgregadorService agregadorService;
  @Autowired
  private IColeccionRepository coleccionRepository;
  @Autowired
  private AlgoritmoDeConsensoFactory algoritmoFactory;

  @Override
  public void aplicarAlgoritmoDeConsenso() {
    List<Coleccion> colecciones = coleccionRepository.findAll();

    Map<Fuente, List<Hecho>> hechosPorFuente = new HashMap<>();

    colecciones.stream() //Decision de diseño las fuentes de colecciones que se repiten me quedo con el llamdo de la primera vez.
        .filter(coleccion -> coleccion.getAlgoritmoDeConsenso() != null)
        .forEach(coleccion -> {
          coleccion.getFuentes().forEach(fuente -> {
            List<Hecho> hechos = this.obtenerHechosPorFuente(fuente);
            hechosPorFuente.putIfAbsent(fuente, hechos);
          });
        });

    colecciones.stream()
                .filter(coleccion -> coleccion.getAlgoritmoDeConsenso() != null)
                .forEach(coleccion -> this.usoDeAlgoritmo(coleccion, hechosPorFuente));

    colecciones.forEach(coleccion -> coleccionRepository.save(coleccion));
  }

  @Override
  public void usoDeAlgoritmo(Coleccion coleccion, Map<Fuente, List<Hecho>> hechosPorFuente) {
    IAlgoritmoDeConsenso algoritmo = algoritmoFactory.resolver(coleccion.getAlgoritmoDeConsenso());
    if (algoritmo != null) {
      algoritmo.calcularConsenso(coleccion, hechosPorFuente);
    }else {
      System.out.println("Algoritmo desconocido");
    }
  }

}