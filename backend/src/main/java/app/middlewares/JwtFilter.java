package app.middlewares;

import app.exceptions.UnauthorizedException;
import app.servicios.ServicioJwt;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final ServicioJwt servicioJwt;
  private final HandlerExceptionResolver errorHandler;

  public JwtFilter(
      ServicioJwt servicioJwt,
      @Qualifier("handlerExceptionResolver")
      HandlerExceptionResolver errorHandler
  ){
    this.servicioJwt = servicioJwt;
    this.errorHandler = errorHandler;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {

    String path = request.getServletPath();

    return path.startsWith("/login")
        || path.startsWith("/registrar")
        || path.startsWith("/public");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    try {

      servicioJwt.validarToken(obtenerToken(request));

      filterChain.doFilter(request, response);

    } catch (JwtException | UnauthorizedException e) {

      errorHandler.resolveException(
          request,
          response,
          null,
          new UnauthorizedException(
              "Token inválido"
          )
      );
    }
  }

  private String obtenerToken(HttpServletRequest request) {

    Cookie[] cookies = request.getCookies();

    if(cookies == null) {
      throw new UnauthorizedException(
          "No se encontró el token"
      );
    }

    for(Cookie cookie : cookies) {
      if("sesion".equals(cookie.getName())) {
        return cookie.getValue();
      }
    }

    throw new UnauthorizedException(
        "No se encontró el token"
    );
  }

}
