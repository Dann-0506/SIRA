# SIRA — Sistema Institucional de Registro Académico

Sistema web para gestionar el ciclo completo de evaluación académica: alumnos, maestros, materias, grupos, actividades, calificaciones y reportes.

## Características Principales

- **Gestión Centralizada de Periodos:** Control total sobre los ciclos académicos, fechas de vigencia y criterios de evaluación normalizados (calificaciones mínimas y máximas por periodo).
- **Filtrado Avanzado:** Sistema de búsqueda y filtrado dinámico en todos los catálogos administrativos mediante un menú de filtros profesional.
- **Experiencia de Usuario Resiliente:** Manejo estético de estados de error con capacidad de reintento y pantallas informativas para listas vacías.
- **Seguridad y Triggers:** Integridad de datos garantizada mediante disparadores a nivel base de datos para bloqueos de actas, validación de periodos y promedios.
- **Importación Inteligente:** Carga masiva mediante CSV con detección automática del periodo escolar vigente.

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
| Base de datos | PostgreSQL 16+ |
| Autenticación | JWT (JJWT 0.12) · BCrypt |
| Frontend | React 19 · TypeScript · Vite · Tailwind CSS |
| Estado cliente | TanStack Query · Zustand |

## Roles

| Rol | Acceso |
|---|---|
| **Administrador** | Gestión completa: alumnos, maestros, materias, grupos, inscripciones, periodos escolares, carga CSV y análisis operativo. |
| **Maestro** | Operación de sus grupos activos: rúbrica por unidad, registro de calificaciones con búsqueda rápida, bonus, reporte y cierre de evaluación. |
| **Alumno** | Consulta de sus cursos, histórico de calificaciones y boletas de periodos anteriores. |

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

## Instalación y Ejecución

La forma más rápida y segura de levantar el sistema es utilizando contenedores.

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd SIRA
```

### 2. Variables de entorno

Copia el archivo de ejemplo para configurar el entorno:

```bash
cp .env.example .env
```

### 3. Iniciar el sistema

```bash
docker-compose up -d --build
# Si usas Podman: podman-compose up -d --build
```

El sistema estará disponible en:
- **App:** `http://localhost:8888`
- **API Backend:** `http://localhost:8080`

> **Nota Crítica:** Debido a la normalización de la base de datos, si estás actualizando desde una versión anterior, debes limpiar los volúmenes antiguos con `podman compose down -v` para permitir que el nuevo esquema de periodos se aplique correctamente.

---

## Flujo Óptimo de Captura de Datos

Para probar el sistema desde cero se recomienda seguir este orden:

1. **Periodos**: Definir el ciclo escolar actual y sus límites de calificación.
2. **Carreras**: Base para registrar alumnos.
3. **Maestros / Administradores**: Personal operativo.
4. **Materias**: Definición de carga académica.
5. **Grupos**: Vinculación de materia, maestro y periodo.
6. **Inscripciones**: Registro de alumnos en grupos.

*(Puedes encontrar archivos CSV de muestra actualizados en `database/csv-samples/`)*.

## Credenciales por defecto

| Campo | Valor |
|---|---|
| Email | `admin@escuela.edu` |
| Contraseña | `123456` |
| Rol | Administrador |

## Colaboradores

- Daniel Landero Arias
- Ximena Zaleta Hernández
- Othón Vladimir López Ortiz
- Habid Fernández Betanzos
- Ángel Miguel Medina Mixtega