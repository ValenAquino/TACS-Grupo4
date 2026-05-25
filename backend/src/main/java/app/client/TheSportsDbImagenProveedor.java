package app.client;

import app.client.dto.TheSportsDbPlayerDto;
import app.client.dto.TheSportsDbResponse;
import app.exceptions.RateLimitException;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.text.Normalizer;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class TheSportsDbImagenProveedor implements ImagenJugadorProveedor {

  private final RestTemplate restTemplate;
  private final RateLimiter rateLimiter;
  private final Retry retry;
  private final String baseUrl;
  private final String apiKey;

  @Autowired
  public TheSportsDbImagenProveedor(
      RestTemplateBuilder builder,
      @Value("${thesportsdb.base-url}") String baseUrl,
      @Value("${thesportsdb.api-key}") String apiKey,
      @Value("${thesportsdb.rpm:30}") int rpm,
      @Value("${thesportsdb.retry.max-attempts:3}") int maxAttempts,
      @Value("${thesportsdb.retry.wait-seconds:60}") long waitSeconds) {

    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
    this.rateLimiter = crearRateLimiter(rpm);
    this.retry = crearRetry(maxAttempts, waitSeconds);
    this.restTemplate = builder
        .setConnectTimeout(Duration.ofSeconds(3))
        .setReadTimeout(Duration.ofSeconds(3))
        .build();
  }

  /**
   * Constructor para tests: acepta un RestTemplate mockeado, sin rate limiting ni retry.
   */
  TheSportsDbImagenProveedor(RestTemplate restTemplate, String baseUrl, String apiKey) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
    this.rateLimiter = crearRateLimiter(Integer.MAX_VALUE);
    this.retry = crearRetry(1, 0);
  }

  @Override
  public Optional<String> buscarImagen(String nombreJugador) {
    return Retry.decorateSupplier(retry,
        RateLimiter.decorateSupplier(rateLimiter,
            () -> buscarImagenInterna(nombreJugador))
    ).get();
  }

  private Optional<String> buscarImagenInterna(String nombreJugador) {
    String format = "%s/%s/searchplayers.php?p=%s";
    String url = String.format(format, baseUrl, apiKey, normalizarNombre(nombreJugador));

    try {
      TheSportsDbResponse response = restTemplate.getForObject(url, TheSportsDbResponse.class);
      return extraerThumb(response);
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
        throw new RateLimitException("Rate limit alcanzado para TheSportsDB (HTTP 429)");
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Normaliza el nombre para la URL: NFD descompone cada carácter especial en sus partes
   * ({@code á} → {@code a} + acento), una regex elimina los acentos sueltos, luego
   * minúsculas y espacios a {@code _}.
   *
   * <p>Ej: {@code "Di María"} → {@code "di_maria"}, {@code "Mbappé"} → {@code "mbappe"}.
   */
  private static String normalizarNombre(String nombre) {
    if (nombre == null) return "";
    String nfd = Normalizer.normalize(nombre, Normalizer.Form.NFD);
    return nfd.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
        .toLowerCase()
        .replace(' ', '_');
  }

  private static Optional<String> extraerThumb(TheSportsDbResponse response) {
    if (response == null)
      return Optional.empty();

    List<TheSportsDbPlayerDto> jugadores = response.getPlayer();
    if (jugadores == null || jugadores.isEmpty())
      return Optional.empty();

    String thumb = jugadores.get(0).getStrThumb();
    return (thumb != null && !thumb.isBlank())
        ? Optional.of(thumb)
        : Optional.empty();
  }

  private static RateLimiter crearRateLimiter(int rpm) {
    return RateLimiter.of("thesportsdb", RateLimiterConfig.custom()
        .limitForPeriod(rpm)
        .limitRefreshPeriod(Duration.ofMinutes(1))
        .timeoutDuration(Duration.ofSeconds(5))
        .build());
  }

  private static Retry crearRetry(int maxAttempts, long waitSeconds) {
    return Retry.of("thesportsdb", RetryConfig.custom()
        .maxAttempts(maxAttempts)
        .waitDuration(Duration.ofSeconds(waitSeconds))
        .retryOnException(e -> e instanceof RateLimitException)
        .build());
  }
}
