package com.semillitas.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey key;
  private final long expirationMs;

  public JwtService(@Value("${app.jwt.secret}") String secret,
                    @Value("${app.jwt.expiration-ms}") long expirationMs) {
    if (secret == null || secret.length() < 32)
      throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres");
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expirationMs = expirationMs;
  }

  public String generar(String dni, String rol) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .subject(dni)
        .claim("rol", rol)
        .issuedAt(new Date(now))
        .expiration(new Date(now + expirationMs))
        .signWith(key)
        .compact();
  }

  /** Lanza JwtException si el token es inválido, está alterado o expiró. */
  public Claims leer(String token) throws JwtException {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }
}
