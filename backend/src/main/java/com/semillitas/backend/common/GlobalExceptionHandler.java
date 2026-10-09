package com.semillitas.backend.common;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException ex) {
    Map<String, String> errores = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
    return ResponseEntity.badRequest().body(errores);
  }

  /** No filtra detalles internos al cliente (OWASP A05/A09); el detalle va al log. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> generico(Exception ex) throws Exception {
    if (ex instanceof org.springframework.web.server.ResponseStatusException
        || ex instanceof org.springframework.security.access.AccessDeniedException) throw ex;
    log.error("Error no controlado", ex);
    return ResponseEntity.status(500).body(Map.of("error", "Error interno del servidor"));
  }
}
