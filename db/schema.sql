-- Semillitas - esquema físico MySQL / MariaDB (v2)
CREATE DATABASE IF NOT EXISTS semillitas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE semillitas;

CREATE TABLE rol (
  id TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(20) NOT NULL UNIQUE            -- DIRECTORA, DOCENTE, PADRE
) ENGINE=InnoDB;

CREATE TABLE usuario (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dni VARCHAR(8) NOT NULL UNIQUE,
  nombres VARCHAR(80) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  telefono VARCHAR(15) NULL,
  email VARCHAR(120) NULL,
  seccion VARCHAR(10) NULL,                      -- solo docentes
  password_hash VARCHAR(100) NOT NULL,           -- BCrypt
  rol_id TINYINT UNSIGNED NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_usuario_dni CHECK (dni REGEXP '^[0-9]{8}$'),
  CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES rol(id)
) ENGINE=InnoDB;

CREATE TABLE salon (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  edad TINYINT NOT NULL,
  turno VARCHAR(10) NOT NULL,
  capacidad SMALLINT NOT NULL DEFAULT 25,
  docente_id BIGINT NULL,
  CONSTRAINT ck_salon_edad CHECK (edad BETWEEN 3 AND 5),
  CONSTRAINT ck_salon_turno CHECK (turno IN ('MANANA','TARDE')),
  CONSTRAINT ck_salon_capacidad CHECK (capacidad BETWEEN 1 AND 40),
  CONSTRAINT fk_salon_docente FOREIGN KEY (docente_id) REFERENCES usuario(id)
) ENGINE=InnoDB;

CREATE TABLE nino (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dni VARCHAR(8) NOT NULL UNIQUE,
  nombres VARCHAR(80) NOT NULL,
  apellidos VARCHAR(80) NOT NULL,
  fecha_nacimiento DATE NOT NULL,
  salon_id BIGINT NULL,
  apoderado_id BIGINT NOT NULL,
  CONSTRAINT ck_nino_dni CHECK (dni REGEXP '^[0-9]{8}$'),
  CONSTRAINT fk_nino_salon FOREIGN KEY (salon_id) REFERENCES salon(id),
  CONSTRAINT fk_nino_apoderado FOREIGN KEY (apoderado_id) REFERENCES usuario(id)
) ENGINE=InnoDB;

CREATE TABLE matricula (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  codigo VARCHAR(20) NULL UNIQUE,
  nino_id BIGINT NOT NULL,
  anio SMALLINT NOT NULL,
  estado VARCHAR(15) NOT NULL DEFAULT 'PREINSCRITO',
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_matricula_estado CHECK (estado IN ('PREINSCRITO','MATRICULADO','TRASLADADO','RETIRADO')),
  CONSTRAINT uq_matricula_nino_anio UNIQUE (nino_id, anio),
  CONSTRAINT fk_matricula_nino FOREIGN KEY (nino_id) REFERENCES nino(id)
) ENGINE=InnoDB;

CREATE TABLE documento_matricula (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  matricula_id BIGINT NOT NULL,
  tipo VARCHAR(30) NOT NULL,
  estado VARCHAR(10) NOT NULL DEFAULT 'PENDIENTE',
  fecha_entrega DATETIME NULL,
  CONSTRAINT ck_doc_tipo CHECK (tipo IN ('DNI_PADRE','DNI_NINO','DIRECCION','FICHA_NINO_SANO')),
  CONSTRAINT ck_doc_estado CHECK (estado IN ('PENDIENTE','COMPLETO')),
  CONSTRAINT uq_doc UNIQUE (matricula_id, tipo),
  CONSTRAINT fk_doc_matricula FOREIGN KEY (matricula_id) REFERENCES matricula(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE asistencia (            -- asistencia de niños (RF-006)
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nino_id BIGINT NOT NULL,
  salon_id BIGINT NOT NULL,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,
  turno VARCHAR(10) NOT NULL,
  estado VARCHAR(12) NOT NULL,
  registrado_por BIGINT NOT NULL,
  CONSTRAINT ck_asist_turno CHECK (turno IN ('MANANA','TARDE')),
  CONSTRAINT ck_asist_estado CHECK (estado IN ('ASISTIO','TARDANZA','FALTA','JUSTIFICADO')),
  CONSTRAINT uq_asist UNIQUE (nino_id, fecha),
  CONSTRAINT fk_asist_nino FOREIGN KEY (nino_id) REFERENCES nino(id),
  CONSTRAINT fk_asist_salon FOREIGN KEY (salon_id) REFERENCES salon(id),
  CONSTRAINT fk_asist_usuario FOREIGN KEY (registrado_por) REFERENCES usuario(id)
) ENGINE=InnoDB;

CREATE TABLE asistencia_docente (    -- asistencia docente (entrada/salida)
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  docente_id BIGINT NOT NULL,
  fecha DATE NOT NULL,
  hora_entrada TIME NULL,
  hora_salida TIME NULL,
  CONSTRAINT uq_asist_doc UNIQUE (docente_id, fecha),
  CONSTRAINT fk_asist_doc FOREIGN KEY (docente_id) REFERENCES usuario(id)
) ENGINE=InnoDB;

CREATE TABLE evaluacion (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nino_id BIGINT NOT NULL,
  docente_id BIGINT NOT NULL,
  area VARCHAR(50) NOT NULL,
  periodo VARCHAR(20) NOT NULL,
  calificacion VARCHAR(2) NOT NULL,
  conclusion TEXT NULL,
  fecha DATE NOT NULL DEFAULT (CURRENT_DATE),
  CONSTRAINT ck_eval_cal CHECK (calificacion IN ('AD','A','B','C')),
  CONSTRAINT fk_eval_nino FOREIGN KEY (nino_id) REFERENCES nino(id),
  CONSTRAINT fk_eval_docente FOREIGN KEY (docente_id) REFERENCES usuario(id)
) ENGINE=InnoDB;

CREATE TABLE control_salud (         -- control de salud (RF-011)
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nino_id BIGINT NOT NULL,
  tipo VARCHAR(30) NOT NULL,
  fecha DATE NOT NULL,
  peso_kg DECIMAL(4,1) NULL,
  talla_cm DECIMAL(4,1) NULL,
  proxima_fecha DATE NULL,
  alerta BOOLEAN NOT NULL DEFAULT FALSE,
  observacion TEXT NULL,
  CONSTRAINT fk_salud_nino FOREIGN KEY (nino_id) REFERENCES nino(id)
) ENGINE=InnoDB;

CREATE TABLE auditoria (             -- RNF-007
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  usuario_dni VARCHAR(8) NULL,
  tabla VARCHAR(40) NOT NULL,
  registro_id BIGINT NOT NULL,
  accion VARCHAR(10) NOT NULL,
  valor_anterior TEXT NULL,
  valor_nuevo TEXT NULL,
  fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Índices clave optimizados
CREATE INDEX idx_asistencia_salon_fecha ON asistencia(salon_id, fecha);
CREATE INDEX idx_salud_proxima ON control_salud(proxima_fecha);
CREATE INDEX idx_nino_salon ON nino(salon_id);
CREATE INDEX idx_nino_apellidos ON nino(apellidos);
CREATE INDEX idx_auditoria_tabla ON auditoria(tabla, registro_id);

INSERT INTO rol(nombre) VALUES ('DIRECTORA'),('DOCENTE'),('PADRE');
