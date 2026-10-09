package com.semillitas.backend.asistencia;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaDocenteRepository extends JpaRepository<AsistenciaDocente, Long> {
  Optional<AsistenciaDocente> findByDocenteIdAndFecha(Long docenteId, LocalDate fecha);
}

