package app.telegram;

import app.dto.IntercambioDto;
import app.dto.paginacion.PaginaResultado;
import app.model.entities.Perfil;
import app.repositories.RepositorioPerfiles;
import app.servicios.ServicioJwt;
import app.servicios.ServicioPropuesta;
import app.telegram.bot.BotResponse;
import app.telegram.handlers.PropuestaHandler;
import app.telegram.sesion.SessionManager;
import app.telegram.utils.MessageBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PropuestaHandlerTest {
  @Mock
  ServicioPropuesta propuestaService;
  @Mock
  ServicioJwt servicioJwt;
  @Mock
  SessionManager sessionManager;
  @Mock
  MessageBuilder messageBuilder;
  @Mock
  RepositorioPerfiles repositorioPerfiles;

  PropuestaHandler handler;

  @BeforeEach
  void setUp() {
    handler = new PropuestaHandler(
        propuestaService,
        servicioJwt,
        sessionManager,
        messageBuilder,
        repositorioPerfiles
    );
  }


  @Test
  void handleMenu_sinLogin_debePedirLogin() {
    Update update = crearUpdateConTexto("/propuestas", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(false);

    BotResponse res = handler.handle(update);

    assertEquals("⚠️ Necesitás iniciar sesión primero. Usá /login", res.texto());
  }

  @Test
  void handleMenu_conLogin_debeMostrarMenu() {
    Update update = crearUpdateConTexto("/propuestas", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(true);

    BotResponse res = handler.handle(update);

    assertTrue(res.texto().contains("/enviadas"));
    assertTrue(res.texto().contains("/recibidas"));
    assertTrue(res.texto().contains("/proponer"));
  }

  @Test
  void handleVerEnviadas_debeEntrarEnFlujoYDevolverFiltro() {
    Update update = crearUpdateConTexto("/enviadas", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(true);

    BotResponse res = handler.handle(update);

    assertTrue(res.texto().contains("Propuestas enviadas"));

    assertTrue(handler.tienePendiente(1L));
  }

  @Test
  void handleCallback_enviadas_debeBuscarPagina() {
    Update update = mock(Update.class);
    var cq = mock(CallbackQuery.class);
    var msg = mock(Message.class);

    when(update.getCallbackQuery()).thenReturn(cq);
    when(cq.getData()).thenReturn("propuestas_enviadas:2");
    when(cq.getMessage()).thenReturn(msg);
    when(msg.getChatId()).thenReturn(1L);

    when(sessionManager.getToken(1L)).thenReturn("token");
    when(servicioJwt.getPerfilId("token")).thenReturn("perfil1");

    PaginaResultado<IntercambioDto> pagina =
        new PaginaResultado<>(List.of(), 0, 1, 0);

    when(propuestaService.buscarPropuestas(eq("perfil1"), any()))
        .thenReturn(pagina);

    BotResponse res = handler.handleCallback(update);

    assertNotNull(res);
  }

  @Test
  void buscarPropuestas_sinResultados_debeRetornarMensajeVacio() {
    long chatId = 1L;

    when(sessionManager.getToken(chatId)).thenReturn("token");
    when(servicioJwt.getPerfilId("token")).thenReturn("perfil1");

    when(propuestaService.buscarPropuestas(eq("perfil1"), any()))
        .thenReturn(new PaginaResultado<>(List.of(), 0, 1, 0));

    handler.cancelarPendiente(chatId);

    Update update = mock(Update.class);
    CallbackQuery cq = mock(CallbackQuery.class);
    Message msg = mock(Message.class);

    when(update.getCallbackQuery()).thenReturn(cq);
    when(cq.getData()).thenReturn("propuestas_enviadas:1");
    when(cq.getMessage()).thenReturn(msg);
    when(msg.getChatId()).thenReturn(chatId);

    BotResponse res = handler.handleCallback(update);

    assertTrue(res.texto().contains("No tenés propuestas"));
  }

  @Test
  void handleAceptar_debeAceptarPropuesta() {
    Update update = crearUpdateConTexto("/aceptar 123", 1L);

    when(sessionManager.getToken(1L)).thenReturn("token");
    when(servicioJwt.getPerfilId("token")).thenReturn("perfil1");

    BotResponse res = handler.handle(update);

    verify(propuestaService).aceptar("123", "perfil1");
    assertTrue(res.texto().contains("aceptada"));
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
