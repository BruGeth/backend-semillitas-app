package com.semillitas.backend.matricula;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.semillitas.backend.auditoria.AuditoriaService;
import com.semillitas.backend.usuario.Rol;
import com.semillitas.backend.usuario.Usuario;
import com.semillitas.backend.usuario.UsuarioRepository;
import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

  @Mock NinoRepository ninos;
  @Mock MatriculaRepository matriculas;
  @Mock UsuarioRepository usuarios;
  @Mock AuditoriaService auditoria;
  @InjectMocks MatriculaService service;

  PreinscripcionRequest req() {
    return new PreinscripcionRequest("70000001", "LUCIA", "MENDOZA", LocalDate.of(2022, 5, 10), "11223344");
  }

  Usuario usuarioConRol(String rol) {
    Rol r = new Rol(); r.setNombre(rol);
    Usuario u = new Usuario(); u.setId(3L); u.setDni("11223344"); u.setRol(r);
    return u;
  }

  @Test @DisplayName("CP-F08 Preinscripción correcta genera código MAT-AAAA-NNNNN (RF-001)")
  void CP_F08_preinscripcionOk() {
    when(usuarios.findByDni("11223344")).thenReturn(Optional.of(usuarioConRol("PADRE")));
    when(ninos.findByDni("70000001")).thenReturn(Optional.empty());
    when(ninos.save(any(Nino.class))).thenAnswer(i -> { Nino n = i.getArgument(0); n.setId(7L); return n; });
    when(matriculas.save(any(Matricula.class))).thenAnswer(i -> {
      Matricula m = i.getArgument(0); if (m.getId() == null) m.setId(5L); return m; });

    Matricula m = service.preinscribir(req(), "12345678");

    assertEquals("MAT-" + Year.now().getValue() + "-00005", m.getCodigo());
    assertEquals("PREINSCRITO", m.getEstado());
    verify(auditoria).registrar(eq("12345678"), eq("matricula"), eq(5L), eq("INSERT"), isNull(), anyString());
  }

  @Test @DisplayName("CP-F09 Apoderado no registrado responde 404")
  void CP_F09_apoderadoNoRegistrado() {
    when(usuarios.findByDni("11223344")).thenReturn(Optional.empty());
    var ex = assertThrows(ResponseStatusException.class, () -> service.preinscribir(req(), "12345678"));
    assertEquals(404, ex.getStatusCode().value());
    verify(ninos, never()).save(any());
  }

  @Test @DisplayName("CP-F10 Un usuario que no es PADRE no puede ser apoderado")
  void CP_F10_apoderadoConOtroRol() {
    when(usuarios.findByDni("11223344")).thenReturn(Optional.of(usuarioConRol("DOCENTE")));
    var ex = assertThrows(ResponseStatusException.class, () -> service.preinscribir(req(), "12345678"));
    assertEquals(404, ex.getStatusCode().value());
  }

  @Test @DisplayName("CP-F11 Niño ya registrado responde 409 y no crea matrícula")
  void CP_F11_ninoDuplicado() {
    when(usuarios.findByDni("11223344")).thenReturn(Optional.of(usuarioConRol("PADRE")));
    when(ninos.findByDni("70000001")).thenReturn(Optional.of(new Nino()));
    var ex = assertThrows(ResponseStatusException.class, () -> service.preinscribir(req(), "12345678"));
    assertEquals(409, ex.getStatusCode().value());
    verify(matriculas, never()).save(any());
  }
}
