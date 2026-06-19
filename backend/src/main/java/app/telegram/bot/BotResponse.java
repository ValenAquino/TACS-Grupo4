package app.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public record BotResponse(
    String texto,
    InlineKeyboardMarkup teclado  // null si no hay botones
) {
  // Constructor simple sin teclado
  public static BotResponse texto(String texto) {
    return new BotResponse(texto, null);
  }

  // Constructor con teclado de paginación
  public static BotResponse conTeclado(String texto, InlineKeyboardMarkup teclado) {
    return new BotResponse(texto, teclado);
  }
}
