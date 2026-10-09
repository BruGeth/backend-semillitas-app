package com.semillitas.backend.asistencia;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
  Optional<Asistencia> findByNinoIdAndFecha(Long ninoId, LocalDate fecha);
  List<Asistencia> findBySalonIdAndFecha(Long salonId, LocalDate fecha);
  List<Asistencia> findByNinoIdOrderByFechaDesc(Long ninoId);
}

