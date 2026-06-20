package app.telegram.handlers;

import app.telegram.bot.BotResponse;
import app.telegram.sesion.SessionManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CommandHandler {

  private final AuthHandler authHandler;
  private final ExplorarHandler explorarHandler;
  private final ColeccionHandler coleccionHandler;
  private final PropuestaHandler propuestaHandler;
  private final SessionManager sessionManager;

  public CommandHandler(
      AuthHandler authHandler,
      ExplorarHandler explorarHandler,
      ColeccionHandler coleccionHandler,
      PropuestaHandler propuestaHandler,
      SessionManager sessionManager
  ) {
    this.authHandler = authHandler;
    this.explorarHandler = explorarHandler;
    this.coleccionHandler = coleccionHandler;
    this.propuestaHandler = propuestaHandler;
    this.sessionManager = sessionManager;
  }

  public BotResponse handle(Update update) {

    if (update.hasCallbackQuery()) {
      return handleCallback(update);
    }

    if (!update.hasMessage() || !update.getMessage().hasText()) return null;

    String text = update.getMessage().getText();
    long chatId = update.getMessage().getChatId();

    if (sessionManager.getPendingField(chatId) != null) {
      if (text.startsWith("/")) {
        authHandler.cancelarLogin(chatId);
      } else {
        BotResponse loginResponse = authHandler.handlePendingLogin(update);
        if (loginResponse != null) return loginResponse;
      }
    }

    if (coleccionHandler.tienePendiente(chatId)) {
      if (text.startsWith("/")) {
        coleccionHandler.cancelarPendiente(chatId);
        // sigue a procesar el comando
      } else {
        BotResponse r = coleccionHandler.handlePendiente(update);
        if (r != null) return r;
      }
    }

    // Flujo propuesta pendiente
    if (propuestaHandler.tienePendiente(chatId)) {
      if (text.startsWith("/")) propuestaHandler.cancelarPendiente(chatId);
      else {
        BotResponse r = propuestaHandler.handlePendiente(update);
        if (r != null) return r;
      }
    }

    if (text.startsWith("/buscar"))   return explorarHandler.handleBuscar(update);
    if (text.startsWith("/aceptar"))  return propuestaHandler.handleAceptar(update);
    if (text.startsWith("/rechazar")) return propuestaHandler.handleRechazar(update);
    if (text.startsWith("/cancelar")) return propuestaHandler.handleCancelar(update);

    return switch (text) {
      case "/start"     -> handleStart();
      case "/login"     -> authHandler.handleLoginCommand(update);
      case "/logout"    -> authHandler.handleLogout(update);
      case "/menu"      -> handleMenu(update);
      case "/explorar" -> explorarHandler.handleVerFiguritas(update);
      case "/coleccion"    -> coleccionHandler.handleMenu(update);
      case "/misfaltantes" -> coleccionHandler.handleVerFaltantes(update);
      case "/misrepetidas" -> coleccionHandler.handleVerRepetidas(update);
      case "/agfaltante"   -> coleccionHandler.handleAgregarFaltante(update);
      case "/agrepetida"   -> coleccionHandler.handleAgregarRepetida(update);
      case "/propuestas"   -> propuestaHandler.handleMenu(update);
      case "/enviadas"     -> propuestaHandler.handleVerEnviadas(update);
      case "/recibidas"    -> propuestaHandler.handleVerRecibidas(update);
      case "/proponer"     -> propuestaHandler.handleCrearPropuesta(update);
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
    return BotResponse.texto("""
              📋 *Menú principal*
              
              🃏 Figuritas
              /explorar — Ver figuritas intercambiables
              /buscar   — Buscar por nombre o selección
              
              📦 Colección
              /coleccion — Ver y gestionar mi colección
              
              🔄 Intercambios
              /propuestas — Ver mis propuestas y proponer
              
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

    if (data.startsWith("faltantes:"))  return coleccionHandler.handlePaginacionFaltantes(update);
    if (data.startsWith("repetidas:"))  return coleccionHandler.handlePaginacionRepetidas(update);

    return null;
  }
}
