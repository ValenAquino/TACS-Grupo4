package app.telegram.handlers;

import app.telegram.bot.BotResponse;
import app.telegram.sesion.SessionManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CommandHandler {

  private final AuthHandler authHandler;
  private final ExplorarHandler explorarHandler;
  private final SessionManager sessionManager;

  public CommandHandler(
      AuthHandler authHandler,
      ExplorarHandler repetidaHandler,
      SessionManager sessionManager
  ) {
    this.authHandler = authHandler;
    this.explorarHandler = repetidaHandler;
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
      return explorarHandler.handleBuscar(update);
    }

    return switch (text) {
      case "/start"     -> handleStart();
      case "/login"     -> authHandler.handleLoginCommand(update);
      case "/logout"    -> authHandler.handleLogout(update);
      case "/menu"      -> handleMenu(update);
      case "/explorar" -> explorarHandler.handleVerFiguritas(update);
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
                /explorar — Ver figuritas intercambiables
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
      return explorarHandler.handlePaginacion(update);
    }

    return null;
  }

  private void handleUnknown(Update update) {
    // Responde cuando el comando no existe
  }
}
