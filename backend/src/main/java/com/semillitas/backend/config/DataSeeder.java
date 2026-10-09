package com.semillitas.backend.config;

import com.semillitas.backend.usuario.Rol;
import com.semillitas.backend.usuario.RolRepository;
import com.semillitas.backend.usuario.Usuario;
import com.semillitas.backend.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Crea los 3 usuarios del mock del frontend (mismos DNI). Solo si SEED_ENABLED=true; la contraseña viene de SEED_PASSWORD. */
@Configuration
public class DataSeeder {

  @Bean
  CommandLineRunner seed(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder,
                         @Value("${app.seed.enabled}") boolean enabled,
                         @Value("${app.seed.password}") String password) {
    return args -> {
      if (!enabled) return;
      if (password == null || password.length() < 6)
        throw new IllegalStateException("SEED_PASSWORD requerido (mínimo 6 caracteres)");
      crear(usuarios, roles, encoder, "12345678", "María", "García", "maria@semillitas.edu.pe", null, "DIRECTORA", password);
      crear(usuarios, roles, encoder, "87654321", "Ana", "López", "ana@semillitas.edu.pe", "A", "DOCENTE", password);
      crear(usuarios, roles, encoder, "11223344", "Carlos", "Pérez", "carlos@email.com", null, "PADRE", password);
    };
  }

  private void crear(UsuarioRepository usuarios, RolRepository roles, PasswordEncoder encoder, String dni,
                     String nombres, String apellidos, String email, String seccion, String rolNombre, String pass) {
    if (usuarios.existsByDni(dni)) return;
    Rol rol = roles.findByNombre(rolNombre).orElseThrow();
    Usuario u = new Usuario();
    u.setDni(dni); u.setNombres(nombres); u.setApellidos(apellidos); u.setEmail(email);
    u.setSeccion(seccion); u.setRol(rol); u.setPasswordHash(encoder.encode(pass));
    usuarios.save(u);
  }
}
