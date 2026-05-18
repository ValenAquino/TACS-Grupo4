package app.servicios.impl;

import app.dto.SesionDto;
import app.model.entities.Perfil;
import app.model.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class ServicioJwt {
  private final String SECRET =
      "jhggsddahjbujbyutydrrtweawqawq4456778689879864422345";

  /**
   Metodo que genera un token con UserId, Rol, PerfilId y ColId.
   */
  public String generarToken(
      Usuario usuario,
      Perfil perfil
  ) {

    return Jwts.builder()
        .setSubject(usuario.getId())

        .claim("usuarioId", usuario.getId())
        .claim("rol", usuario.getRol().toString())
        .claim("perfilId", perfil.getId())
        //.claim("colId", perfil.getColeccion().getId())

        .setIssuedAt(new Date())

        .setExpiration(
            new Date(
                System.currentTimeMillis()
                    + 1000 * 60 * 60 * 12
            )
        )

        .signWith(
            getSignKey(),
            SignatureAlgorithm.HS256
        )

        .compact();
  }

  /**
   * Metodo que firma el token.
  */
  private Key getSignKey() {

    return Keys.hmacShaKeyFor(
        SECRET.getBytes(StandardCharsets.UTF_8)
    );
  }

  /**
   * Metodo que valida el token. Valida tanto por expiracion como por firma (si fue modificado).
   */
  public Claims validarToken(String token) {

    return Jwts.parserBuilder()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * Metodo que valida el token y devuelve los datos guardados en el.
   */
  public SesionDto obtenerSesion(String token) {

    Claims claims = validarToken(token);

    return new SesionDto(
        claims.get("usuarioId", String.class),
        claims.get("rol", String.class),
        claims.get("perfilId", String.class),
        claims.get("colId", String.class)
    );
  }
}
