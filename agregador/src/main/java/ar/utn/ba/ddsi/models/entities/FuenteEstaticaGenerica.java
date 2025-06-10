//package ar.utn.ba.ddsi.models.entities;
//
//import ar.utn.ba.ddsi.adapters.IFuenteProxyAdapter;
//import ar.utn.ba.ddsi.models.entities.enumerados.TipoFuente;
//import ar.utn.ba.ddsi.models.repositories.IHechoRepository;
//import java.util.List;
//
//public class FuenteEstaticaGenerica extends Fuente{
//  private final IHechoRepository repository;
//
//  public FuenteEstaticaGenerica(IHechoRepository repository) {
//    this.repository = repository;
//  }
//
//    @Override
//    public List<Hecho> getHechos() {
//      return this.repository.findAll();
//    }
//
//    @Override
//    public TipoFuente getTipo() {
//      return TipoFuente.ESTATICA;
//    }
//
//}
