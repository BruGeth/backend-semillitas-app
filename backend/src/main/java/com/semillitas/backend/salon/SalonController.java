package com.semillitas.backend.salon;

import com.semillitas.backend.auditoria.AuditoriaService;
import com.semillitas.backend.usuario.Usuario;
import com.semillitas.backend.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/salones")
public class SalonController {

  private final SalonRepository salones;
  private final UsuarioRepository usuarios;
  private final AuditoriaService auditoria;

  public SalonController(SalonRepository salones, UsuarioRepository usuarios, AuditoriaService auditoria) {
    this.salones = salones;
    this.usuarios = usuarios;
    this.auditoria = auditoria;
  }

  @GetMapping
  public List<Salon> listar() {
    return salones.findAll();
  }

  @GetMapping("/{id}")
  public Salon obtenerPorId(@PathVariable Long id) {
    return salones.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salón no encontrado"));
  }

  @PostMapping
  public ResponseEntity<Salon> crear(@Valid @RequestBody SalonRequest req, Principal principal) {
    Usuario docente = null;
    if (req.docenteId() != null) {
      docente = usuarios.findById(req.docenteId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Docente no encontrado"));
    }
    Salon s = new Salon();
    s.setNombre(req.nombre());
    s.setEdad(req.edad());
    s.setTurno(req.turno());
    s.setCapacidad(req.capacidad());
    s.setDocente(docente);
    Salon guardado = salones.save(s);

    auditoria.registrar(principal != null ? principal.getName() : "SISTEMA", "salon", guardado.getId(), "INSERT", null,
        "nombre=" + guardado.getNombre() + ";capacidad=" + guardado.getCapacidad());

    return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
  }
}

