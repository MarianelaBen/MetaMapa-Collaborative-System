package ar.utn.ba.ddsi.services.impl;

import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
import ar.utn.ba.ddsi.models.entities.Fuente;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoFuente;
import ar.utn.ba.ddsi.models.repositories.IFuenteRepository;
import ar.utn.ba.ddsi.services.IAgregadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgregadorService implements IAgregadorService {
private List<Fuente> fuentes;
private  IFuenteRepository fuenteRepository;

public AgregadorService(List<Fuente> fuentes, IFuenteRepository fuenteRepository){
  this.fuentes = fuentes;
  this.fuenteRepository = fuenteRepository;
}

  @Override
  public void agregarFuente(Fuente fuente){
    this.fuentes.add(fuente);
  //fuenteRepository.save(fuente); ya no usamos el repositorio porque necesitamos la conexion dinamica con las fuentes,no sacar los hechos de un repo como veniamos haciendo
  }

  @Override
  public List<HechoInputDTO> obtenerHechosDeFuentes() {
    return fuentes.stream()
        .flatMap(f -> f.getHechos().stream())
        .map(hecho -> new HechoInputDTO(hecho)) // conversión a DTO
        .collect(Collectors.toList());
  }

  @Override
  public List<HechoInputDTO> obtenerHechosPorTipoDeFuente(TipoFuente tipo) {
    return fuentes.stream()
        .filter(f -> f.getTipo() == tipo)
        .flatMap(f -> f.getHechos().stream())
        .map(hecho -> new HechoInputDTO(hecho)) // conversión a DTO
        .collect(Collectors.toList());
  }

  @Override
  public List<HechoInputDTO> obtenerHechosPorFuenteExterna(String tipo) {
    List<Fuente> fuentesProxy = this.obtenerFuentesProxy();
    return fuentesProxy.stream()
        .flatMap(f -> f.getHechos().stream())           // Stream<Hecho>
        .map(h -> new HechoInputDTO(h))                 // Stream<HechoInputDTO>
        .filter(dto -> tipo.equalsIgnoreCase(dto.getFuenteExterna()))
        .collect(Collectors.toList());
  }

  @Override
  public List<Fuente> obtenerFuentesProxy() {
    return fuentes.stream().filter(f -> f.getTipo() == TipoFuente.PROXY).collect(Collectors.toList());
  }

}
/*
public class AgregadorService implements IAgregadorService {

    private final AdapterFuenteDinamica adapterFuenteDinamica;
    private final AdapterFuenteEstatica adapterFuenteEstatica;
    private final AdapterFuenteProxy adapterFuenteProxy;

    public AgregadorService(AdapterFuenteDinamica adapterFuenteDinamica,
                             AdapterFuenteEstatica adapterFuenteEstatica,
                             AdapterFuenteProxy adapterFuenteProxy) {
        this.adapterFuenteDinamica = adapterFuenteDinamica;
        this.adapterFuenteEstatica = adapterFuenteEstatica;
        this.adapterFuenteProxy = adapterFuenteProxy;
    }

    @Override
    public List<Hecho> obtenerTodosLosHechos() {
        List<Hecho> hechos = new ArrayList<>();
        hechos.addAll(adapterFuenteDinamica.obtenerHechos());
        hechos.addAll(adapterFuenteEstatica.obtenerHechos());
        hechos.addAll(adapterFuenteProxy.obtenerHechos());
        return hechos;
    }
}*/