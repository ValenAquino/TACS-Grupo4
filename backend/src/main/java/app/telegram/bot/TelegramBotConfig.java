package app.telegram.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

@Configuration
public class TelegramBotConfig {

  @Value("${telegram.bot.token}")
  private String botToken;

  @Bean
  public TelegramBotsLongPollingApplication telegramBotsApplication() {
    return new TelegramBotsLongPollingApplication();
  }

  @Bean
  public CommandLineRunner registerBot(TelegramBotsLongPollingApplication app,
                                       FiguritasBot figuritasBot) {
    return args -> {
      app.registerBot(botToken, figuritasBot);
    };
  }
}