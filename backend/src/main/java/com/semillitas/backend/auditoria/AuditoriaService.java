package com.semillitas.backend.auditoria;

import org.springframework.stereotype.Service;

/** RNF-007: guarda usuario, fecha, valor anterior y nuevo de cada cambio. */
@Service
public class AuditoriaService {
  private final AuditoriaRepository repo;
  public AuditoriaService(AuditoriaRepository repo) { this.repo = repo; }

  public void registrar(String dni, String tabla, long registroId, String accion, String anterior, String nuevo) {
    Auditoria a = new Auditoria();
    a.setUsuarioDni(dni); a.setTabla(tabla); a.setRegistroId(registroId);
    a.setAccion(accion); a.setValorAnterior(anterior); a.setValorNuevo(nuevo);
    repo.save(a);
  }
}
