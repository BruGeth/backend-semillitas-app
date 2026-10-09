package com.semillitas.backend.asistencia;

import com.semillitas.backend.auditoria.AuditoriaService;
import com.semillitas.backend.matricula.Nino;
import com.semillitas.backend.matricula.NinoRepository;
import com.semillitas.backend.salon.Salon;
import com.semillitas.backend.salon.SalonRepository;
import com.semillitas.backend.usuario.Usuario;
import com.semillitas.backend.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

  private final AsistenciaRepository asistencias;
  private final AsistenciaDocenteRepository asistenciasDocente;
  private final NinoRepository ninos;
  private final SalonRepository salones;
  private final UsuarioRepository usuarios;
  private final AuditoriaService auditoria;

  public AsistenciaController(AsistenciaRepository asistencias,
                              AsistenciaDocenteRepository asistenciasDocente,
                              NinoRepository ninos,
                              SalonRepository salones,
                              UsuarioRepository usuarios,
                              AuditoriaService auditoria) {
    this.asistencias = asistencias;
    this.asistenciasDocente = asistenciasDocente;
    this.ninos = ninos;
    this.salones = salones;
    this.usuarios = usuarios;
    this.auditoria = auditoria;
  }

  @PostMapping("/ninos")
  public ResponseEntity<Asistencia> registrarNino(@Valid @RequestBody RegistrarAsistenciaNinoRequest req,
                                                  Principal principal) {
    Usuario docente = usuarios.findByDni(principal.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));

    Nino nino = ninos.findById(req.ninoId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Niño no encontrado"));

    Salon salon = salones.findById(req.salonId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salón no encontrado"));

    Asistencia a = asistencias.findByNinoIdAndFecha(nino.getId(), req.fecha())
        .orElseGet(Asistencia::new);

    String accion = a.getId() == null ? "INSERT" : "UPDATE";
    String estadoAnterior = a.getEstado();

    a.setNino(nino);
    a.setSalon(salon);
    a.setFecha(req.fecha());
    a.setHora(req.hora() != null ? req.hora() : LocalTime.now());
    a.setTurno(req.turno());
    a.setEstado(req.estado());
    a.setRegistradoPor(docente);

    Asistencia guardada = asistencias.save(a);

    auditoria.registrar(docente.getDni(), "asistencia", guardada.getId(), accion,
        estadoAnterior, "ninoId=" + nino.getId() + ";estado=" + guardada.getEstado());

    return ResponseEntity.status(accion.equals("INSERT") ? HttpStatus.CREATED : HttpStatus.OK).body(guardada);
  }

  @GetMapping("/salon/{salonId}")
  public List<Asistencia> listarPorSalonYFecha(
      @PathVariable Long salonId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
    LocalDate target = fecha != null ? fecha : LocalDate.now();
    return asistencias.findBySalonIdAndFecha(salonId, target);
  }

  @GetMapping("/nino/{ninoId}")
  public List<Asistencia> historialPorNino(@PathVariable Long ninoId) {
    return asistencias.findByNinoIdOrderByFechaDesc(ninoId);
  }

  @PostMapping("/docente")
  public ResponseEntity<AsistenciaDocente> marcarDocente(@Valid @RequestBody MarcarAsistenciaDocenteRequest req,
                                                         Principal principal) {
    Usuario docente = usuarios.findByDni(principal.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));

    AsistenciaDocente ad = asistenciasDocente.findByDocenteIdAndFecha(docente.getId(), req.fecha())
        .orElseGet(() -> {
          AsistenciaDocente nueva = new AsistenciaDocente();
          nueva.setDocente(docente);
          nueva.setFecha(req.fecha());
          return nueva;
        });

    if (req.horaEntrada() != null) ad.setHoraEntrada(req.horaEntrada());
    if (req.horaSalida() != null) ad.setHoraSalida(req.horaSalida());

    AsistenciaDocente guardada = asistenciasDocente.save(ad);
    return ResponseEntity.ok(guardada);
  }

  @GetMapping("/docente/hoy")
  public ResponseEntity<AsistenciaDocente> obtenerMiAsistenciaHoy(Principal principal) {
    Usuario docente = usuarios.findByDni(principal.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));
    return asistenciasDocente.findByDocenteIdAndFecha(docente.getId(), LocalDate.now())
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.noContent().build());
  }
}
