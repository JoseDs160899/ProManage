-- ============================================================
--  ProManage - Esquema relacional (Avance 2)
--  Motor: MySQL 8.x
--  Nota: con spring.jpa.hibernate.ddl-auto=update Hibernate
--        crea/actualiza estas tablas automáticamente al arrancar.
--        Este script sirve como ANEXO / referencia del esquema.
-- ============================================================

CREATE DATABASE IF NOT EXISTS promanage
  CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE promanage;

-- ------------------------------------------------------------
--  Usuarios (autenticación / roles)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,        -- BCrypt
    rol      VARCHAR(20)  NOT NULL         -- ADMIN | OPERADOR
);

-- ------------------------------------------------------------
--  Proyectos
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS proyectos (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo             VARCHAR(150) NOT NULL,
    descripcion        VARCHAR(500),
    fecha_vencimiento  DATE,
    prioridad          VARCHAR(20),        -- ALTA | MEDIA | BAJA
    estado             VARCHAR(20)         -- PENDIENTE | EN_PROGRESO | COMPLETADA
);

-- ------------------------------------------------------------
--  Tareas (N:1 con proyectos)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tareas (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo      VARCHAR(150) NOT NULL,
    completada  BIT(1)       NOT NULL DEFAULT 0,
    proyecto_id BIGINT       NOT NULL,
    CONSTRAINT fk_tarea_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos (id)
        ON DELETE CASCADE
);

-- ============================================================
--  Datos de ejemplo (equivalentes al DataInitializer)
--  Los usuarios se insertan automáticamente desde la app con
--  la contraseña ya cifrada con BCrypt:
--      admin    / Admin123    (ADMIN)
--      operador / Operador123 (OPERADOR)
-- ============================================================
INSERT INTO proyectos (titulo, descripcion, fecha_vencimiento, prioridad, estado) VALUES
  ('Sitio web institucional', 'Rediseño del portal corporativo', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'ALTA',  'EN_PROGRESO'),
  ('App móvil de ventas',     'MVP para catálogo y pedidos',     DATE_ADD(CURDATE(), INTERVAL 60 DAY), 'MEDIA', 'PENDIENTE');

INSERT INTO tareas (titulo, completada, proyecto_id) VALUES
  ('Definir wireframes', 1, 1),
  ('Maquetar home',      0, 1),
  ('Integrar CMS',       0, 1),
  ('Modelo de datos',    0, 2),
  ('Pantalla de catálogo', 0, 2);

-- ============================================================
--  Consultas JPQL implementadas (equivalente SQL de referencia)
-- ============================================================
-- Buscar por título (parcial, case-insensitive):
--   SELECT * FROM proyectos WHERE LOWER(titulo) LIKE LOWER('%web%');
-- Filtrar por estado:
--   SELECT * FROM proyectos WHERE estado = 'PENDIENTE' ORDER BY fecha_vencimiento;
-- Contar por estado:
--   SELECT COUNT(*) FROM proyectos WHERE estado = 'PENDIENTE';
-- Tareas pendientes de un proyecto:
--   SELECT * FROM tareas WHERE proyecto_id = 1 AND completada = 0;
