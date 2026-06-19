package app.telegram.handlers;

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

  public void handle(Update update) {

    // Manejo de botones inline (paginación, acciones)
    if (update.hasCallbackQuery()) {
      handleCallback(update);
      return;
    }

    if (!update.hasMessage() || !update.getMessage().hasText()) return;

    // Login en progreso tiene prioridad
    if (authHandler.handlePendingLogin(update)) return;

    String text = update.getMessage().getText();
    long chatId = update.getMessage().getChatId();

    // Comandos con argumentos (ej: "/buscar Messi")
    if (text.startsWith("/buscar")) {
      repetidaHandler.handleBuscar(update);
      return;
    }

    switch (text) {
      case "/start"  -> handleStart(update);
      case "/login"  -> authHandler.handleLoginCommand(update);
      case "/logout" -> authHandler.handleLogout(update);
      case "/menu"   -> handleMenu(update);
      default        -> handleUnknown(update);
    }
  }

  private void handleStart(Update update) {
    String msg = """
                👋 *¡Bienvenido al bot de Figuritas Mundial!*
                
                Con este bot podés gestionar tus figuritas directamente desde Telegram.
                
                Para empezar, iniciá sesión con /login
                """;
    // sendMessage(update, msg);
  }

  private void handleMenu(Update update) {
    long chatId = update.getMessage().getChatId();

    if (!sessionManager.isAuthenticated(chatId)) {
      // sendMessage(update, "⚠️ Necesitás iniciar sesión primero. Usá /login");
      return;
    }

    String menu = """
                📋 *Menú principal*
                
                🃏 Figuritas
                /misfigus — Ver mis repetidas y faltantes
                /cargar   — Cargar figuritas
                
                🔄 Intercambios
                /intercambios — Ver propuestas
                /proponer     — Crear propuesta
                
                🏷️ Subastas
                /subastas — Ver subastas activas
                /subastar — Crear una subasta
                
                /logout — Cerrar sesión
                """;
    // sendMessage(update, menu);
  }

  private void handleCallback(Update update) {
    String data = update.getCallbackQuery().getData();

    if (data.startsWith("figuritas:")) {
      repetidaHandler.handlePaginacion(update);
    }
    // A medida que agreguemos más handlers, los conectamos acá
    // if (data.startsWith("subastas:")) { ... }
    // if (data.startsWith("intercambios:")) { ... }
  }

  private void handleUnknown(Update update) {
    // Responde cuando el comando no existe
  }
}
