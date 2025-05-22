package ar.utn.ba.ddsi.builders;

import ar.utn.ba.ddsi.models.entities.Hecho;

public class HechoBuilder {
  private Hecho hecho;

  public HechoBuilder() {
    this.hecho = new Hecho();
  }

  public HechoBuilder withTitle(String title) {
    this.hecho.setTitulo(title);
    return this;
  }

  public Hecho build()
  {
    // validaciones o logica de creacion si necesita la entidad
    return this.hecho;
  }
}
