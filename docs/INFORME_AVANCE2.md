# INFORME TÉCNICO — AVANCE 2 DEL PROYECTO FINAL
### Desarrollo Web Integrado · Proyecto: **ProManage** (Gestión de Proyectos)

> Documento base siguiendo la estructura oficial de 12 rubros. Completa los
> campos entre `[corchetes]` y reemplaza los marcadores `(captura: ...)` por tus
> capturas de pantalla reales. Las evidencias técnicas se generan con
> `docs/GUIA_PRUEBAS.md`.

**Equipo:** [integrantes] · **Clase:** [NRC] · **Fecha:** [fecha]

---

## 1. Introducción
ProManage es una aplicación para la **gestión de proyectos** y sus tareas. En el
**Avance 1** se construyó una API REST con datos en memoria y un cliente Angular
con el CRUD de proyectos. En el **Avance 2** el proyecto evoluciona hacia un
**backend persistente y seguro**: los datos se almacenan en MySQL mediante Spring
Data JPA, se incorporan consultas JPQL y operaciones transaccionales, y se protege
la API con **Spring Security + JWT** y control de acceso por roles.

## 2. Descripción del problema y requerimientos
Las organizaciones necesitan registrar y dar seguimiento a sus proyectos y a las
tareas asociadas, controlando **quién** puede consultar y **quién** puede modificar
la información.

Requerimientos que justifican la solución:
- **Persistir** proyectos y tareas (no perder datos al reiniciar).
- Controlar el **acceso por roles**: un administrador gestiona (crea/edita/elimina)
  y un operador solo consulta.
- Resolver **consultas** frecuentes: buscar proyectos por título, filtrar por estado
  y contar por estado.
- Garantizar **consistencia** en operaciones que afectan a varios registros
  (completar un proyecto y todas sus tareas en una sola operación).
- Proteger los endpoints de escritura y dejar públicos los de prueba.

## 3. Descripción de la solución
La API REST del Avance 1 evoluciona a un backend conectado a base de datos y
protegido:

```
API REST  →  Persistencia JPA/MySQL  →  Consultas/Transacciones  →  Seguridad JWT
```

- **Persistencia:** entidades `Proyecto`, `Tarea` y `Usuario` gestionadas con
  Spring Data JPA/Hibernate sobre MySQL.
- **Modelo de dominio:** un `Proyecto` tiene muchas `Tarea` (relación 1:N).
- **Consultas/Transacciones:** consultas JPQL en los repositorios y métodos de
  servicio anotados con `@Transactional`.
- **Seguridad:** autenticación con usuario/contraseña (BCrypt), emisión de **JWT**
  y autorización por roles **ADMIN** / **OPERADOR** mediante Spring Security
  (modelo *stateless*).
- **Cliente Angular:** pantalla de login, almacenamiento del token, interceptor que
  envía `Authorization: Bearer <token>` y ocultamiento de acciones según el rol.

## 4. Alcances, limitaciones y avance alcanzado
**Implementado y verificado (Avance 2):**
- Entidades JPA: `Proyecto`, `Tarea`, `Usuario`.
- CRUD de proyectos persistido en MySQL.
- Consultas JPQL: por título, por estado y conteo por estado; tareas por proyecto.
- Operación transaccional: completar proyecto + sus tareas.
- Seguridad JWT con roles ADMIN/OPERADOR; respuestas 200 / 401 / 403.
- Login y control de acceso en el frontend Angular.

**Pendiente (siguientes semanas):**
- Paginación y validaciones avanzadas.
- Despliegue en servidor.

## 5. Arquitectura y estructura del proyecto
Backend organizado por capas con responsabilidades separadas:

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| Controller | `controller` | Expone endpoints HTTP (`/api/...`). |
| Service | `service` | Lógica de negocio y transacciones. |
| Repository | `repository` | Acceso a datos con Spring Data JPA + JPQL. |
| Model/Entity | `model` | Entidades persistentes (`Proyecto`, `Tarea`, `Usuario`). |
| Security | `security` / `config` | JWT, filtro, reglas de acceso. |

Flujo: **Controller → Service → Repository → JPA/Hibernate → MySQL**.
Flujo de seguridad: **login → JWT → JwtAuthenticationFilter → SecurityContext → autorización**.

```
backend/src/main/java/com/example/promanage/
├── ProManageApplication.java
├── config/      → SecurityConfig, DataInitializer
├── controller/  → AuthController, ProyectoController, TareaController, PublicoController
├── dto/         → LoginRequest, LoginResponse
├── model/       → Proyecto, Tarea, Usuario
├── repository/  → ProyectoRepository, TareaRepository, UsuarioRepository
├── security/    → JwtService, JwtAuthenticationFilter
└── service/     → ProyectoService, TareaService, CustomUserDetailsService
```
(captura: estructura de paquetes en el IDE)

## 6. Persistencia de datos con Spring Boot
- **Motor:** MySQL (base `promanage`). Esquema en `docs/esquema.sql`.
- **Entidades JPA:** `Proyecto` (1) — (N) `Tarea`; `Usuario`.
- **Repositorios:** interfaces que extienden `JpaRepository`.
- **Conexión** (`application.properties`):
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/promanage
  spring.jpa.hibernate.ddl-auto=update
  ```
- **CRUD demostrado:** GET / POST / PUT / DELETE sobre `/api/projects`.
- **Persistencia comprobada** tras reiniciar la aplicación (los datos permanecen).

(captura: tabla `proyectos` en MySQL Workbench con los registros)

## 7. Consultas y transacciones
**Consultas JPQL** (`ProyectoRepository` / `TareaRepository`):

| Propósito | JPQL | Endpoint |
|-----------|------|----------|
| Buscar por título (parcial) | `SELECT p FROM Proyecto p WHERE LOWER(p.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))` | `GET /api/projects/buscar?texto=web` |
| Filtrar por estado | `SELECT p FROM Proyecto p WHERE p.estado = :estado` | `GET /api/projects/estado/PENDIENTE` |
| Contar por estado | `SELECT COUNT(p) FROM Proyecto p WHERE p.estado = :estado` | `GET /api/projects/contar/PENDIENTE` |
| Tareas pendientes | `SELECT t FROM Tarea t WHERE t.proyecto.id = :id AND t.completada = false` | `GET /api/tareas/proyecto/1/pendientes` |

**Transacción** (`ProyectoService.completarProyecto`): marca el proyecto como
`COMPLETADA` y, en la **misma transacción**, completa todas sus tareas. Si algo
falla, se revierte todo (consistencia). Endpoint: `PATCH /api/projects/{id}/completar`.

(captura: petición y respuesta de una consulta JPQL y de la transacción)

## 8. Seguridad de la API
- **Usuarios de prueba:** `admin / Admin123` (ADMIN), `operador / Operador123` (OPERADOR).
- **Roles:** ADMIN (gestión total) y OPERADOR (solo lectura).
- **Mecanismo:** autenticación con `AuthenticationManager` + BCrypt y emisión de
  **JWT** en `POST /api/auth/login`. Modelo *stateless*.
- **Reglas de autorización:**
  - Público: `/api/auth/**`, `/api/publico/**`.
  - Lectura (`GET`) de proyectos/tareas: **ADMIN y OPERADOR**.
  - Escritura (POST/PUT/PATCH/DELETE): **solo ADMIN**.
- **Respuestas:** sin token → **401**; token válido sin permiso → **403**;
  autorizado → **200**.

(captura: login con token, acceso ADMIN 200 y acceso OPERADOR 403)

## 9. Tecnologías y configuración del backend
| Elemento | Valor |
|----------|-------|
| Java / JDK | 21 |
| Spring Boot | 3.4.5 |
| Spring Data JPA | Persistencia (Hibernate) |
| Spring Security | Autenticación/autorización |
| JWT | JJWT 0.12.6 |
| MySQL | 8.x (base `promanage`) |
| Frontend | Angular 19 |
| Build | Maven (`spring-boot-maven-plugin`) |

Dependencias principales: `spring-boot-starter-web`, `-data-jpa`, `-security`,
`-validation`, `mysql-connector-j`, `jjwt-api/impl/jackson`.

## 10. Pruebas y evidencias de funcionamiento
Guía reproducible paso a paso en `docs/GUIA_PRUEBAS.md`. Evidencias mínimas:

| Prueba | Resultado esperado |
|--------|--------------------|
| `GET /api/publico/saludo` sin token | 200 |
| `GET /api/projects` sin token | 401 |
| Login `admin` | 200 + JWT |
| Login inválido | 401 |
| `GET /api/projects` con token ADMIN | 200 |
| `POST /api/projects` con token OPERADOR | 403 |
| `POST/PUT/DELETE /api/projects` con ADMIN | 201/200/204 + persistencia |
| Consulta JPQL `/buscar?texto=web` | 200 + lista filtrada |
| Transacción `/api/projects/{id}/completar` | 200 + proyecto y tareas completadas |

(captura: cada caso con petición, respuesta y verificación en MySQL)

## 11. Conclusiones y trabajo pendiente
- Se logró evolucionar la API a un backend **persistente (MySQL)** y **seguro (JWT)**.
- Se demostró **JPQL** y una **transacción** real sobre datos relacionados.
- El control de acceso diferencia correctamente **ADMIN** y **OPERADOR** (200/401/403).
- **Pendiente:** gestión de tareas en el frontend, paginación/validaciones y despliegue.

## 12. Anexos técnicos
- `docs/esquema.sql` — esquema relacional y datos de ejemplo.
- `docs/GUIA_PRUEBAS.md` — comandos de prueba (evidencias).
- Diagrama entidad-relación: `proyectos (1) —— (N) tareas`; `usuarios`.
- Listado de endpoints: ver `README.md`.
- Fragmentos de código: `JwtService`, `SecurityConfig`, `ProyectoService.completarProyecto`.
