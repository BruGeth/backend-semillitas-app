package com.semillitas.backend.auditoria;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auditoria")
@Getter @Setter @NoArgsConstructor
public class Auditoria {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "usuario_dni", length = 8) private String usuarioDni;
  @Column(nullable = false, length = 40) private String tabla;
  @Column(name = "registro_id", nullable = false) private Long registroId;
  @Column(nullable = false, length = 10) private String accion;
  @Column(name = "valor_anterior", columnDefinition = "TEXT") private String valorAnterior;
  @Column(name = "valor_nuevo", columnDefinition = "TEXT") private String valorNuevo;
  @Column(nullable = false) private LocalDateTime fecha = LocalDateTime.now();
}
