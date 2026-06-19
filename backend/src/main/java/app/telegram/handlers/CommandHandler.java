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
  private final SessionManager sessionManager;

  public CommandHandler(
      AuthHandler authHandler,
      SessionManager sessionManager
  ) {
    this.authHandler = authHandler;
    this.sessionManager = sessionManager;
  }

  public void handle(Update update) {
    String text = update.getMessage().getText();
    long chatId = update.getMessage().getChatId();

    // Primero chequeamos si hay un login en progreso
    if (authHandler.handlePendingLogin(update)) return;

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

  private void handleUnknown(Update update) {
    // Responde cuando el comando no existe
  }
}
