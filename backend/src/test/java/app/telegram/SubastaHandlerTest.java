package app.telegram;

import app.servicios.ServicioJwt;
import app.servicios.ServicioSubasta;
import app.telegram.bot.BotResponse;
import app.telegram.handlers.SubastaHandler;
import app.telegram.sesion.SessionManager;
import app.telegram.utils.MessageBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubastaHandlerTest {

  @Mock
  ServicioSubasta subastaService;
  @Mock
  ServicioJwt servicioJwt;
  @Mock
  SessionManager sessionManager;
  @Mock
  MessageBuilder messageBuilder;

  @InjectMocks
  SubastaHandler handler;

  @Test
  void handle_subastas_sin_auth() {
    Update u = crearUpdateConTexto("/subastas", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(false);

    BotResponse res = handler.handle(u);

    assertTrue(res.texto().contains("Necesitás iniciar sesión"));
  }

  @Test
  void handle_subastas_ok() {
    Update u = crearUpdateConTexto("/subastas", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(true);

    BotResponse res = handler.handle(u);

    assertTrue(res.texto().contains("Subastas"));
  }

  @Test
  void crear_subasta_inicia_flujo() {
    Update u = crearUpdateConTexto("/crearsubasta", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(true);

    BotResponse res = handler.handle(u);

    assertTrue(handler.tienePendiente(1L));
    assertTrue(res.texto().contains("Nueva subasta"));
  }

  @Test
  void pendiente_crear_subasta_paso1() {
    long chatId = 1L;

    when(sessionManager.isAuthenticated(chatId)).thenReturn(true);

    handler.handle(crearUpdateConTexto("/crearsubasta", chatId));

    BotResponse res = handler.handlePendiente(
        crearUpdateConTexto("ARG-10", chatId)
    );

    assertTrue(res.texto().contains("Paso 2/4"));
  }

  @Test
  void pendiente_calificacion_invalida() {
    long chatId = 1L;

    when(sessionManager.isAuthenticated(chatId)).thenReturn(true);

    handler.handle(crearUpdateConTexto("/crearsubasta", chatId));

    handler.handlePendiente(crearUpdateConTexto("ARG-10", chatId));
    handler.handlePendiente(crearUpdateConTexto("24", chatId));
    handler.handlePendiente(crearUpdateConTexto("A,B", chatId));

    BotResponse res = handler.handlePendiente(
        crearUpdateConTexto("10", chatId)
    );

    assertTrue(res.texto().contains("0 y 5"));
  }

  @Test
  void pendiente_confirmar_crea_subasta() {
    long chatId = 1L;

    when(sessionManager.isAuthenticated(chatId)).thenReturn(true);
    when(sessionManager.getToken(chatId)).thenReturn("token");
    when(servicioJwt.getPerfilId("token")).thenReturn("perfil-1");

    handler.handle(crearUpdateConTexto("/crearsubasta", chatId));

    handler.handlePendiente(crearUpdateConTexto("ARG-10", chatId));
    handler.handlePendiente(crearUpdateConTexto("24", chatId));
    handler.handlePendiente(crearUpdateConTexto("BRA-1", chatId));
    handler.handlePendiente(crearUpdateConTexto("5", chatId));

    BotResponse res = handler.handlePendiente(
        crearUpdateConTexto("si", chatId)
    );

    verify(subastaService).crearSubasta(
        eq("perfil-1"),
        eq("ARG-10"),
        eq(24),
        anyList(),
        eq(5)
    );

    assertTrue(res.texto().contains("Subasta creada"));
  }

  private Update crearUpdateConTexto(String texto, Long chatId) {
    Update update = mock(Update.class);
    Message message = mock(Message.class);

    when(update.getMessage()).thenReturn(message);
    when(message.getText()).thenReturn(texto);
    when(message.getChatId()).thenReturn(chatId);

    return update;
  }

  private Update crearCallbackUpdate(String data, Long chatId) {
    Update update = mock(Update.class);
    CallbackQuery callback = mock(CallbackQuery.class);
    Message message = mock(Message.class);

    when(update.getCallbackQuery()).thenReturn(callback);
    when(callback.getData()).thenReturn(data);
    when(callback.getMessage()).thenReturn(message);
    when(message.getChatId()).thenReturn(chatId);

    return update;
  }
}
