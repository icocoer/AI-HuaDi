# AGENTS.md

## Project Overview

AI+老年认知衰弱分级干预与智能管理系统 — a Spring Boot 3.2 + Vue 3 web application for elderly cognitive health management.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.0, MyBatis, MySQL, JWT (jjwt 0.12.6), Lombok
- **Frontend**: Vue 3.4, Vite 5, Element Plus 2.5, ECharts 5.5, Axios
- **Testing**: JUnit 5, H2 in-memory DB (test profile), MyBatis test starter

## Key Commands

```bash
# Backend (requires MySQL running on localhost:3306, database: cognitive_health)
./mvnw.cmd spring-boot:run          # Start backend on :8080

# Frontend
cd frontend
npm install                         # First time only
npm run dev                         # Dev server on :3000, proxies /api → :8080

# Tests (use H2, no MySQL needed)
./mvnw.cmd test

# One-click start/stop (Windows)
start.bat                           # Starts MySQL check, backend, frontend
stop.bat                            # Kills processes on :8080 and :3000
```

## Database Setup

Production DB init: `mysql -u root -p < src/main/resources/init-complete.sql`
- Creates `cognitive_health` database with all tables and seed data
- Default credentials: root/123456 (configurable in `application.properties`)
- Default login: admin / admin123

Tests use H2 with `src/test/resources/schema-h2.sql` (DDL) and `test-data.sql` (fixtures).

## Architecture

### Backend Module Layout

`src/main/java/com/example/work_program/modules/{module}/`

Each module follows: `controller/` → `service/` → `service/impl/` → `mapper/` → `entity/`

Modules:
- `system` — User auth, dictionary (sys_user, sys_dict)
- `elder` — Elder health records
- `risk` — Cognitive assessment, risk warnings
- `intervention` — Intervention plans and execution
- `datacollection` — Health data, questionnaires, image reports, smart assessment
- `analysis` — Statistics and risk distribution

### Auth Model

**Not Spring Security.** Uses custom `@LoginRequired` annotation + `JwtInterceptor`:
- Endpoints without `@LoginRequired` are public
- `@LoginRequired(roles = {"admin"})` for role-gated endpoints
- JWT token passed as `Authorization: Bearer <token>`
- Login/register endpoints are excluded in `WebConfig`

### ID Generation

Uses Snowflake IDs (`SnowflakeIdGenerator`), not auto-increment. Entity IDs are `Long` (BIGINT).

### API Response Format

All controllers return `Result<T>` with shape: `{ code, message, data }`
- `Result.success(data)` — code 200
- `Result.error(msg)` — code 500
- `Result.error(401, msg)` — auth errors

### Exception Handling

`GlobalExceptionHandler` catches all exceptions. `BusinessException` is the intended way to throw business errors with custom codes.

## Frontend Structure

`frontend/src/`
- `api/index.js` — Axios instance and API functions
- `router/index.js` — Vue Router config
- `views/` — Page components (Login, Dashboard, ElderList, etc.)
- `components/` — Reusable modals (AddEditModal, DetailModal, ImportExcel)
- `utils/export.js` — Excel export utility

## Quirks & Gotchas

- MyBatis mapper scan: `@MapperScan("com.example.work_program.modules.*.mapper")` — new modules must be under `modules/`
- `mybatis.configuration.map-underscore-to-camel-case=true` — DB columns are snake_case, Java fields are camelCase
- Frontend Vite proxy only forwards `/api` prefix — all backend endpoints must start with `/api/`
- `fill_template.py` and `reformat_report.py` are standalone doc-generation scripts, not part of the app
- `server.error.include-message=never` — Spring error responses hide details; use `GlobalExceptionHandler` for user-facing errors

## Existing Instructions

See `CLAUDE.md` for skill framework (superpowers-zh). The skills there govern brainstorming, TDD, debugging workflows.
