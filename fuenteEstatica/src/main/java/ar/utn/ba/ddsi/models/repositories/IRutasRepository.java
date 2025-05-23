package ar.utn.ba.ddsi.models.repositories;

import java.util.List;

public interface IRutasRepository {
  void save(String path);
  List<String> findAll();
}
