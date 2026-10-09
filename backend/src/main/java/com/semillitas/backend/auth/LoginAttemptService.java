package com.semillitas.backend.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/** Control anti fuerza bruta: 5 fallos por DNI bloquean 15 minutos (en memoria). */
@Service
public class LoginAttemptService {
  private static final int MAX = 5;
  private static final Duration BLOQUEO = Duration.ofMinutes(15);
  private record Intento(int fallos, Instant desde) {}
  private final Map<String, Intento> intentos = new ConcurrentHashMap<>();

  public boolean bloqueado(String dni) {
    Intento i = intentos.get(dni);
    if (i == null) return false;
    if (Instant.now().isAfter(i.desde().plus(BLOQUEO))) { intentos.remove(dni); return false; }
    return i.fallos() >= MAX;
  }

  public void fallo(String dni) {
    intentos.merge(dni, new Intento(1, Instant.now()),
        (a, b) -> new Intento(a.fallos() + 1, a.desde()));
  }

  public void exito(String dni) { intentos.remove(dni); }
}
