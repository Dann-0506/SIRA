# SIRA — Sistema Institucional de Registro Académico

Sistema web para gestionar el ciclo completo de evaluación académica: alumnos, maestros, materias, grupos, actividades, calificaciones y reportes.

## Arquitectura

```
SIRA/
├── backend/    Spring Boot 4 · REST API · Java 21
└── frontend/   React 19 · TypeScript · Vite
```

El backend expone una API REST protegida con JWT. El frontend consume esa API y renderiza las vistas según el rol del usuario autenticado.

## Stack

| Capa | Tecnología |
|---|---|
| API | Spring Boot 4 · Spring Security · Spring Data JPA |
| Base de datos | PostgreSQL 14+ |
| Autenticación | JWT (JJWT 0.12) · BCrypt |
| Frontend | React 19 · TypeScript · Vite · Tailwind CSS |
| Estado cliente | TanStack Query · Zustand |

## Roles

| Rol | Acceso |
|---|---|
| **Administrador** | Gestión completa: alumnos, maestros, materias, grupos, inscripciones, configuración, carga CSV y análisis. Puede reabrir grupos y cerrar definitivamente. |
| **Maestro** | Operación de sus grupos activos: actividades y rúbrica por unidad, registro de calificaciones, bonus, reporte y cierre de evaluación. |
| **Alumno** | Consulta de sus cursos y calificaciones. Solo lectura. |

## Flujo de evaluación

```
Grupo ABIERTO
  └─ Maestro configura actividades y ponderaciones por unidad
  └─ Maestro registra calificaciones (auto-guardado por campo)
  └─ Maestro cierra cada unidad (requiere calificaciones completas)
  └─ Maestro termina la evaluación (requiere todas las unidades cerradas)
Grupo CERRADO  ←──── Admin puede reabrir
  └─ Admin cierra definitivamente (irreversible)
Grupo FINALIZADO
```

## Instalación y Ejecución (Recomendada con Docker/Podman)

La forma más rápida y segura de levantar el sistema es utilizando contenedores, ya que automatiza la creación de la base de datos, aplica los triggers de integridad de forma transparente y sirve el frontend preconfigurado.

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd SIRA
```

### 2. Variables de entorno

Copia el archivo de ejemplo para configurar el entorno de Docker:

```bash
cp .env.example .env
```
*(Edita el `.env` en la raíz si deseas cambiar la contraseña de PostgreSQL o el secreto JWT).*

### 3. Iniciar el sistema

```bash
docker-compose up -d --build
# Si usas Podman: podman-compose up -d --build
```

El sistema estará disponible en:
- **App:** `http://localhost:8888`
- **API Backend:** `http://localhost:8080`

> **Nota sobre la Base de Datos:** Al levantar los contenedores por primera vez, PostgreSQL ejecutará automáticamente los scripts `database/01_schema.sql` y `database/02_constraints_triggers.sql`. Esto es **crítico para la integridad del sistema** (bloqueo de calificaciones en grupos cerrados, etc.).

---

## Instalación Manual (Desarrollo)

Si necesitas ejecutar los servicios por separado para desarrollar:

**Requisitos:** Java 21+, Node.js 20+, PostgreSQL 14+

1. **Variables de entorno (Backend)**: 
   Copia el archivo de configuración dentro de la carpeta del backend.
   ```bash
   cp .env.example backend/.env
   ```
2. **Base de datos**: Crea la base `sira` en tu PostgreSQL local.
3. **Scripts SQL**: **ANTES** de iniciar el backend por primera vez, ejecuta manualmente los scripts de la carpeta `database/` para asegurar la correcta estructura y los triggers de integridad.
   ```bash
   psql -U postgres -d sira -f database/01_schema.sql
   psql -U postgres -d sira -f database/02_constraints_triggers.sql
   ```
4. **Backend**: 
   ```bash
   cd backend
   mvn spring-boot:run
   ```
5. **Frontend**:
   ```bash
   cd frontend
   npm install
   npm run dev  # Disponible en http://localhost:5173
   ```

## Flujo Óptimo de Captura de Datos

Para probar el sistema desde cero (ya sea de forma manual o utilizando la herramienta de importación CSV), se recomienda crear los catálogos en el siguiente orden lógico de dependencia:

1. **Carreras**: Base necesaria para registrar alumnos.
2. **Administradores**: Personal de gestión.
3. **Maestros**: Necesarios para asignar a los grupos.
4. **Alumnos**: Requieren una carrera existente.
5. **Materias**: Base para crear grupos.
6. **Actividades**: Catálogo de tipos de evaluación.
7. **Grupos**: Requieren una materia y un maestro.
8. **Inscripciones**: Requieren un alumno y un grupo.

*(Puedes encontrar archivos CSV de muestra listos para importar en la carpeta `database/csv-samples/`)*.

## Credenciales por defecto

Al arrancar con una base de datos vacía, el sistema genera automáticamente un administrador inicial:

| Campo | Valor |
|---|---|
| Email | `admin@escuela.edu` |
| Contraseña | `123456` |
| Rol | Administrador |

> Los usuarios nuevos (alumnos y maestros importados/creados) reciben como contraseña temporal su **fecha de nacimiento en formato `DDMMYYYY`** (ej. `14052003`). Al iniciar sesión por primera vez se les solicita cambiarla obligatoriamente.

## Colaboradores

- Daniel Landero Arias
- Ximena Zaleta Hernández
- Othón Vladimir López Ortiz
- Habid Fernández Betanzos
- Ángel Miguel Medina Mixtega