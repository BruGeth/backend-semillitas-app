package com.semillitas.backend.matricula;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
  Optional<Matricula> findByNinoIdAndAnio(Long ninoId, Integer anio);
}
