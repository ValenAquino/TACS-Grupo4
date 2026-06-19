package app.telegram.bot;

import app.telegram.handlers.CommandHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class FiguritasBot extends TelegramLongPollingBot {

  @Value("${telegram.bot.token}")
  private String botToken;

  @Value("${telegram.bot.username}")
  private String botUsername;

  private final CommandHandler commandHandler;

  public FiguritasBot(CommandHandler commandHandler) {
    this.commandHandler = commandHandler;
  }

  @Override
  public String getBotToken() { return botToken; }

  @Override
  public String getBotUsername() { return botUsername; }

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      commandHandler.handle(update);
    }
  }
}
