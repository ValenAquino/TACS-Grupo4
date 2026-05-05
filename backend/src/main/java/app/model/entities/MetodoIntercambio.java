package app.model.entities;

public enum MetodoIntercambio {
  SUBASTA,
  INTERCAMBIO;

  /**
   * Convierte un string al enum correspondiente (case-insensitive).
   * Lanza {@link IllegalArgumentException} si el valor no coincide con ningún método.
   */
  public static MetodoIntercambio fromString(String value) {
    for (MetodoIntercambio metodo : MetodoIntercambio.values()) {
      if (metodo.name().equalsIgnoreCase(value)) {
        return metodo;
      }
    }
    throw new IllegalArgumentException("Valor inválido: " + value);
  }
}
