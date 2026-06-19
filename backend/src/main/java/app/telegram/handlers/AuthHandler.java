package app.telegram.handlers;

import app.dto.request.LoginRequest;
import app.exceptions.UsuarioException;
import app.servicios.ServicioSesion;
import app.telegram.sesion.SessionManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthHandler {

  private final ServicioSesion servicioSesion;
  private final SessionManager sessionManager;

  // Guardamos temporalmente el nombre mientras esperamos la contraseña
  private final Map<Long, String> pendingUsername = new ConcurrentHashMap<>();

  public AuthHandler(ServicioSesion servicioSesion, SessionManager sessionManager) {
    this.servicioSesion = servicioSesion;
    this.sessionManager = sessionManager;
  }

  // Paso 1: usuario escribe /login
  public void handleLoginCommand(Update update) {
    long chatId = update.getMessage().getChatId();

    // Solo permitimos login en chat privado
    if (!update.getMessage().getChat().getType().equals("private")) {
      sendMessage(update, "⚠️ Por seguridad, el login solo está disponible en chat privado con el bot.");
      return;
    }

    if (sessionManager.isAuthenticated(chatId)) {
      sendMessage(update, "✅ Ya estás autenticado. Usá /menu para ver las opciones.");
      return;
    }

    sessionManager.setPendingField(chatId, "username");
    sendMessage(update, "🔐 *Inicio de sesión*\n\nEscribí tu nombre de usuario:");
  }

  // Paso 2 y 3: recibe usuario, luego contraseña
  public boolean handlePendingLogin(Update update) {
    long chatId = update.getMessage().getChatId();
    String field = sessionManager.getPendingField(chatId);
    String text = update.getMessage().getText();

    if (field == null) return false; // no hay login pendiente

    if (field.equals("username")) {
      pendingUsername.put(chatId, text);
      sessionManager.setPendingField(chatId, "password");
      sendMessage(update, "🔑 Ahora escribí tu contraseña:");
      return true;
    }

    if (field.equals("password")) {
      String nombre = pendingUsername.get(chatId);
      sessionManager.clearPendingField(chatId);
      pendingUsername.remove(chatId);

      try {
        // Reutilizamos exactamente tu servicio existente
        LoginRequest loginRequest = new LoginRequest(nombre, text);
        String token = servicioSesion.login(loginRequest);
        sessionManager.saveToken(chatId, token);
        sendMessage(update, "✅ *¡Bienvenido, " + nombre + "!*\n\nUsá /menu para ver todas las opciones.");
      } catch (UsuarioException e) {
        sendMessage(update, "❌ Credenciales inválidas. Intentá de nuevo con /login");
      }
      return true;
    }

    return false;
  }

  public void handleLogout(Update update) {
    long chatId = update.getMessage().getChatId();
    sessionManager.logout(chatId);
    sendMessage(update, "👋 Sesión cerrada correctamente.");
  }

  private void sendMessage(Update update, String text) {
    // Lo implementamos cuando creemos el MessageBuilder
  }
}
