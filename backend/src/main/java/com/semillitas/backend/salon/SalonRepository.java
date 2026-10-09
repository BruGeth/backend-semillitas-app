package com.semillitas.backend.salon;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonRepository extends JpaRepository<Salon, Long> {
  List<Salon> findByDocenteId(Long docenteId);
}

