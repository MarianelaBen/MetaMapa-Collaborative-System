package ar.utn.ba.ddsi.models.entities;
import ar.utn.ba.ddsi.models.entities.enumerados.TipoFuente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Fuente {
    private Long id;
    private String url;
    private TipoFuente tipo;

    public Fuente(String url, TipoFuente tipo) {
        this.url = url;
        this.tipo = tipo;
    }
}

