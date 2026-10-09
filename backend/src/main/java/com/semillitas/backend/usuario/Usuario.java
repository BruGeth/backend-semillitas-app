package com.semillitas.backend.usuario;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter @Setter @NoArgsConstructor
public class Usuario {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 8)
  private String dni;

  @Column(nullable = false, length = 80)
  private String nombres;

  @Column(nullable = false, length = 80)
  private String apellidos;

  @Column(length = 120)
  private String email;

  @Column(length = 10)
  private String seccion;

  @Column(name = "password_hash", nullable = false, length = 100)
  private String passwordHash;

  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "rol_id")
  private Rol rol;

  @Column(nullable = false)
  private boolean activo = true;

  @Column(name = "creado_en", insertable = false, updatable = false)
  private LocalDateTime creadoEn;

  public String nombreCompleto() { return nombres + " " + apellidos; }
}
