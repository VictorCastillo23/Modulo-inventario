# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Java EE (Jakarta EE 10) inventory management webapp ("Almacén") with role-based permissions, built as a WAR deployed to Tomcat, backed by MySQL. Built with Apache NetBeans; no CLI build workflow is documented in the repo.

Stack: JDK 11 (compiler target, see `pom.xml`; README mentions JDK 25 for the IDE), Jakarta EE 10 API (provided scope), JSTL, MySQL Connector/J, packaged as WAR via `maven-war-plugin`.

## Build

```
mvn clean package
```

Produces `target/prueba1-1.0-SNAPSHOT.war`. `src/test/java` holds a JUnit 5 + AssertJ unit test suite (50 tests, run via `mvn test`) covering the pure/static classes in `seguridad/` and the build harness — these run with no container, no DB, and no mocking. Running the app itself still requires deploying the WAR to Apache Tomcat 10.1.x and a MySQL 8.x instance — there's no embedded/local run command; this was developed and run through NetBeans' integrated server tooling.

## Database setup

Run `SCRIPTS/inventario_roles.sql` against MySQL to create the `inventario_roles` database, its schema (`Roles`, `Permisos`, `Roles_Permisos`, `Usuarios`, `Productos`, `Historico`), and seed data (two users: `admin`/`admin` as Administrador, `almacen`/`almacen` as Almacenista).

Connection is resolved by `src/main/java/config/Conexion.java` from `DB_URL`/`DB_USER`/`DB_PASSWORD` (env vars, with a system-property fallback) — deployment fails fast via `config/StartupConfigListener` if any are missing. See `README.md` for the required env-var setup.

## Architecture

Classic Servlet/JSP MVC, no framework (no Spring, no JSF, no JPA in practice despite an empty `persistence.xml` being present and unused — all data access is raw JDBC via DAOs).

- **Controllers** (`controlador/`): `HttpServlet`s mapped via `@WebServlet`.
  - `LoginController` (`/LoginController`) — handles login/logout, populates the session with `usuario`, `idUsuario`, `idRol`, and a `permisos` map (`Map<String, Boolean>`) built from `PermisoDAO`.
  - `ProductosController` (`/ProductosController`) — single servlet fielding all inventory actions via an `accion` request parameter (`nuevo`, `insertar`, `guardarCambios`, `salida_productos`, `guardarSalidas`, `historial`, or empty for the listing). Each branch checks a specific permission key from the session's `permisos` map before proceeding, then forwards to a JSP under `Productos/`.
- **Auth filter** (`filtro/AuthFilter.java`): `@WebFilter` on `/ProductosController` and `/Productos/*`; redirects to `LoginController` if no `usuario` is in session. Permission checks (per-action) are separate from authentication and live inline in `ProductosController`, not in the filter.
- **Models/DAOs** (`modelo/`): plain JDBC DAOs, each opening its own `Connection` via `config.Conexion` in its constructor (no pooling, no shared/request-scoped connection — every `new XyzDAO()` in a servlet method opens a fresh connection).
  - `UsuarioDAO.validar` — credential check.
  - `PermisoDAO.listarPermisosPorRol` — resolves a role's permission set at login time; permissions are cached in the session, not re-checked per request against the DB.
  - `ProductosDAO` — CRUD + stock operations (`agregarCantidad`, `retirarCantidad`, `cambiarEstatus`).
  - `HistoricoDAO` — append-only movement log (`Entrada`/`Salida`), written whenever `ProductosDAO` stock is inserted/adjusted, and read back with a user/product name join for the historial view.
- **Views** (`src/main/webapp/Productos/*.jsp`, `login.jsp`): JSP + JSTL, Bootstrap 5 via CDN, shared header/footer via `WEB-INF/jspf/layout-top.jspf` / `layout-bottom.jspf`. Views read the `permisos` map directly from session scope (e.g. `${permisos.agregar_productos}`) to conditionally render actions/columns.
- **Security controls** (`seguridad/`): pure, static, dependency-free classes (`PasswordHasher`, `CsrfTokens`, `InventoryRequestValidator`, `ValidationResult`/`ValidationError`, `WithdrawalOutcome`, `Permisos`) extracted so they're unit-testable without a container or DB. `controlador`/`filtro`/`modelo` are thin wiring around them.

### Permission model

Permission names checked in Java (`seguridad/Permisos.java` constants) and JSP EL (e.g. `${permisos.agregar_productos}`) are lowercase and now match the names seeded in `SCRIPTS/inventario_roles.sql` (fixed by the SEC-07 remediation, migration `SCRIPTS/migrations/2026-07-28_01_permisos_rename.sql`). `PermisoDAO` does a direct string match with no case normalization — the JSPs mirror `Permisos`' constants as string literals with no compile-time link, so verify casing/wording still matches between the SQL seed, `Permisos`, and the JSPs when touching any of the three.

### Session shape

After login, session attributes are: `usuario` (username string), `idUsuario` (int), `idRol` (int), `permisos` (`Map<String, Boolean>`, only present keys are `true`). Missing key ⇒ treat as no permission (`Boolean.TRUE.equals(...)` pattern used throughout).

## Automated review hooks

`.claude/settings.json` wires the Gentle AI review lenses to real git lifecycle events via `PreToolUse`/`Bash` agent hooks (not just the advisory text in the global Agent Trigger Rules):

- `git commit*` / `git push*` → lightweight, non-blocking **R2 Readability** check (Haiku, advisory only, never blocks).
- `gh pr create*` → checks whether the diff exceeds 400 changed lines or touches `filtro/**`, `config/Conexion.java`, `modelo/*DAO.java`, or `SCRIPTS/**`; if so, runs the full 4R fan-out (risk/resilience/readability/reliability, Sonnet) and blocks the PR (`continue:false`) on any BLOCKER/CRITICAL finding.

Caveat: prefix-match `if` patterns only match when the git/gh invocation is the first command in the Bash call — a compound command like `cd x && git commit ...` won't trigger the hook. Also, since `.claude/` didn't exist before these hooks were added, an existing session may need `/hooks` (or a restart) to pick them up.
