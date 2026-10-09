package com.semillitas.backend.matricula;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

  private final MatriculaService service;
  public MatriculaController(MatriculaService service) { this.service = service; }

  @PostMapping
  public ResponseEntity<?> preinscribir(@Valid @RequestBody PreinscripcionRequest req, Principal p) {
    Matricula m = service.preinscribir(req, p.getName());
    return ResponseEntity.status(201).body(Map.of("id", m.getId(), "codigo", m.getCodigo(), "estado", m.getEstado()));
  }

  @GetMapping("/ninos")
  public List<Nino> buscar(@RequestParam(defaultValue = "") String texto) {
    return service.buscarNinos(texto);
  }
}
