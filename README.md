# ProManage

Gestión de proyectos con **Spring Boot** (API REST) + **Angular**.
Avance 2: persistencia con **MySQL + JPA**, consultas **JPQL**, transacciones y
seguridad con **Spring Security + JWT** (roles ADMIN / OPERADOR).

## Estructura

```
ProManage/
  backend/   → Spring Boot (Java 21, JPA/MySQL, Security + JWT)
  frontend/  → Angular 19 (login + token JWT + CRUD por rol)
  docs/      → esquema.sql, INFORME_AVANCE2.md, GUIA_PRUEBAS.md
```

## Requisitos previos

- JDK 21 · Maven · Node 18+ · **MySQL 8** en ejecución.
- Crear la base (o dejar que la cree la app): `CREATE DATABASE promanage;`
- Ajustar credenciales en `backend/src/main/resources/application.properties`:
  ```properties
  spring.datasource.username=root
  spring.datasource.password=TU_CONTRASEÑA
  ```

## Backend

```bash
cd backend
mvn spring-boot:run
```

API en `http://localhost:8081`. Al primer arranque se crean las tablas y los
usuarios de prueba.

Usuarios: `admin / Admin123` (ADMIN) · `operador / Operador123` (OPERADOR).

## Frontend

```bash
cd frontend
npm install
npm start
```

App en `http://localhost:4200` (pantalla de login).

## Endpoints

| Método | URL | Acceso |
|--------|-----|--------|
| POST | `/api/auth/login` | Público |
| GET | `/api/publico/saludo` | Público |
| GET | `/api/projects` | ADMIN, OPERADOR |
| GET | `/api/projects/{id}` | ADMIN, OPERADOR |
| GET | `/api/projects/buscar?texto=` | ADMIN, OPERADOR (JPQL) |
| GET | `/api/projects/estado/{estado}` | ADMIN, OPERADOR (JPQL) |
| GET | `/api/projects/contar/{estado}` | ADMIN, OPERADOR (JPQL) |
| POST | `/api/projects` | ADMIN |
| PUT | `/api/projects/{id}` | ADMIN |
| PATCH | `/api/projects/{id}/completar` | ADMIN (transacción) |
| DELETE | `/api/projects/{id}` | ADMIN |
| GET | `/api/tareas/proyecto/{id}` | ADMIN, OPERADOR |
| GET | `/api/tareas/proyecto/{id}/pendientes` | ADMIN, OPERADOR (JPQL) |
| POST | `/api/tareas/proyecto/{id}` | ADMIN |
| PATCH | `/api/tareas/{id}/completar` | ADMIN |
| DELETE | `/api/tareas/{id}` | ADMIN |

## Pruebas

- Tests backend: `cd backend && mvn test`
- Evidencias (curl/Postman): ver `docs/GUIA_PRUEBAS.md`
- Informe del Avance 2: ver `docs/INFORME_AVANCE2.md`
