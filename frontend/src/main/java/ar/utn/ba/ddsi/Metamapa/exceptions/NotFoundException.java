package ar.utn.ba.ddsi.Metamapa.exceptions;

public class NotFoundException extends RuntimeException{
  public NotFoundException(String entidad) {
    super("No se ha encontrado " + entidad + " de id " + id);
  }
}
