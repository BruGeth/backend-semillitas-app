package com.semillitas.backend.salud;

import com.semillitas.backend.matricula.Nino;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "control_salud")
@Getter @Setter @NoArgsConstructor
public class ControlSalud {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "nino_id")
  private Nino nino;

  @Column(nullable = false, length = 30)
  private String tipo;

  @Column(nullable = false)
  private LocalDate fecha;

  @Column(name = "peso_kg", precision = 4, scale = 1)
  private BigDecimal pesoKg;

  @Column(name = "talla_cm", precision = 4, scale = 1)
  private BigDecimal tallaCm;

  @Column(name = "proxima_fecha")
  private LocalDate proximaFecha;

  @Column(nullable = false)
  private Boolean alerta = false;

  @Column(columnDefinition = "TEXT")
  private String observacion;
}

