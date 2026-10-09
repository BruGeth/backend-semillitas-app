package com.semillitas.backend.asistencia;

import com.semillitas.backend.usuario.Usuario;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "asistencia_docente", uniqueConstraints = @UniqueConstraint(name = "uq_asist_doc", columnNames = {"docente_id", "fecha"}))
@Getter @Setter @NoArgsConstructor
public class AsistenciaDocente {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "docente_id")
  private Usuario docente;

  @Column(nullable = false)
  private LocalDate fecha;

  @Column(name = "hora_entrada")
  private LocalTime horaEntrada;

  @Column(name = "hora_salida")
  private LocalTime horaSalida;
}

