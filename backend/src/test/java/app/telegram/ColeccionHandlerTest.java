package app.telegram;

import app.dto.FiguritaDto;
import app.dto.paginacion.PaginaResultado;
import app.servicios.ServicioColeccion;
import app.servicios.ServicioJwt;
import app.telegram.bot.BotResponse;
import app.telegram.handlers.ColeccionHandler;
import app.telegram.sesion.SessionManager;
import app.telegram.utils.MessageBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ColeccionHandlerTest {

  @Mock
  ServicioColeccion coleccionService;
  @Mock
  ServicioJwt servicioJwt;
  @Mock
  SessionManager sessionManager;
  @Mock
  MessageBuilder messageBuilder;

  ColeccionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ColeccionHandler(
        coleccionService,
        servicioJwt,
        sessionManager,
        messageBuilder
    );
  }

  @Test
  void handleMenu_usuarioAutenticado_debeMostrarMenu() {
    Update update = updateConTexto("/coleccion", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(true);

    BotResponse res = handler.handle(update);

    assertTrue(res.texto().contains("Mi Colección"));
  }

  @Test
  void handleMenu_sinLogin_debePedirLogin() {
    Update update = updateConTexto("/coleccion", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(false);

    BotResponse res = handler.handle(update);

    assertEquals("⚠️ Necesitás iniciar sesión primero. Usá /login", res.texto());
  }

  @Test
  void handleVerFaltantes_sinPaginas_debeRetornarTexto() {
    Update update = updateConTexto("/misfaltantes", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(true);
    when(sessionManager.getToken(1L)).thenReturn("token");
    when(servicioJwt.getColeccionId("token")).thenReturn("col1");

    PaginaResultado<FiguritaDto> pagina =
        new PaginaResultado<>(List.of(
            mock(FiguritaDto.class)
        ), 1, 1, 0);

    when(coleccionService.buscarFaltantes(eq("col1"), any()))
        .thenReturn(pagina);

    BotResponse res = handler.handle(update);

    assertNull(res.teclado());
    assertFalse(res.texto().contains("➡️"));
    assertFalse(res.texto().contains("⬅️"));
  }

  @Test
  void handleVerFaltantes_conMultiplesPaginas_debeAgregarTeclado() {
    Update update = updateConTexto("/misfaltantes", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(true);
    when(sessionManager.getToken(1L)).thenReturn("token");
    when(servicioJwt.getColeccionId("token")).thenReturn("col1");

    PaginaResultado<FiguritaDto> pagina =
        new PaginaResultado<>(List.of(mock(FiguritaDto.class)), 10, 3, 0);

    when(coleccionService.buscarFaltantes(eq("col1"), any()))
        .thenReturn(pagina);

    when(messageBuilder.tecladoPaginacion(0, 3, "faltantes"))
        .thenReturn(mock(org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup.class));

    BotResponse res = handler.handle(update);

    assertNotNull(res.teclado());
  }

//  @Test
//  void handleCallback_faltantes_debeIrAPaginaCorrecta() {
//    Update callback = mock(Update.class);
//    var cq = mock(org.telegram.telegrambots.meta.api.objects.CallbackQuery.class);
//    var msg = mock(org.telegram.telegrambots.meta.api.objects.message.Message.class);
//
//    when(callback.getCallbackQuery()).thenReturn(cq);
//    when(cq.getData()).thenReturn("faltantes:2");
//    when(cq.getMessage()).thenReturn(msg);
//    when(msg.getChatId()).thenReturn(1L);
//
//    when(sessionManager.isAuthenticated(1L)).thenReturn(true);
//    when(sessionManager.getToken(1L)).thenReturn("token");
//    when(servicioJwt.getColeccionId("token")).thenReturn("col1");
//
//    PaginaResultado<FiguritaDto> pagina =
//        new PaginaResultado<>(List.of(mock(FiguritaDto.class)), 10, 3, 1);
//
//    when(coleccionService.buscarFaltantes(eq("col1"), any()))
//        .thenReturn(pagina);
//
//    when(messageBuilder.tecladoPaginacion(1, 3, "faltantes"))
//        .thenReturn(mock(InlineKeyboardMarkup.class));
//
//    BotResponse res = handler.handleCallback(callback);
//
//    assertNotNull(res);
//  }

  @Test
  void agregarFaltante_debeIniciarFlujo() {
    Update update = updateConTexto("/agfaltante", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(true);

    BotResponse res = handler.handle(update);

    assertTrue(res.texto().contains("Ingresá el ID"));
  }

//  @Test
//  void flujoFaltante_segundoPaso_debeConfirmar() {
//    long chatId = 1L;
//
//    when(sessionManager.getToken(chatId)).thenReturn("token");
//    when(servicioJwt.getColeccionId("token")).thenReturn("col1");
//
//    Update update = updateConTexto("FIG123", chatId);
//
//    handler.handlePendiente(update);
//
//    verify(coleccionService)
//        .agregarFaltante("col1", "FIG123");
//  }

  @Test
  void buscarFaltantes_siFalla_servicio_debeDevolverError() {
    Update update = updateConTexto("/misfaltantes", 1L);

    when(sessionManager.isAuthenticated(1L)).thenReturn(true);
    when(sessionManager.getToken(1L)).thenReturn("token");
    when(servicioJwt.getColeccionId("token")).thenReturn("col1");

    when(coleccionService.buscarFaltantes(any(), any()))
        .thenThrow(new RuntimeException("DB caída"));

    BotResponse res = handler.handle(update);

    assertTrue(res.texto().contains("Error"));
  }

  private Update updateConTexto(String text, long chatId) {
    Update u = mock(Update.class);
    org.telegram.telegrambots.meta.api.objects.message.Message m = mock(org.telegram.telegrambots.meta.api.objects.message.Message.class);

    when(u.getMessage()).thenReturn(m);
    when(m.getText()).thenReturn(text);
    when(m.getChatId()).thenReturn(chatId);

    return u;
  }

}
