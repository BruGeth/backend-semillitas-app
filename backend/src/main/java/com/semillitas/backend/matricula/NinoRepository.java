package com.semillitas.backend.matricula;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NinoRepository extends JpaRepository<Nino, Long> {
  Optional<Nino> findByDni(String dni);

  @Query("select n from Nino n where lower(n.apellidos) like lower(concat('%', :texto, '%')) "
       + "or lower(n.nombres) like lower(concat('%', :texto, '%')) or n.dni = :texto")
  List<Nino> buscar(@Param("texto") String texto);
}
