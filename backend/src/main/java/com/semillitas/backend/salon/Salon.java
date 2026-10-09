package com.semillitas.backend.salon;

import com.semillitas.backend.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "salon")
@Getter @Setter @NoArgsConstructor
public class Salon {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String nombre;

  @Column(nullable = false)
  private Integer edad;

  @Column(nullable = false, length = 10)
  private String turno; // MANANA, TARDE

  @Column(nullable = false)
  private Integer capacidad = 25;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "docente_id")
  private Usuario docente;
}

