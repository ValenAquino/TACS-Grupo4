package app.telegram.bot;

import app.telegram.handlers.CommandHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class FiguritasBot implements LongPollingUpdateConsumer {

  private final CommandHandler commandHandler;
  private final TelegramClient telegramClient;

  @Value("${telegram.bot.token}")
  private String botToken;

  public FiguritasBot(CommandHandler commandHandler,
                      @Value("${telegram.bot.token}") String botToken) {
    this.commandHandler = commandHandler;
    this.telegramClient = new OkHttpTelegramClient(botToken);
  }

  @Override
  public void consume(List<Update> updates) {
    updates.forEach(update -> {
      System.out.println(">>> UPDATE RECIBIDO: " + update); // log temporal
      if (update.hasMessage() && update.getMessage().hasText()) {
        commandHandler.handle(update);
      }
    });
  }

  // Los handlers llaman a estos métodos para enviar mensajes
  public void enviarMensaje(long chatId, String texto) {
    SendMessage msg = SendMessage.builder()
        .chatId(chatId)
        .text(texto)
        .parseMode("Markdown")
        .build();
    try {
      telegramClient.execute(msg);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  public void enviarMensajeConBotones(long chatId, String texto, InlineKeyboardMarkup teclado) {
    SendMessage msg = SendMessage.builder()
        .chatId(chatId)
        .text(texto)
        .parseMode("Markdown")
        .replyMarkup(teclado)
        .build();
    try {
      telegramClient.execute(msg);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}
