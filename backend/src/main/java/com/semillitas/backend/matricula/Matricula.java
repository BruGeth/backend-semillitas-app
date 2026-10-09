package com.semillitas.backend.matricula;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "matricula")
@Getter @Setter @NoArgsConstructor
public class Matricula {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(length = 20, unique = true) private String codigo;
  @Column(name = "nino_id", nullable = false) private Long ninoId;
  @Column(nullable = false) private Integer anio;
  @Column(nullable = false, length = 15) private String estado = "PREINSCRITO";
  @Column(name = "creado_en", insertable = false, updatable = false) private LocalDateTime creadoEn;
}
