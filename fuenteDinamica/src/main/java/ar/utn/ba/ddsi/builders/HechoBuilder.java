package ar.utn.ba.ddsi.builders;

import ar.utn.ba.ddsi.models.entities.Hecho;

public class HechoBuilder {
  private Hecho hecho;

  public HechoBuilder() {
    this.hecho = new Hecho();
  }

  public HechoBuilder withTitulo(String titulo) {
    this.hecho.setTitulo(titulo);
    return this;
  }

  public HechoBuilder withDescripcion(String descripcion) {
    this.hecho.setDescripcion(descripcion);
    return this;
  }

  // TODO poner los atributos

  public Hecho build()
  {
    // validaciones o logica de creacion si necesita la entidad
    return this.hecho;
  }
}
