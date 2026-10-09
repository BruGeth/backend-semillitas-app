package com.semillitas.backend.matricula;

import com.semillitas.backend.auditoria.AuditoriaService;
import com.semillitas.backend.usuario.Usuario;
import com.semillitas.backend.usuario.UsuarioRepository;
import java.time.Year;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MatriculaService {

  private final NinoRepository ninos;
  private final MatriculaRepository matriculas;
  private final UsuarioRepository usuarios;
  private final AuditoriaService auditoria;

  public MatriculaService(NinoRepository ninos, MatriculaRepository matriculas,
                          UsuarioRepository usuarios, AuditoriaService auditoria) {
    this.ninos = ninos; this.matriculas = matriculas; this.usuarios = usuarios; this.auditoria = auditoria;
  }

  @Transactional
  public Matricula preinscribir(PreinscripcionRequest r, String usuarioDni) {
    Usuario apoderado = usuarios.findByDni(r.dniApoderado())
        .filter(u -> "PADRE".equals(u.getRol().getNombre()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Apoderado no registrado"));
    if (ninos.findByDni(r.dniNino()).isPresent())
      throw new ResponseStatusException(HttpStatus.CONFLICT, "El niño ya está registrado");

    Nino n = new Nino();
    n.setDni(r.dniNino()); n.setNombres(r.nombres()); n.setApellidos(r.apellidos());
    n.setFechaNacimiento(r.fechaNacimiento()); n.setApoderadoId(apoderado.getId());
    n = ninos.save(n);

    Matricula m = new Matricula();
    m.setNinoId(n.getId()); m.setAnio(Year.now().getValue());
    m = matriculas.save(m);
    m.setCodigo("MAT-" + m.getAnio() + "-" + String.format("%05d", m.getId()));
    m = matriculas.save(m);

    auditoria.registrar(usuarioDni, "matricula", m.getId(), "INSERT", null,
        "codigo=" + m.getCodigo() + ";estado=" + m.getEstado() + ";ninoId=" + n.getId());
    return m;
  }

  public List<Nino> buscarNinos(String texto) { return ninos.buscar(texto); }
}
