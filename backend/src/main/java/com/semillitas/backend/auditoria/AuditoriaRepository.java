package com.semillitas.backend.auditoria;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
  List<Auditoria> findTop50ByOrderByFechaDesc();
}
