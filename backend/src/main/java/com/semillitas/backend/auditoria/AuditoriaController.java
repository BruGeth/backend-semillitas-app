package com.semillitas.backend.auditoria;

import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {
  private final AuditoriaRepository repo;
  public AuditoriaController(AuditoriaRepository repo) { this.repo = repo; }

  @GetMapping
  public List<Auditoria> ultimos() { return repo.findTop50ByOrderByFechaDesc(); }
}
