# Cómo integrar esto en tu backend (sin tocar el frontend)

## 1. Copiar
1. `backend/src/main/java/com/semillitas/backend/**` → mismas carpetas de tu repo (paquetes nuevos: config, security, auth, usuario, matricula, auditoria, common; `BackendApplication.java` no se toca).
2. `backend/src/main/resources/application.yaml` → reemplaza el tuyo.
3. `docs/pom-agregar.xml` → pega las 4 dependencias en tu `pom.xml`.
4. `db/schema.sql` → córrelo en MySQL (Workbench/DBeaver o la BD en la nube).

## 2. Variables de entorno
| Variable | Ejemplo |
|---|---|
| DB_URL | jdbc:mysql://HOST:3306/semillitas?sslMode=REQUIRED |
| DB_USER / DB_PASSWORD | usuario y clave de tu BD |
| JWT_SECRET | cadena aleatoria de 32+ caracteres (`openssl rand -base64 48`) |
| CORS_ORIGINS | https://tu-frontend.vercel.app,http://localhost:5173 |
| SEED_ENABLED / SEED_PASSWORD | `true` y una clave de 6+ caracteres, SOLO para crear los 3 usuarios demo la primera vez; luego `false` |

Nunca subas estos valores al repo.

## 3. Probar (local)
```bash
cd backend && ./mvnw spring-boot:run
# login (mismos DNI que el mock del frontend: 12345678 directora, 87654321 docente, 11223344 padre)
curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"dni":"12345678","password":"TU_SEED_PASSWORD"}'
# usar el token
curl -s localhost:8080/api/matriculas/ninos -H "Authorization: Bearer TOKEN"
# sin token -> 401 ; token de PADRE en /api/matriculas -> 403
```

## 4. El frontend no se rompe
`POST /api/auth/login` devuelve `{ token, user: { id, name, email, role, section } }` con `role` en minúsculas
(`directora|docente|padre`): exactamente lo que ya usa `auth.store.ts`. Mientras no conectes, el front sigue con su mock.
Cuando decidan conectarlo, el único cambio es reemplazar el cuerpo de `login()` en `auth.store.ts`
(axios ya está instalado) y guardar `token` en el store; ningún componente cambia.

## 5. Qué cubre cada artefacto de la rúbrica
| Artefacto APF2 | Dónde está |
|---|---|
| Diseño físico BD + script | db/schema.sql (genera el diagrama ER desde esta BD) |
| Patrón de acceso a datos | `usuario/UsuarioRepository`, `matricula/*Repository` + `MatriculaService` |
| Auth y autorización | `auth/*`, `security/*`, `config/SecurityConfig` |
| Cifrado / trazabilidad | BCrypt(12), JWT HS256, HTTPS del hosting, tabla `auditoria` + `AuditoriaService` |
| RNF-012 inyección SQL | consultas JPA parametrizadas, Bean Validation, regex DNI + CHECK en BD, ZAP/sqlmap |
| Catálogo de controles | tabla abajo |

## 6. Catálogo de controles (borrador)
| ID | Control | Estándar | Implementación |
|---|---|---|---|
| C-01 | Contraseñas con hash | OWASP A02 / ISO 27001 A.8.24 | BCrypt cost 12 |
| C-02 | Autenticación stateless | OWASP A07 | JWT firmado, expira en 1 h |
| C-03 | Control de acceso por rol | OWASP A01 / Ley 29733 | `hasRole` en SecurityConfig, 401/403 |
| C-04 | Prevención de inyección SQL | OWASP A03 / RNF-012 | JPA parametrizado + validación de entrada |
| C-05 | Anti fuerza bruta | OWASP A07 | 5 fallos = bloqueo 15 min, 429 |
| C-06 | Mensajes de error genéricos | OWASP A05 | login sin enumeración; handler global sin stack |
| C-07 | Trazabilidad | OWASP A09 / RNF-007 | tabla `auditoria` (usuario, fecha, antes/después) |
| C-08 | Secretos fuera del código | OWASP A05 | variables de entorno |
| C-09 | CORS restringido | OWASP A05 | solo el dominio del frontend |
| C-10 | Cabeceras de seguridad | OWASP A05 | HSTS, X-Frame-Options deny |
| C-11 | Restricciones en BD | integridad | CHECK de DNI/estados, FK, UNIQUE |
| C-12 | Cifrado en tránsito | ISO 27001 A.8.24 | TLS del proveedor cloud |

## 7. Pendiente / límites (sé honesto en la sustentación)
- No pude compilar aquí (sin acceso a Maven). Compílalo y corrige cualquier detalle de versión: tu pom usa Spring Boot 4.1.1.
- Asistencia, evaluaciones, salud, import/export y documentos de matrícula tienen tabla pero aún no endpoints.
- Anti fuerza bruta es en memoria (se reinicia al reiniciar y no escala a varias instancias).
- Replicación: es propuesta documental (primario + réplica de lectura), no la implementas.
