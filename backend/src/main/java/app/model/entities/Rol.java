package app.model.entities;

public enum Rol {
  ADMINISTRADOR, USUARIO;

  public String toString() {
    return this.name();
  }
}
