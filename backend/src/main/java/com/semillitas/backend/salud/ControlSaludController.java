package com.semillitas.backend.salud;

import com.semillitas.backend.auditoria.AuditoriaService;
import com.semillitas.backend.matricula.Nino;
import com.semillitas.backend.matricula.NinoRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/salud")
public class ControlSaludController {

  private final ControlSaludRepository saludRepo;
  private final NinoRepository ninos;
  private final AuditoriaService auditoria;

  public ControlSaludController(ControlSaludRepository saludRepo, NinoRepository ninos, AuditoriaService auditoria) {
    this.saludRepo = saludRepo;
    this.ninos = ninos;
    this.auditoria = auditoria;
  }

  @PostMapping
  public ResponseEntity<ControlSalud> registrar(@Valid @RequestBody RegistrarControlSaludRequest req,
                                                Principal principal) {
    Nino nino = ninos.findById(req.ninoId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Niño no encontrado"));

    ControlSalud cs = new ControlSalud();
    cs.setNino(nino);
    cs.setTipo(req.tipo());
    cs.setFecha(req.fecha());
    cs.setPesoKg(req.pesoKg());
    cs.setTallaCm(req.tallaCm());
    cs.setProximaFecha(req.proximaFecha());
    cs.setAlerta(req.alerta() != null ? req.alerta() : false);
    cs.setObservacion(req.observacion());

    ControlSalud guardado = saludRepo.save(cs);

    String usuario = principal != null ? principal.getName() : "SISTEMA";
    auditoria.registrar(usuario, "control_salud", guardado.getId(), "INSERT", null,
        "ninoId=" + nino.getId() + ";tipo=" + guardado.getTipo() + ";alerta=" + guardado.getAlerta());

    return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
  }

  @GetMapping("/nino/{ninoId}")
  public List<ControlSalud> listarPorNino(@PathVariable Long ninoId) {
    return saludRepo.findByNinoIdOrderByFechaDesc(ninoId);
  }

  @GetMapping("/alertas")
  public List<ControlSalud> listarAlertas() {
    return saludRepo.findByAlertaTrueOrderByFechaDesc();
  }

  @GetMapping("/proximos")
  public List<ControlSalud> listarProximos(@RequestParam(defaultValue = "30") int dias) {
    LocalDate hoy = LocalDate.now();
    LocalDate limite = hoy.plusDays(dias);
    return saludRepo.findByProximaFechaBetweenOrderByProximaFechaAsc(hoy, limite);
  }
}

