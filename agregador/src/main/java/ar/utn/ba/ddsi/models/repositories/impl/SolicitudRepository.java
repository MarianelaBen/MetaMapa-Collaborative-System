package ar.utn.ba.ddsi.models.repositories.impl;

import ar.utn.ba.ddsi.models.entities.Solicitud;
import ar.utn.ba.ddsi.models.repositories.ISolicitudRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SolicitudRepository implements ISolicitudRepository {
    private List<Solicitud> solicitudes = new ArrayList<>();

    @Override
    public void save(Solicitud solicitud){
      solicitudes.add(solicitud);
    }

    @Override
    public List<Solicitud> findAll(){
      return this.solicitudes;
    }
}
