package com.semillitas.backend.asistencia;

import com.semillitas.backend.matricula.Nino;
import com.semillitas.backend.salon.Salon;
import com.semillitas.backend.usuario.Usuario;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "asistencia", uniqueConstraints = @UniqueConstraint(name = "uq_asist", columnNames = {"nino_id", "fecha"}))
@Getter @Setter @NoArgsConstructor
public class Asistencia {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "nino_id")
  private Nino nino;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "salon_id")
  private Salon salon;

  @Column(nullable = false)
  private LocalDate fecha;

  @Column(nullable = false)
  private LocalTime hora;

  @Column(nullable = false, length = 10)
  private String turno; // MANANA, TARDE

  @Column(nullable = false, length = 12)
  private String estado; // ASISTIO, TARDANZA, FALTA, JUSTIFICADO

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "registrado_por")
  private Usuario registradoPor;
}

