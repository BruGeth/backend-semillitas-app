package com.semillitas.backend.salud;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ControlSaludRepository extends JpaRepository<ControlSalud, Long> {
  List<ControlSalud> findByNinoIdOrderByFechaDesc(Long ninoId);
  List<ControlSalud> findByAlertaTrueOrderByFechaDesc();
  List<ControlSalud> findByProximaFechaBetweenOrderByProximaFechaAsc(LocalDate desde, LocalDate hasta);
}

