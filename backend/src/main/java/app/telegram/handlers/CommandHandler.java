package app.telegram.handlers;

import app.telegram.bot.BotResponse;
import app.telegram.sesion.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CommandHandler {

  // Acá vas a ir inyectando los handlers a medida que los creemos
  // @Autowired private IntercambioHandler intercambioHandler;

  private final AuthHandler authHandler;
  private final RepetidaHandler repetidaHandler;
  private final SessionManager sessionManager;

  public CommandHandler(
      AuthHandler authHandler,
      RepetidaHandler repetidaHandler,
      SessionManager sessionManager
  ) {
    this.authHandler = authHandler;
    this.repetidaHandler = repetidaHandler;
    this.sessionManager = sessionManager;
  }

  public BotResponse handle(Update update) {

    if (update.hasCallbackQuery()) {
      return handleCallback(update);
    }

    if (!update.hasMessage() || !update.getMessage().hasText()) return null;

    String text = update.getMessage().getText();
    long chatId = update.getMessage().getChatId();

    // Si hay login pendiente pero el usuario mandó un comando → cancelamos el login
    if (sessionManager.getPendingField(chatId) != null) {
      if (text.startsWith("/")) {
        authHandler.cancelarLogin(chatId);
        // continúa abajo a procesar el comando normalmente
      } else {
        // No es un comando, lo tomamos como input del login
        BotResponse loginResponse = authHandler.handlePendingLogin(update);
        if (loginResponse != null) return loginResponse;
      }
    }

    if (text.startsWith("/buscar")) {
      return repetidaHandler.handleBuscar(update);
    }

    return switch (text) {
      case "/start"     -> handleStart();
      case "/login"     -> authHandler.handleLoginCommand(update);
      case "/logout"    -> authHandler.handleLogout(update);
      case "/menu"      -> handleMenu(update);
      case "/figuritas" -> repetidaHandler.handleVerFiguritas(update);
      default           -> BotResponse.texto("❓ Comando no reconocido. Usá /menu para ver las opciones.");
    };
  }

  private BotResponse handleStart() {
    return BotResponse.texto("""
                👋 *¡Bienvenido al bot de Figuritas Mundial!*
                
                Con este bot podés gestionar tus figuritas desde Telegram.
                
                Para empezar, iniciá sesión con /login
                """);
  }

  private BotResponse handleMenu(Update update) {
    long chatId = update.getMessage().getChatId();

    if (!sessionManager.isAuthenticated(chatId)) {
      return BotResponse.texto("⚠️ Necesitás iniciar sesión primero. Usá /login");
    }

    return BotResponse.texto("""
                📋 *Menú principal*
                
                🃏 Figuritas
                /figuritas — Ver figuritas intercambiables
                /buscar    — Buscar por nombre o selección
                
                🔄 Intercambios
                /intercambios — Ver propuestas
                /proponer     — Crear propuesta
                
                🏷️ Subastas
                /subastas — Ver subastas activas
                /subastar — Crear una subasta
                
                /logout — Cerrar sesión
                """);
  }

  private BotResponse handleCallback(Update update) {
    String data = update.getCallbackQuery().getData();

    if (data.startsWith("figuritas:")) {
      return repetidaHandler.handlePaginacion(update);
    }

    return null;
  }

  private void handleUnknown(Update update) {
    // Responde cuando el comando no existe
  }
}
