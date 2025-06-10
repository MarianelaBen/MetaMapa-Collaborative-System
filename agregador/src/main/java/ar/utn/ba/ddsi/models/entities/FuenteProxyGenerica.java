//package ar.utn.ba.ddsi.models.entities;
//
//import ar.utn.ba.ddsi.adapters.IFuenteProxyAdapter;
//import ar.utn.ba.ddsi.models.dtos.input.HechoInputDTO;
//import ar.utn.ba.ddsi.models.entities.enumerados.TipoFuente;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class FuenteProxyGenerica extends Fuente {
//    private final IFuenteProxyAdapter adapter;
//
//    public FuenteProxyGenerica(IFuenteProxyAdapter adapter) {
//        this.adapter = adapter;
//    }
//
//    @Override
//    public List<Hecho> getHechos() {
//        return adapter.getHechos().stream()
//            .map(HechoInputDTO::toHecho)
//            .collect(Collectors.toList());
//    }
//
//    @Override
//    public TipoFuente getTipo() {
//        return TipoFuente.PROXY;
//    }
//}
