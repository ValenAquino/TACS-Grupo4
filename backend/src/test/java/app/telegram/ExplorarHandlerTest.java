package app.telegram;

import app.dto.FiguritaIntercambiableDto;
import app.dto.paginacion.PaginaResultado;
import app.servicios.ServicioFigurita;
import app.telegram.bot.BotResponse;
import app.telegram.handlers.ExplorarHandler;
import app.telegram.utils.MessageBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExplorarHandlerTest {

  @Mock
  private ServicioFigurita servicioFigurita;

  @Mock
  private MessageBuilder messageBuilder;

  private ExplorarHandler explorarHandler;

  @BeforeEach
  void setUp() {
    explorarHandler = new ExplorarHandler(
        servicioFigurita,
        messageBuilder
    );
  }

  @Test
  void comandos_debeRetornarExplorar() {
    assertEquals(Set.of("/explorar"), explorarHandler.comandos());
  }

  @Test
  void prefijos_debeRetornarBuscar() {
    assertEquals(Set.of("/buscar"), explorarHandler.prefijos());
  }

  @Test
  void handleBuscar_sinArgumentos_debeMostrarAyuda() {
    Update update = crearUpdateConTexto("/buscar", 1L);

    BotResponse response = explorarHandler.handleBuscar(update);

    assertNotNull(response);
    assertTrue(response.texto().contains("Usá el comando así"));
  }

  @Test
  void handleVerFiguritas_conUnaPagina_debeRetornarTextoSinTeclado() {
    Update update = crearUpdate(1L);

    PaginaResultado<FiguritaIntercambiableDto> pagina =
        new PaginaResultado<>(List.of(), 0, 1, 0);

    when(servicioFigurita.buscarFiguritas(null, null, null, null, 1, 5))
        .thenReturn(pagina);

    when(messageBuilder.formatearPagina(pagina))
        .thenReturn("pagina 1");

    BotResponse response = explorarHandler.handleVerFiguritas(update);

    assertEquals("pagina 1", response.texto());
    assertNull(response.teclado());
  }

  @Test
  void handleBuscar_conQueryValida_debeBuscarPorQuery() {
    Update update = crearUpdateConTexto("/buscar Messi", 1L);

    PaginaResultado<FiguritaIntercambiableDto> pagina =
        new PaginaResultado<>(List.of(), 0, 1, 0);

    when(servicioFigurita.buscarPorQuery("Messi", null, 1, 5))
        .thenReturn(pagina);

    when(messageBuilder.formatearPagina(pagina))
        .thenReturn("resultado");

    BotResponse response = explorarHandler.handleBuscar(update);

    assertEquals("resultado", response.texto());

    verify(servicioFigurita)
        .buscarPorQuery("Messi", null, 1, 5);
  }

  @Test
  void handleBuscar_conMultiplesPaginas_debeAgregarTeclado() {
    Update update = crearUpdateConTexto("/buscar Messi", 1L);

    PaginaResultado<FiguritaIntercambiableDto> pagina =
        new PaginaResultado<>(List.of(), 10, 3, 0);

    InlineKeyboardMarkup teclado = mock(InlineKeyboardMarkup.class);

    when(servicioFigurita.buscarPorQuery("Messi", null, 1, 5))
        .thenReturn(pagina);

    when(messageBuilder.formatearPagina(pagina))
        .thenReturn("resultado");

    when(messageBuilder.tecladoPaginacion(1, 3, "figuritas"))
        .thenReturn(teclado);

    BotResponse response = explorarHandler.handleBuscar(update);

    assertEquals("resultado", response.texto());
    assertEquals(teclado, response.teclado());
  }

  @Test
  void handlePaginacion_debeIrALaPaginaSolicitada() {
    Update buscar = crearUpdateConTexto("/buscar Messi", 1L);

    PaginaResultado<FiguritaIntercambiableDto> paginaInicial =
        new PaginaResultado<>(List.of(), 10, 3, 0);

    when(servicioFigurita.buscarPorQuery("Messi", null, 1, 5))
        .thenReturn(paginaInicial);
    when(messageBuilder.formatearPagina(paginaInicial))
        .thenReturn("inicial");

    explorarHandler.handleBuscar(buscar);

    Update callback = crearCallbackUpdate("figuritas:2", 1L);

    PaginaResultado<FiguritaIntercambiableDto> paginaDos =
        new PaginaResultado<>(List.of(), 10, 3, 2);

    when(servicioFigurita.buscarPorQuery("Messi", null, 3, 5))
        .thenReturn(paginaDos);

    when(messageBuilder.formatearPagina(paginaDos))
        .thenReturn("pagina 3");

    BotResponse response = explorarHandler.handlePaginacion(callback);

    assertEquals("pagina 3", response.texto());
  }

//  @Test
//  void handle_siServiceFalla_debeRetornarMensajeDeError() {
//    Update update = crearUpdateConTexto("/explorar", 1L);
//
//    when(servicioFigurita.buscarFiguritas(null, null, null, null, 1, 5))
//        .thenThrow(new RuntimeException("DB caída"));
//
//    BotResponse response = explorarHandler.handleVerFiguritas(update);
//
//    assertTrue(response.texto().contains("Error al obtener las figuritas"));
//  }

  private Update crearUpdate(Long chatId) {
    Update update = mock(Update.class);
    Message message = mock(Message.class);

    when(update.getMessage()).thenReturn(message);
    when(message.getChatId()).thenReturn(chatId);

    return update;
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
