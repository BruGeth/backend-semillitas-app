package com.semillitas.backend.security;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
  static final String SECRET = "clave-de-prueba-con-mas-de-32-caracteres-123456";

  @Test @DisplayName("CP-F01 Genera y lee un token válido")
  void CP_F01_generaYLee() {
    var s = new JwtService(SECRET, 3_600_000L);
    var c = s.leer(s.generar("87654321", "DOCENTE"));
    assertEquals("87654321", c.getSubject());
    assertEquals("DOCENTE", c.get("rol", String.class));
  }

  @Test @DisplayName("CP-F02 Rechaza secreto menor a 32 caracteres")
  void CP_F02_secretoCorto() {
    assertThrows(IllegalStateException.class, () -> new JwtService("corta", 1000L));
  }

  @Test @DisplayName("CP-F03 Rechaza token alterado")
  void CP_F03_tokenAlterado() {
    var s = new JwtService(SECRET, 3_600_000L);
    String t = s.generar("87654321", "DOCENTE");
    String[] partes = t.split("\\.");
    String alterado = partes[0] + "." + partes[1] + ".firmaFalsaFirmaFalsaFirmaFalsaFirmaFalsa123";
    assertThrows(JwtException.class, () -> s.leer(alterado));
  }

  @Test @DisplayName("CP-F04 Rechaza token expirado")
  void CP_F04_tokenExpirado() {
    var s = new JwtService(SECRET, -1000L);
    String t = s.generar("87654321", "DOCENTE");
    assertThrows(JwtException.class, () -> s.leer(t));
  }
}

