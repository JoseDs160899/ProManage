# Guía de pruebas y evidencias — Avance 2 (ProManage)

Comandos reproducibles para generar las evidencias del informe (rubro 10).
Puedes usar **curl** (abajo) o **Postman**. El backend corre en
`http://localhost:8081`.

> Antes de empezar: arranca MySQL, luego el backend (`mvn spring-boot:run`).
> La primera vez se crean las tablas y los usuarios de prueba automáticamente.

## Usuarios de prueba
| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `admin` | `Admin123` | ADMIN |
| `operador` | `Operador123` | OPERADOR |

---

## 1. Recurso público (200 sin token)
```bash
curl -i http://localhost:8081/api/publico/saludo
```
➡ **200 OK**

## 2. Recurso protegido sin token (401)
```bash
curl -i http://localhost:8081/api/projects
```
➡ **401 Unauthorized**

## 3. Login válido (200 + JWT)
```bash
curl -i -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123"}'
```
➡ **200 OK** con `{ "token": "...", "username": "admin", "rol": "ADMIN" }`.
Copia el token en una variable:
```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')
echo "$TOKEN"
```

## 4. Login inválido (401)
```bash
curl -i -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"malaClave"}'
```
➡ **401 Unauthorized**

## 5. Listar proyectos con token ADMIN (200)
```bash
curl -i http://localhost:8081/api/projects -H "Authorization: Bearer $TOKEN"
```
➡ **200 OK**

## 6. CRUD de proyectos (ADMIN)
```bash
# Crear (201)
curl -i -X POST http://localhost:8081/api/projects \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"titulo":"Nuevo proyecto","descripcion":"Demo","fechaVencimiento":"2026-12-31","prioridad":"ALTA","estado":"PENDIENTE"}'

# Actualizar (200) — reemplaza {id}
curl -i -X PUT http://localhost:8081/api/projects/1 \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"titulo":"Actualizado","descripcion":"Demo","fechaVencimiento":"2026-12-31","prioridad":"MEDIA","estado":"EN_PROGRESO"}'

# Eliminar (204)
curl -i -X DELETE http://localhost:8081/api/projects/3 -H "Authorization: Bearer $TOKEN"
```

## 7. Consultas JPQL (ADMIN u OPERADOR)
```bash
curl -s "http://localhost:8081/api/projects/buscar?texto=web"   -H "Authorization: Bearer $TOKEN"
curl -s "http://localhost:8081/api/projects/estado/PENDIENTE"   -H "Authorization: Bearer $TOKEN"
curl -s "http://localhost:8081/api/projects/contar/PENDIENTE"   -H "Authorization: Bearer $TOKEN"
curl -s "http://localhost:8081/api/tareas/proyecto/1/pendientes" -H "Authorization: Bearer $TOKEN"
```

## 8. Transacción: completar proyecto + sus tareas (ADMIN)
```bash
curl -i -X PATCH http://localhost:8081/api/projects/1/completar -H "Authorization: Bearer $TOKEN"
```
➡ **200 OK** con `estado: "COMPLETADA"` y todas sus tareas `completada: true`.

## 9. OPERADOR: lectura permitida, escritura prohibida (403)
```bash
TOP=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"operador","password":"Operador123"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')

# Lectura: 200
curl -i http://localhost:8081/api/projects -H "Authorization: Bearer $TOP"

# Escritura: 403 Forbidden
curl -i -X POST http://localhost:8081/api/projects \
  -H "Authorization: Bearer $TOP" -H "Content-Type: application/json" \
  -d '{"titulo":"X","descripcion":"x","fechaVencimiento":"2026-12-31","prioridad":"BAJA","estado":"PENDIENTE"}'
```

## 10. Verificación en MySQL
```sql
USE promanage;
SELECT * FROM usuarios;
SELECT * FROM proyectos;
SELECT * FROM tareas;
```

---

### Resumen de códigos
| Situación | Código |
|-----------|--------|
| Recurso público | 200 |
| Sin token en recurso protegido | 401 |
| Credenciales inválidas | 401 |
| Autenticado sin permiso (OPERADOR escribe) | 403 |
| Autorizado | 200 / 201 / 204 |
