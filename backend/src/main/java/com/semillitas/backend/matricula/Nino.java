package com.semillitas.backend.matricula;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nino")
@Getter @Setter @NoArgsConstructor
public class Nino {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, unique = true, length = 8) private String dni;
  @Column(nullable = false, length = 80) private String nombres;
  @Column(nullable = false, length = 80) private String apellidos;
  @Column(name = "fecha_nacimiento", nullable = false) private LocalDate fechaNacimiento;
  @Column(name = "salon_id") private Long salonId;
  @Column(name = "apoderado_id", nullable = false) private Long apoderadoId;
}
