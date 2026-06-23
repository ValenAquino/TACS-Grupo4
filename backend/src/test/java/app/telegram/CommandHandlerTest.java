package app.telegram;

import app.telegram.bot.BotResponse;
import app.telegram.handlers.BotHandler;
import app.telegram.handlers.CommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandHandlerTest {

  @Mock
  BotHandler handlerA;
  @Mock
  BotHandler handlerB;

  CommandHandler commandHandler;

  @BeforeEach
  void setUp() {

    when(handlerA.comandos()).thenReturn(Set.of("/menu"));
    when(handlerA.prefijos()).thenReturn(Set.of("/buscar"));
    when(handlerA.callbackPrefijos()).thenReturn(Set.of("subastas_activas:"));

    when(handlerB.comandos()).thenReturn(Set.of());
    when(handlerB.prefijos()).thenReturn(Set.of());
    when(handlerB.callbackPrefijos()).thenReturn(Set.of());

    commandHandler = new CommandHandler(List.of(handlerA, handlerB));
  }

  @Test
  void comando_exacto_delega_a_handler() {
    Update u = crearUpdateConTexto("/menu", 1L);

    when(handlerA.handle(u)).thenReturn(BotResponse.texto("OK"));

    BotResponse res = commandHandler.handle(u);

    assertEquals("OK", res.texto());
  }

  @Test
  void prefijo_delega_a_handler() {
    Update u = crearUpdateConTexto("/buscar algo", 1L);

    when(handlerA.comandos()).thenReturn(Set.of());
    when(handlerA.prefijos()).thenReturn(Set.of("/buscar"));
    when(handlerA.callbackPrefijos()).thenReturn(Set.of());

    when(handlerA.handle(u)).thenReturn(BotResponse.texto("BUSCAR"));

    BotResponse res = commandHandler.handle(u);

    assertEquals("BUSCAR", res.texto());
  }

  @Test
  void callback_delega_a_handler() {
    Update u = mock(Update.class);
    CallbackQuery cb = mock(CallbackQuery.class);

    when(u.hasCallbackQuery()).thenReturn(true);
    when(u.getCallbackQuery()).thenReturn(cb);
    when(cb.getData()).thenReturn("subastas_activas:1");

    when(handlerA.callbackPrefijos()).thenReturn(Set.of("subastas_activas:"));
    when(handlerA.handleCallback(u)).thenReturn(BotResponse.texto("CB OK"));

    BotResponse res = commandHandler.handle(u);

    assertEquals("CB OK", res.texto());
  }

  @Test
  void pendiente_se_ejecuta() {
    Update u = crearUpdateConTexto("hola", 1L);

    when(handlerA.tienePendiente(1L)).thenReturn(true);
    when(handlerA.handlePendiente(u)).thenReturn(BotResponse.texto("PENDIENTE"));

    when(handlerA.comandos()).thenReturn(Set.of());
    when(handlerA.prefijos()).thenReturn(Set.of());
    when(handlerA.callbackPrefijos()).thenReturn(Set.of());

    BotResponse res = commandHandler.handle(u);

    assertEquals("PENDIENTE", res.texto());
  }

  @Test
  void pendiente_se_cancela_si_nuevo_comando() {
    Update u = crearUpdateConTexto("/menu", 1L);

    when(handlerA.tienePendiente(1L)).thenReturn(true);

    when(handlerA.comandos()).thenReturn(Set.of("/menu"));
    when(handlerA.prefijos()).thenReturn(Set.of());
    when(handlerA.callbackPrefijos()).thenReturn(Set.of());

    when(handlerA.handle(u)).thenReturn(BotResponse.texto("MENU"));

    BotResponse res = commandHandler.handle(u);

    assertEquals("MENU", res.texto());
    verify(handlerA).cancelarPendiente(1L);
  }

  @Test
  void comando_desconocido() {
    Update u = crearUpdateConTexto("xd", 1L);

    when(handlerA.comandos()).thenReturn(Set.of());
    when(handlerA.prefijos()).thenReturn(Set.of());
    when(handlerA.callbackPrefijos()).thenReturn(Set.of());

    when(handlerB.comandos()).thenReturn(Set.of());
    when(handlerB.prefijos()).thenReturn(Set.of());
    when(handlerB.callbackPrefijos()).thenReturn(Set.of());

    BotResponse res = commandHandler.handle(u);

    assertTrue(res.texto().contains("no reconocido"));
  }

  private Update crearUpdateConTexto(String texto, Long chatId) {
    Update update = mock(Update.class);
    Message message = mock(Message.class);

    when(update.getMessage()).thenReturn(message);
    when(message.getText()).thenReturn(texto);
    when(message.getChatId()).thenReturn(chatId);

    return update;
  }
}
