package com.semillitas.backend.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginAttemptServiceTest {

  @Test @DisplayName("CP-F05 Bloquea al 5.º fallo (RNF-003)")
  void CP_F05_bloqueaAlQuintoFallo() {
    var s = new LoginAttemptService();
    for (int i = 0; i < 4; i++) s.fallo("12345678");
    assertFalse(s.bloqueado("12345678"));
    s.fallo("12345678");
    assertTrue(s.bloqueado("12345678"));
  }

  @Test @DisplayName("CP-F06 Un login exitoso reinicia el contador")
  void CP_F06_exitoReinicia() {
    var s = new LoginAttemptService();
    for (int i = 0; i < 4; i++) s.fallo("12345678");
    s.exito("12345678");
    s.fallo("12345678");
    assertFalse(s.bloqueado("12345678"));
  }

  @Test @DisplayName("CP-F07 El bloqueo es por DNI, no global")
  void CP_F07_bloqueoPorDni() {
    var s = new LoginAttemptService();
    for (int i = 0; i < 5; i++) s.fallo("12345678");
    assertTrue(s.bloqueado("12345678"));
    assertFalse(s.bloqueado("87654321"));
  }
}
