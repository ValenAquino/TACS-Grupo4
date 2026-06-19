package app.telegram.handlers;

import app.dto.FiguritaIntercambiableDto;
import app.dto.paginacion.PaginaResultado;
import app.model.entities.MetodoIntercambio;
import app.servicios.ServicioFigurita;
import app.telegram.bot.FiguritasBot;
import app.telegram.sesion.SessionManager;
import app.telegram.utils.MessageBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RepetidaHandler {

  private final ServicioFigurita figuitaService;
  private final MessageBuilder messageBuilder;
  private final SessionManager sessionManager;
  private final FiguritasBot bot;

  // Guardamos la última búsqueda por chat para poder paginar
  private final Map<Long, String> ultimaQuery = new ConcurrentHashMap<>();
  private final Map<Long, List<MetodoIntercambio>> ultimoTipo = new ConcurrentHashMap<>();

  public RepetidaHandler(ServicioFigurita figuitaService,
                        MessageBuilder messageBuilder,
                        SessionManager sessionManager,
                        @Lazy FiguritasBot bot
  ) {
    this.figuitaService = figuitaService;
    this.messageBuilder = messageBuilder;
    this.sessionManager = sessionManager;
    this.bot = bot;
  }

  // /misfigus → primera página sin filtros
  public void handleVerFiguritas(Update update) {
    long chatId = update.getMessage().getChatId();

    if (!sessionManager.isAuthenticated(chatId)) {
      bot.enviarMensaje(chatId, "⚠️ Necesitás iniciar sesión primero. Usá /login");
      return;
    }

    // Limpiamos filtros previos
    ultimaQuery.remove(chatId);
    ultimoTipo.remove(chatId);

    buscarYMostrar(chatId, null, null, null, null, null, 0);
  }

  // Búsqueda con query libre: "/buscar Messi"
  public void handleBuscar(Update update) {
    long chatId = update.getMessage().getChatId();

    if (!sessionManager.isAuthenticated(chatId)) {
      bot.enviarMensaje(chatId, "⚠️ Necesitás iniciar sesión primero. Usá /login");
      return;
    }

    String[] partes = update.getMessage().getText().split(" ", 2);
    if (partes.length < 2 || partes[1].isBlank()) {
      bot.enviarMensaje(chatId,
          "🔍 Usá el comando así:\n`/buscar Messi`\n`/buscar Argentina`");
      return;
    }

    String query = partes[1].trim();
    ultimaQuery.put(chatId, query);

    buscarYMostrar(chatId, query, null, null, null, null, 0);
  }

  // Maneja los botones de paginación (callback "figuritas:2")
  public void handlePaginacion(Update update) {
    long chatId = update.getCallbackQuery().getMessage().getChatId();
    String data = update.getCallbackQuery().getData(); // "figuritas:2"
    int pagina = Integer.parseInt(data.split(":")[1]);

    String query = ultimaQuery.get(chatId);
    List<MetodoIntercambio> tipo = ultimoTipo.get(chatId);

    buscarYMostrar(chatId, query, null, null, null, tipo, pagina);
  }

  private void buscarYMostrar(long chatId, String q, Integer numero, String seleccion,
                              String jugador, List<MetodoIntercambio> tipo, int pagina) {
    try {
      PaginaResultado<FiguritaIntercambiableDto> resultado;

      if (q != null && !q.isBlank()) {
        resultado = figuitaService.buscarPorQuery(q, tipo, pagina, 5); // 5 por página en Telegram
      } else {
        resultado = figuitaService.buscarFiguritas(numero, seleccion, jugador, tipo, pagina, 5);
      }

      String texto = messageBuilder.formatearPagina(resultado);

      if (resultado.cantidadDePaginas() > 1) {
        InlineKeyboardMarkup teclado = messageBuilder.tecladoPaginacion(
            pagina, resultado.cantidadDePaginas(), "figuritas"
        );
        bot.enviarMensajeConBotones(chatId, texto, teclado);
      } else {
        bot.enviarMensaje(chatId, texto);
      }

    } catch (Exception e) {
      bot.enviarMensaje(chatId, "❌ Error al obtener las figuritas. Intentá de nuevo.");
    }
  }
}
