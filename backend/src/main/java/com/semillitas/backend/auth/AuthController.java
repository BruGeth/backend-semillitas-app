package com.semillitas.backend.auth;

import com.semillitas.backend.auditoria.AuditoriaService;
import com.semillitas.backend.security.JwtService;
import com.semillitas.backend.usuario.Usuario;
import com.semillitas.backend.usuario.UsuarioRepository;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UsuarioRepository usuarios;
  private final PasswordEncoder encoder;
  private final JwtService jwt;
  private final LoginAttemptService intentos;
  private final AuditoriaService auditoria;

  public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder, JwtService jwt,
                        LoginAttemptService intentos, AuditoriaService auditoria) {
    this.usuarios = usuarios; this.encoder = encoder; this.jwt = jwt;
    this.intentos = intentos; this.auditoria = auditoria;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
    if (intentos.bloqueado(req.dni()))
      return ResponseEntity.status(429).body(Map.of("error", "Demasiados intentos. Intente más tarde."));

    Usuario u = usuarios.findByDni(req.dni()).filter(Usuario::isActivo).orElse(null);
    // mensaje genérico: no revela si el DNI existe
    if (u == null || !encoder.matches(req.password(), u.getPasswordHash())) {
      intentos.fallo(req.dni());
      auditoria.registrar(req.dni(), "usuario", u == null ? 0L : u.getId(), "LOGIN_FAIL", null, null);
      return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
    }
    intentos.exito(req.dni());
    auditoria.registrar(u.getDni(), "usuario", u.getId(), "LOGIN_OK", null, null);

    String rol = u.getRol().getNombre();
    var dto = new LoginResponse.UserDto(String.valueOf(u.getId()), u.nombreCompleto(), u.getEmail(),
        rol.toLowerCase(), u.getSeccion());
    return ResponseEntity.ok(new LoginResponse(jwt.generar(u.getDni(), rol), dto));
  }
}
