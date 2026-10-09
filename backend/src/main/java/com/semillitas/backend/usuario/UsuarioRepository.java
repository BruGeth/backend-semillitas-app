package com.semillitas.backend.usuario;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Patrón Repository: el servicio depende de esta interfaz y no de SQL.
 * Todas las consultas son parametrizadas (RNF-012, OWASP A03 Injection).
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByDni(String dni);

  boolean existsByDni(String dni);

  @Query("select u from Usuario u where lower(u.apellidos) like lower(concat('%', :texto, '%'))")
  List<Usuario> buscarPorApellido(@Param("texto") String texto);
}
