# Auditoría de seguridad — Modulo-inventario

**Fecha del hallazgo original:** 2026-07-28
**Fecha de esta actualización:** 2026-07-29
**Alcance:** revisión estática/manual de todo el árbol de código fuente (`src/main/java`, `src/main/webapp`, `SCRIPTS/`, `src/main/resources`, `pom.xml`, `README.md`). No incluye testing dinámico (no hay un deployment corriendo contra el cual atacar), ni auditoría de infraestructura/Tomcat, ni fuzzing.
**Metodología:** (1) reverificación de la superficie de autenticación/autorización/datos/config/salida/errores/dependencias/sesión; (2) lookup real de CVE contra NVD/Snyk para las dependencias declaradas en `pom.xml`; (3) barrido acotado mapeado a OWASP Top 10 para cerrar categorías no cubiertas; (4) red de greps mecánica como chequeo de cierre. Detalle completo del proceso en el plan de auditoría.

## Estado de remediación (actualizado 2026-07-29)

**10 de 11 hallazgos remediados.** El pase original (2026-07-28) fue find/report puro, sin corregir nada. Después se ejecutó un ciclo SDD completo (propuesta → spec → diseño → tareas → implementación → verificación) que cerró todos los hallazgos salvo SEC-10, que sigue siendo de solo monitoreo (nunca hubo un CVE explotable que arreglar). El trabajo se hizo en 6 commits/PRs independientes en la rama `security-remediation`, mergeada a `main` en el PR #1.

| Fase / commit | Hallazgos cerrados |
|---|---|
| `test: agregar arnés de testing` | — (arnés JUnit5/AssertJ/surefire, prerequisito de todo lo demás) |
| `fix: SEC-07/SEC-01/SEC-02` (atómico) | SEC-07, SEC-01, SEC-02 |
| `fix: SEC-03` | SEC-03 |
| `fix: SEC-04/SEC-05/SEC-06` | SEC-04, SEC-05, SEC-06 |
| `fix: SEC-08` | SEC-08 |
| `chore: SEC-09/SEC-11` | SEC-09, SEC-11 |

**Verificación real:** se desplegó el WAR reconstruido a un Tomcat 10.1.52 + MySQL 8.0 reales (no solo `mvn test`) y se confirmó login funcional con `admin`/`admin` y `almacen`/`almacen` (ahora con hash BCrypt) y permisos correctos por rol. El `sdd-verify` de esa implementación quedó en **PASS WITH WARNINGS** (0 críticos): detectó que ni `mvn test` ni `mvn clean package` parsean `web.xml` — por eso un bug real de XML (comentarios con `--` suelto, ver nota en SEC-08) pasó desapercibido hasta el deploy real y hubo que corregirlo fuera del ciclo automatizado. Tres verificaciones puntuales (rechazo explícito de CSRF, tope de stock contra DB real + flags de cookie en devtools, y disparar a mano una excepción para ver la error-page) quedaron como manuales-only, sin automatizar, dado el alcance portfolio/localhost del proyecto.

---

## Tabla de hallazgos

| ID | Título | Severidad | Estado | Ubicación |
|---|---|---|---|---|
| SEC-01 | Credenciales de base de datos hardcodeadas en el código fuente | Crítico | ✅ Remediado | `config/Conexion.java:16` |
| SEC-02 | Contraseñas de usuario en texto plano, sin hashing | Crítico | ✅ Remediado | `modelo/UsuarioDAO.java:22`, `SCRIPTS/inventario_roles.sql:28` |
| SEC-03 | Sin protección CSRF en formularios que modifican estado | Alto | ✅ Remediado | JSPs bajo `Productos/`, `login.jsp` |
| SEC-04 | Sin validación de stock mínimo en servidor (stock negativo) | Alto | ✅ Remediado | `modelo/ProductosDAO.java:147-148` |
| SEC-05 | Sin defensa contra session fixation | Alto | ✅ Remediado | `controlador/LoginController.java:68` |
| SEC-06 | Sin `httpOnly`/`secure` en cookies de sesión, sin enforcement de HTTPS | Alto | ✅ Remediado (parcial por diseño) | `WEB-INF/web.xml`, `META-INF/context.xml` |
| SEC-07 | Mismatch de nombres de permisos entre SQL y código (falla cerrada) | Medio | ✅ Remediado | `SCRIPTS/inventario_roles.sql:56-63` vs `controlador/ProductosController.java` |
| SEC-08 | Validación de input insuficiente en acciones de inventario (NPE/AIOOBE/NFE no capturadas, sin `<error-page>`) | Medio | ✅ Remediado | `controlador/ProductosController.java:86-114,129-156` |
| SEC-09 | Endpoint JAX-RS sin autenticación | Bajo | ✅ Remediado (eliminado) | `com/mycompany/prueba1/resources/JakartaEE10Resource.java` |
| SEC-10 | Dependencias desactualizadas / a vigilar (sin CVEs explotables confirmados) | Bajo | 🔍 Monitoreo (sin cambios) | `pom.xml` |
| SEC-11 | Artefacto SQL muerto e inconsistente con otra credencial sembrada | Bajo | ✅ Remediado (eliminado) | `src/main/resources/sql/crear_usuarios.sql` |

---

## Detalle por hallazgo

### SEC-01 — Credenciales de base de datos hardcodeadas (Crítico)

**Descripción:** `Conexion.java:16` contiene `DriverManager.getConnection("jdbc:mysql://localhost:3306/inventario_roles?serverTimezone=UTC", "root", "admin")` — usuario `root` y contraseña en texto plano, commiteados al repositorio. El mismo string se repite en `README.md:50-52` como instrucción de setup.

**Impacto:** cualquiera con acceso de lectura al repositorio (incluyendo un repo público, un fork, o un colaborador temporal) obtiene la credencial de administrador de la base de datos. Si esas credenciales se reutilizan en cualquier entorno real (no solo local), es compromiso total de la BD.

**Evidencia:**
```java
// config/Conexion.java:16
Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventario_roles?serverTimezone=UTC", "root", "admin");
```

**✅ Estado: Remediado** (commit `fix: SEC-07/SEC-01/SEC-02`). `Conexion.getConexion()` ahora lee `DB_URL`/`DB_USER`/`DB_PASSWORD` de variables de entorno (con fallback a system properties) y tira `IllegalStateException` — nombrando solo la variable faltante, nunca un valor — si falta cualquiera. `config.StartupConfigListener` corre esa validación una vez al desplegar, así que un entorno mal configurado falla el deploy en vez de fallar silenciosamente en la primera consulta. El string hardcodeado se borró de `README.md` y se reemplazó por una tabla de variables de entorno requeridas.

---

### SEC-02 — Contraseñas en texto plano, sin hashing (Crítico)

**Descripción:** `UsuarioDAO.validar()` compara la contraseña directamente en la consulta SQL (`WHERE ... AND contraseña = ?`), y la columna `contraseña VARCHAR(25)` en `SCRIPTS/inventario_roles.sql:28` almacena el valor tal cual. No hay ningún algoritmo de hashing (bcrypt/argon2/PBKDF2) en todo el código fuente — confirmado exhaustivamente por grep (`md5|sha1|MessageDigest|BCrypt|PBKDF2|Argon2` → 0 resultados en `src/main/java`). Las credenciales por defecto (`admin`/`admin`, `almacen`/`almacen`) están además documentadas en texto plano en `README.md:76-83`.

**Impacto:** un volcado de la tabla `Usuarios` (por backup expuesto, inyección en otra parte del sistema, o acceso directo a la BD) revela las contraseñas reales de todos los usuarios sin ningún esfuerzo adicional. Combinado con SEC-01 (credenciales de BD también expuestas), el camino de compromiso es directo.

**Evidencia:**
```java
// modelo/UsuarioDAO.java:22
String sql = "SELECT idUsuario, correo, idRol FROM Usuarios WHERE correo = ? AND contraseña = ? AND estatus = 1";
```

**✅ Estado: Remediado** (commit `fix: SEC-07/SEC-01/SEC-02`). Nueva clase pura `seguridad.PasswordHasher` (wrapper de `at.favre.lib:bcrypt`, dependencia nueva) con `hash`/`verify` — la comparación ahora pasa por Java, no por el `WHERE` del SQL. Columna `Usuarios.contraseña` ensanchada a `VARCHAR(60)` para el hash BCrypt, con migración idempotente (`SCRIPTS/migrations/2026-07-28_02_password_hash.sql` + `_down`) para bases ya existentes. Los usuarios demo `admin`/`admin` y `almacen`/`almacen` se mantuvieron (decisión explícita del usuario, para que se pueda seguir entrando a probar el portfolio) pero ahora sembrados con hashes reales precalculados, no texto plano. `PasswordHasher` tiene 7 tests unitarios (round-trip, contraseña incorrecta, salting distinto, nulls, hash legado en texto plano falla cerrado).

---

### SEC-03 — Sin protección CSRF (Alto)

**Descripción:** ninguno de los formularios POST que modifican estado (`Productos/nuevo.jsp:24` insertar, `Productos/index.jsp:50` guardarCambios, `Productos/modificar.jsp:33` guardarSalidas, `login.jsp:31`) incluye un token CSRF. `AuthFilter.java:28-36` solo verifica que exista una sesión (`session.getAttribute("usuario") != null`); no valida origen ni referer.

**Impacto — encadenado con SEC-04:** una página de terceros puede inducir el navegador de un usuario autenticado a enviar un POST a `ProductosController?accion=guardarSalidas` con un `cantidad[]` arbitrario. Como el servidor tampoco valida el límite de stock (SEC-04), esto permite dejar el inventario en negativo sin que el usuario lo haya solicitado conscientemente — es el hallazgo de mayor impacto combinado de todo el informe.

**✅ Estado: Remediado** (commit `fix: SEC-03`). Se implementó el synchronizer token pattern: nueva clase pura `seguridad.CsrfTokens` (generación por sesión, comparación constant-time), minteado en `LoginController` tras el login. Al investigar la implementación se encontró un prerequisito que la auditoría original no había señalado: `ProductosController.doPost` delegaba directo a `doGet`, así que toda mutación (`insertar`, `guardarCambios`, `guardarSalidas`) era alcanzable por GET — un token CSRF solo no alcanzaba mientras eso siguiera así. Se separó en `doGet` (solo lectura) / `doPost` (solo mutación); una acción no reconocida ahora devuelve 405 en vez de caer en el dispatcher nulo que generaba una NPE latente (bug adicional cerrado de paso). `AuthFilter` valida el token mirando únicamente el método HTTP, sin necesidad de conocer la lista de acciones. Se auditaron los 6 JSPs: ningún link de mutación usaba GET salvo `LoginController?accion=salir` (logout), que queda como riesgo residual de bajo impacto fuera de este alcance.

---

### SEC-04 — Sin validación de stock mínimo en servidor (Alto)

**Descripción:** `ProductosDAO.retirarCantidad` ejecuta `UPDATE productos SET cantidad = cantidad - ? WHERE idProducto = ?` sin verificar que `cantidad - ? >= 0`. El único guard contra cantidades negativas está en JavaScript del lado cliente (`Productos/modificar.jsp:106-122`), que es trivialmente evitable con una request directa.

**Impacto:** cualquier usuario autenticado con permiso `sacar_inventario` (o, vía SEC-03, cualquiera cuyo navegador sea inducido a enviarlo) puede dejar `productos.cantidad` en negativo, corrompiendo la integridad de los datos de inventario — el propósito central de la aplicación.

**Evidencia:**
```java
// modelo/ProductosDAO.java:147-148
ps = conexion.prepareStatement(
    "UPDATE productos SET cantidad = cantidad - ? WHERE idProducto = ?"
);
```

**✅ Estado: Remediado** (commit `fix: SEC-04/SEC-05/SEC-06`). `retirarCantidad` ahora agrega `AND cantidad >= ?` al `UPDATE` y devuelve si se afectó exactamente una fila. Nueva clase pura `seguridad.WithdrawalOutcome` separa lo unit-testeable (la lógica de aceptar/rechazar en el controller) de lo que solo se puede verificar contra una base real (el predicado SQL en sí — documentado explícitamente como límite de cobertura honesto, no se finge un test que no existe). De paso se corrigió un bug encontrado durante la implementación: `Historico` registraba un movimiento "Salida" aunque el retiro no se aplicara; ahora solo loguea si la DAO confirmó que se afectó una fila.

---

### SEC-05 — Sin defensa contra session fixation (Alto)

**Descripción:** `LoginController.doPost:68` llama `request.getSession(true)` inmediatamente después de validar credenciales, sin invalidar antes ninguna sesión preexistente ni regenerar el ID de sesión. Confirmado por grep que este es el único `getSession(true)` en todo el código (los demás 5 call sites usan `getSession(false)`).

**Impacto:** si un atacante logra plantar un `JSESSIONID` en el navegador de la víctima antes del login (fijación clásica), `getSession(true)` reutiliza la sesión existente en vez de crear una nueva — el ID de sesión plantado por el atacante podría quedar autenticado tras el login legítimo.

**✅ Estado: Remediado** (commit `fix: SEC-04/SEC-05/SEC-06`). `LoginController` invalida cualquier sesión preexistente y crea una nueva antes de escribir nada en ella — más fuerte que solo rotar el `sessionId`, ya que descarta también cualquier atributo que un atacante haya plantado antes del login.

---

### SEC-06 — Sin `httpOnly`/`secure` en cookies, sin enforcement de HTTPS (Alto)

**Descripción:** `WEB-INF/web.xml` solo define `<session-timeout>30</session-timeout>`; no tiene `<cookie-config>` (por lo tanto sin flags `http-only`/`secure`), ni `<security-constraint>` con `transport-guarantee` forzando HTTPS. `META-INF/context.xml` tampoco configura nada al respecto.

**Impacto:** el cookie de sesión es accesible desde JavaScript (facilita robo vía XSS, aunque no se encontró XSS explotable en esta auditoría — ver Fortalezas) y puede viajar en texto plano si el deployment no fuerza HTTPS externamente.

**✅ Estado: Remediado (parcial por diseño)** (commit `fix: SEC-04/SEC-05/SEC-06`). `<cookie-config><http-only>true</http-only></cookie-config>` quedó habilitado sin condiciones (no rompe nada en localhost). `<secure>` quedó en `false` por defecto y el bloque `<security-constraint>`/`transport-guarantee` para forzar HTTPS quedó **comentado, no borrado**, con nota explicativa: el usuario confirmó que este proyecto es de despliegue local/portfolio únicamente, así que forzar `Secure`/HTTPS ahora rompería el desarrollo en HTTP plano sin ningún beneficio real. El `README.md` tiene un checklist "Despliegue más allá de localhost" con los pasos exactos para descomentar y habilitar esto el día que el proyecto se despliegue en un entorno real.

---

### SEC-07 — Mismatch de nombres de permisos SQL vs código (Medio, funcional)

**Descripción:** ya documentado en `CLAUDE.md` §"Permission model gotcha". Los nombres sembrados en `SCRIPTS/inventario_roles.sql:56-63` (`VER_INVENTARIO`, `AGREGAR_PRODUCTO`, `VER_SALIDA_PRODUCTOS`, `VER_HISTORIAL`, ...) no coinciden en mayúsculas/minúsculas ni en redacción con los checks en `ProductosController.java`/JSPs (`ver_inventario`, `agregar_productos`, `ver_salida`, `ver_historico`, ...). `PermisoDAO` hace match de string exacto sin normalizar case.

**Impacto:** no es un riesgo de escalamiento de privilegios — al contrario, el efecto es fail-closed: con el seed actual, ningún permiso matchea, por lo que todo usuario es redirigido a `sinPermiso` para toda acción. Es un bug de disponibilidad funcional, clasificado Medio porque compromete la usabilidad de los controles de autorización, no la seguridad de datos directamente.

**✅ Estado: Remediado** (commit `fix: SEC-07/SEC-01/SEC-02`, primero en el orden de implementación aunque es Medio por severidad — sin esto la app queda inutilizable y no se puede verificar nada de lo demás a mano). Se decidió corregir el seed SQL, no los checks de Java/JSP: el mismatch era de *redacción*, no solo de mayúsculas (`AGREGAR_PRODUCTO` vs `agregar_productos`, `VER_SALIDA_PRODUCTOS` vs `ver_salida`, `VER_HISTORIAL` vs `ver_historico`), así que normalizar el case en `PermisoDAO` no habría alcanzado. Los 7 nombres se extrajeron a una clase de constantes `seguridad.Permisos` (ya no hay literales de permiso repetidos en el controller), y se agregó una migración idempotente (`SCRIPTS/migrations/2026-07-28_01_permisos_rename.sql` + `_down`) para bases ya existentes. `CLAUDE.md` se actualizó para reflejar que este gotcha ya está cerrado.

---

### SEC-08 — Validación de input insuficiente (Medio)

**Descripción:** en `guardarCambios` (líneas 86-114) y `guardarSalidas` (129-156) de `ProductosController.java`:
- No hay null-check de `request.getParameterValues("id[]")` antes de usar `.length` → `NullPointerException` si el form es bypaseado y falta el parámetro.
- No hay chequeo de que `id[]`, `cantidad[]`, `estatus[]`, `modificado[]` tengan la misma longitud → `ArrayIndexOutOfBoundsException` con un POST con arrays de tamaños distintos.
- `Integer.parseInt(ids[i])`/`Integer.parseInt(cantidades[i])` sin validar formato → `NumberFormatException` con input no numérico.
- `web.xml` no define ningún `<error-page>`, por lo que cualquiera de estas excepciones no capturadas resulta en la página de error por defecto del contenedor, que en Tomcat en modo desarrollo típicamente expone el stack trace completo (nombres de clase, números de línea, rutas del servidor).

**Impacto:** información de la estructura interna de la aplicación expuesta a cualquier usuario autenticado que envíe una request malformada (accidental o intencionalmente).

**✅ Estado: Remediado** (validación: commit `fix: SEC-04/SEC-05/SEC-06`; error-page: commit `fix: SEC-08`). Nueva clase pura `seguridad.InventoryRequestValidator` valida null, longitud desigual entre `id[]`/`cantidad[]`/`estatus[]`/`modificado[]`, y formato numérico *antes* de parsear — devuelve 400 en vez de dejar pasar una excepción sin capturar. Se agregó `WEB-INF/error.jsp` (genérico, sin stack trace) enchufado en `web.xml` para `java.lang.Exception` y los códigos 400/403/404/405.

Nota de proceso: al desplegar el WAR reconstruido contra un Tomcat real (no solo `mvn test`), el `web.xml` con la `<error-page>` falló al parsear — tenía tres comentarios XML con un doble guion (`--`) suelto en el medio del texto, algo que XML prohíbe explícitamente y que ni `mvn test` ni `mvn clean package` detectan porque ninguno de los dos parsea `web.xml`. Se corrigió reemplazando el `--` por un guion largo (`—`) en los tres comentarios. `sdd-verify` marcó esto como un gap real del arnés de testing y sugirió agregar un test unitario que cargue `web.xml` con `DocumentBuilderFactory` (~10 líneas, sin dependencias nuevas) para que este tipo de bug no vuelva a pasar desapercibido hasta el deploy — **pendiente, no implementado todavía**.

---

### SEC-09 — Endpoint JAX-RS sin autenticación (Bajo)

**Descripción:** `JakartaEE10Resource.java` (`@Path("jakartaee10")`, montado en `@ApplicationPath("resources")`) queda expuesto en `/resources/jakartaee10` sin pasar por `AuthFilter`, cuyo `urlPatterns` solo cubre `/ProductosController` y `/Productos/*`.

**Impacto:** bajo — solo devuelve el string `"ping Jakarta EE"`, sin datos sensibles. Es boilerplate del arquetipo NetBeans, no usado por la aplicación real, pero amplía innecesariamente la superficie no autenticada.

**✅ Estado: Remediado (eliminado)** (commit `chore: SEC-09/SEC-11`). Se borró todo el paquete `com/mycompany/prueba1/` (`JakartaRestConfiguration.java` y `resources/JakartaEE10Resource.java`), confirmado por grep que no tenía ningún dependiente vivo en el resto del código ni en `web.xml`/`persistence.xml`.

---

### SEC-10 — Dependencias a vigilar (Bajo)

**🔍 Estado: sin cambios (monitoreo).** Ver apéndice de CVE más abajo — ninguna vulnerabilidad explotable confirmada en las versiones exactas usadas al 2026-07-28, así que no se tocó ninguna versión de dependencia. Sigue siendo un ítem a re-chequear periódicamente, no una acción pendiente.

---

### SEC-11 — Artefacto SQL muerto e inconsistente (Bajo)

**Descripción:** `src/main/resources/sql/crear_usuarios.sql` define una tabla `usuarios`/`clave` que no coincide con el esquema real (`Usuarios`/`contraseña` en `SCRIPTS/inventario_roles.sql`) usado por `UsuarioDAO`. No está referenciado por ningún código Java, pero siembra otra credencial `admin`/`admin` (línea 11).

**Impacto:** bajo directamente (no se ejecuta en el flujo normal), pero es un artefacto confuso que podría correrse por error contra la base equivocada, agregando otra credencial débil al sistema.

**✅ Estado: Remediado (eliminado)** (commit `chore: SEC-09/SEC-11`). Se borró `src/main/resources/sql/crear_usuarios.sql`.

---

## Fortalezas confirmadas

- **SEC-S1 — Sin inyección SQL.** Los 12 usos de `PreparedStatement`/`Statement.RETURN_GENERATED_KEYS` en `modelo/*.java` están 100% parametrizados con `?`. Grep exhaustivo de `Statement\s|createStatement` no encontró ningún `Statement` crudo, y grep de concatenación (`+ request.getParameter`) no encontró ningún resultado en todo `src/main/java`. `HistoricoDAO.listar()` construye SQL con `StringBuilder` pero solo concatena una cláusula literal fija (`" AND h.movimiento = ?"`), nunca el valor del usuario — el valor sigue bindeado vía `ps.setString(...)`.
- **SEC-S2 — Sin XSS de texto libre.** Todos los valores de string provenientes de DB o de sesión (`producto.nombre`, `r.nombreProducto`, `r.nombreUsuario`, `r.movimiento`, `error`, `sessionScope.usuario`, `pageTitle`) están envueltos en `<c:out>` en los 6 JSPs revisados. La única interpolación EL cruda encontrada es sobre valores booleanos/numéricos derivados de la DB (`producto.id`, `producto.cantidad`, `producto.estatus`) usados en atributos `data-*`/`value`/nombres de clase — sin riesgo real de XSS porque no son texto libre. No se encontraron scriptlets `<% %>` que evadan el escaping de JSTL.
- **SEC-S3 — Sin deserialización insegura ni ejecución de comandos.** Grep de `Runtime.exec|ProcessBuilder|ObjectInputStream|readObject` y de `new URL|HttpClient|URLConnection` no encontró ningún resultado — no hay superficie de deserialización insegura (A08) ni de SSRF (A10), consistente con ser una app JDBC+Servlet sin llamadas salientes.
- **SEC-S4 — Autorización sin bypass de DAO.** Se confirmó que las 4 DAOs (`ProductosDAO`, `HistoricoDAO`, `UsuarioDAO`, `PermisoDAO`) solo se instancian dentro de `ProductosController` y `LoginController` — ningún JSP ni otro punto de entrada accede a los DAOs saltándose el gate de permisos del controller.

---

## Apéndice: dependencias (lookup 2026-07-28)

| Dependencia | Versión | Resultado del lookup |
|---|---|---|
| `com.mysql:mysql-connector-j` | 8.3.0 | Sin CVEs conocidos que afecten esta versión. Vulnerabilidades documentadas (CVE-2023-22102, acceso indebido en <8.2.0; RCE en <8.0.33; permisos incorrectos en [9.0.0, 9.3.0)) no aplican — 8.3.0 queda fuera de todos los rangos afectados. |
| `jakarta.platform:jakarta.jakartaee-api` | 10.0.0 | Sin CVEs específicos encontrados para este artefacto/versión. |
| `jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api` | 3.0.0 | Sin CVEs encontrados (es solo el artefacto de API/interfaces). |
| `org.glassfish.web:jakarta.servlet.jsp.jstl` | 3.0.1 | Un reporte de scanner (CVE-2022-42920) menciona esta versión, pero corresponde a una dependencia transitiva shaded de Apache BCEL, no al código JSTL en sí — el propio reporte original lo marca como no confirmado/posible falso positivo. Se registra como **no concluyente**, no como vulnerabilidad confirmada. |
| `maven-compiler-plugin` (build) | 3.8.1 | Sin CVEs directos. CVEs transitivos (CVE-2022-29599 en maven-shared-utils, CVE-2021-26291 en resolución de repos) no son explotables en este build — no invoca procesos externos con input de atacante ni usa repositorios HTTP no confiables. |
| `maven-war-plugin` (build) | 3.3.2 | Sin CVEs encontrados. |

Nota adicional: `pom.xml` fija el compilador a Java 11 (`<source>11</source>`) mientras `README.md:7` indica JDK 25 para desarrollo en NetBeans — es una inconsistencia de build hygiene, no un hallazgo de seguridad.

---

## Preguntas abiertas / riesgos aceptados para la escala de este proyecto

- **Sin lockout/rate-limiting en login:** `LoginController` no limita intentos fallidos. Dado que es un sistema seedeado con 2 usuarios y sin exposición pública conocida, se registra como riesgo aceptado para este pase — no se detalla como hallazgo a remediar en profundidad, pero debería revisarse si la app llega a exponerse fuera de una red controlada. Sigue sin implementarse tras la remediación — fue un no-objetivo explícito de la propuesta.
- **Sin logging/observabilidad más allá de `System.out.println`:** 24 ocurrencias de `System.out.print`/`printStackTrace` en 6 archivos de `modelo/`+`controlador/`+`config/`. No hay forma de detectar en producción si algo está fallando silenciosamente (A09). No se profundiza como hallazgo bloqueante dado que no existe stack de observabilidad en este proyecto, pero queda anotado. Tampoco se tocó en la remediación.

## Verificaciones manuales pendientes

Tres ítems de la implementación quedaron marcados como verificación manual únicamente (no automatizable con las herramientas de esta sesión), pendientes de confirmación explícita por el equipo:

- Rechazo explícito de un POST con token CSRF ausente o incorrecto (SEC-03).
- Un retiro que supere el stock disponible queda rechazado contra una base MySQL real, y las flags `HttpOnly`/`Secure` de la cookie de sesión se ven correctamente en las devtools del navegador (SEC-04/SEC-06).
- Disparar a mano una excepción no capturada y confirmar que se ve la página de error genérica, no el stack trace de Tomcat (SEC-08).

El login funcional con ambos usuarios demo y los permisos por rol sí se confirmaron contra un despliegue real (Tomcat 10.1.52 + MySQL 8.0).
