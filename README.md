# ProManage

Gestion de proyectos con Spring Boot (API REST) + Angular.

## Estructura

```
ProManage/
  backend/   → Spring Boot (Java 21, ArrayList en memoria)
  frontend/  → Angular 19
```

## Backend

```bash
cd backend
mvn spring-boot:run
```

API en: http://localhost:8081/api/projects

## Frontend

```bash
cd frontend
npm start
```

App en: http://localhost:4200

## Endpoints

| Metodo | URL |
|--------|-----|
| GET | `/api/projects` |
| GET | `/api/projects/{id}` |
| POST | `/api/projects` |
| PUT | `/api/projects/{id}` |
| DELETE | `/api/projects/{id}` |
