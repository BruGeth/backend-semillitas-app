package com.semillitas.backend.config;

import com.semillitas.backend.asistencia.Asistencia;
import com.semillitas.backend.asistencia.AsistenciaRepository;
import com.semillitas.backend.matricula.Matricula;
import com.semillitas.backend.matricula.MatriculaRepository;
import com.semillitas.backend.matricula.Nino;
import com.semillitas.backend.matricula.NinoRepository;
import com.semillitas.backend.salon.Salon;
import com.semillitas.backend.salon.SalonRepository;
import com.semillitas.backend.salud.ControlSalud;
import com.semillitas.backend.salud.ControlSaludRepository;
import com.semillitas.backend.usuario.Rol;
import com.semillitas.backend.usuario.RolRepository;
import com.semillitas.backend.usuario.Usuario;
import com.semillitas.backend.usuario.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Crea los usuarios y datos base del mock del frontend. Solo si SEED_ENABLED=true; la contraseña viene de SEED_PASSWORD. */
@Configuration
public class DataSeeder {

  @Bean
  CommandLineRunner seed(UsuarioRepository usuarios,
                         RolRepository roles,
                         PasswordEncoder encoder,
                         SalonRepository salones,
                         NinoRepository ninos,
                         MatriculaRepository matriculas,
                         AsistenciaRepository asistencias,
                         ControlSaludRepository controlesSalud,
                         @Value("${app.seed.enabled}") boolean enabled,
                         @Value("${app.seed.password}") String password) {
    return args -> {
      if (!enabled) return;
      if (password == null || password.length() < 6)
        throw new IllegalStateException("SEED_PASSWORD requerido (mínimo 6 caracteres)");

      Usuario dir = crear(usuarios, roles, encoder, "12345678", "María", "García", "987111222", "maria@semillitas.edu.pe", null, "DIRECTORA", password);
      Usuario doc = crear(usuarios, roles, encoder, "87654321", "Ana", "López", "987333444", "ana@semillitas.edu.pe", "A", "DOCENTE", password);
      Usuario pad = crear(usuarios, roles, encoder, "11223344", "Carlos", "Pérez", "987555666", "carlos@email.com", null, "PADRE", password);

      // Sembrar Salón si no existe
      Salon salon = salones.findAll().stream().findFirst().orElseGet(() -> {
        Salon s = new Salon();
        s.setNombre("Aula Amarilla (3 años)");
        s.setEdad(3);
        s.setTurno("MANANA");
        s.setCapacidad(25);
        s.setDocente(doc);
        return salones.save(s);
      });

      // Sembrar Niño si no existe
      Nino nino = ninos.findByDni("70123456").orElseGet(() -> {
        Nino n = new Nino();
        n.setDni("70123456");
        n.setNombres("Mateo");
        n.setApellidos("Pérez Gómez");
        n.setFechaNacimiento(LocalDate.of(2021, 5, 10));
        n.setSalonId(salon.getId());
        n.setApoderadoId(pad.getId());
        return ninos.save(n);
      });

      // Sembrar Matrícula si no existe
      if (matriculas.findByNinoIdAndAnio(nino.getId(), Year.now().getValue()).isEmpty()) {
        Matricula m = new Matricula();
        m.setNinoId(nino.getId());
        m.setAnio(Year.now().getValue());
        m.setEstado("MATRICULADO");
        m = matriculas.save(m);
        m.setCodigo("MAT-" + m.getAnio() + "-" + String.format("%05d", m.getId()));
        matriculas.save(m);
      }

      // Sembrar Asistencia hoy si no existe
      if (asistencias.findByNinoIdAndFecha(nino.getId(), LocalDate.now()).isEmpty()) {
        Asistencia a = new Asistencia();
        a.setNino(nino);
        a.setSalon(salon);
        a.setFecha(LocalDate.now());
        a.setHora(LocalTime.of(8, 15));
        a.setTurno("MANANA");
        a.setEstado("ASISTIO");
        a.setRegistradoPor(doc);
        asistencias.save(a);
      }

      // Sembrar Control de Salud si no existe
      if (controlesSalud.findByNinoIdOrderByFechaDesc(nino.getId()).isEmpty()) {
        ControlSalud cs = new ControlSalud();
        cs.setNino(nino);
        cs.setTipo("CONTROL_PESO_TALLA");
        cs.setFecha(LocalDate.now());
        cs.setPesoKg(new BigDecimal("14.5"));
        cs.setTallaCm(new BigDecimal("95.0"));
        cs.setProximaFecha(LocalDate.now().plusMonths(3));
        cs.setAlerta(false);
        cs.setObservacion("Desarrollo psicomotriz y nutricional adecuado para su edad.");
        controlesSalud.save(cs);
      }
    };
  }

  private Usuario crear(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder, String dni,
                        String nombres, String apellidos, String telefono, String email, String seccion, String rolNombre, String pass) {
    return usuarios.findByDni(dni).orElseGet(() -> {
      Rol rol = roles.findByNombre(rolNombre).orElseThrow();
      Usuario u = new Usuario();
      u.setDni(dni); u.setNombres(nombres); u.setApellidos(apellidos); u.setTelefono(telefono); u.setEmail(email);
      u.setSeccion(seccion); u.setRol(rol); u.setPasswordHash(encoder.encode(pass));
      return usuarios.save(u);
    });
  }
}
