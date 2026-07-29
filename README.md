# Almacén — Sistema de Gestión de Inventario

Aplicación web para el control de inventario de un almacén: alta y baja de
productos, entradas y salidas de stock, histórico de movimientos y permisos
diferenciados por rol (Administrador / Almacenista).

![Java](https://img.shields.io/badge/Java-11%2B-ED8B00?logo=openjdk&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-3C873A?logo=eclipseide&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3?logo=bootstrap&logoColor=white)

## Índice

- [Stack técnico](#stack-técnico)
- [Configuración de la base de datos](#configuración-de-la-base-de-datos)
- [Instalación y ejecución](#instalación-y-ejecución-por-terminal-sin-netbeans)
- [Usuarios de prueba](#usuarios-de-prueba)
- [Checklist de despliegue en producción](#despliegue-más-allá-de-localhost-checklist)

## Stack técnico

| Capa                     | Tecnología                                    |
| ------------------------ | ---------------------------------------------- |
| IDE                      | Apache NetBeans 28 (JDK 25)                    |
| Lenguaje y plataforma    | Java (compilación Maven: JDK 11+), Jakarta EE 10, JSTL |
| Servidor de aplicaciones | Apache Tomcat 10.1.52                          |
| Base de datos            | MySQL Server 8.1                               |
| JDBC                     | MySQL Connector/J (`com.mysql.cj.jdbc.Driver`) |
| Arquitectura             | MVC (Servlets + JSP, DAOs con JDBC puro)       |
| Frontend                 | Bootstrap 5.3, Bootstrap Icons                 |

## Configuración de la base de datos

Ejecutar el script `SCRIPTS/inventario_roles.sql` de este repositorio. Esto
crea la base de datos con las tablas necesarias y algunos registros de
prueba.

## Instalación y ejecución (por terminal, sin NetBeans)

**Requisitos:** JDK 11+ instalado, Maven (`mvn`) en el PATH, un Apache
Tomcat 10.1.x standalone descomprimido en algún lado (no hace falta el que
trae NetBeans, sirve cualquier distribución oficial), y MySQL 8.x corriendo
con la base ya creada (ver [Configuración de la base de datos](#configuración-de-la-base-de-datos)).

**1. Compilar el WAR**

```powershell
cd C:\ruta\al\proyecto
mvn clean package
```

Esto genera `target/prueba1-1.0-SNAPSHOT.war`.

**2. Configurar las variables de entorno de la base de datos**

La clase de conexión (`config.Conexion`) ya NO contiene credenciales
hardcodeadas — `config.StartupConfigListener` falla el despliegue
inmediatamente si falta cualquiera de las tres, en lugar de fallar
silenciosamente en la primera consulta:

| Variable      | Ejemplo                                                            |
| -------------- | ------------------------------------------------------------------- |
| `DB_URL`      | `jdbc:mysql://localhost:3306/inventario_roles?serverTimezone=UTC` |
| `DB_USER`     | `root`                                                             |
| `DB_PASSWORD` | `admin`                                                            |

**3. Copiar el WAR a la carpeta `webapps` de Tomcat**

```powershell
Copy-Item target\prueba1-1.0-SNAPSHOT.war "C:\ruta\a\apache-tomcat-10.1.52\webapps\prueba1.war" -Force
```

**4. Arrancar Tomcat**

Con las variables de entorno seteadas en la misma ventana de PowerShell (no
se heredan entre ventanas distintas):

```powershell
$env:CATALINA_HOME = "C:\ruta\a\apache-tomcat-10.1.52"
$env:JAVA_HOME = "C:\ruta\a\tu\jdk"
$env:DB_URL = "jdbc:mysql://localhost:3306/inventario_roles?serverTimezone=UTC"
$env:DB_USER = "root"
$env:DB_PASSWORD = "tu-contraseña-real"

& "$env:CATALINA_HOME\bin\startup.bat"
```

Se abre una ventana nueva de consola ("Tomcat") — dejarla abierta y esperar
a ver `Server startup in [xxxx] ms` sin ningún `SEVERE` antes.

**5. Probar en el navegador**

```
http://localhost:8080/prueba1/
```

Redirige solo a la pantalla de login.

**6. Para parar el servidor**

```powershell
& "$env:CATALINA_HOME\bin\shutdown.bat"
```

Si la ventana no responde, cerrar el proceso `java.exe` correspondiente
desde el Administrador de tareas.

## Usuarios de prueba

| Rol            | Usuario   | Contraseña |
| --------------- | --------- | ---------- |
| Administrador  | `admin`   | `admin`    |
| Almacenista    | `almacen` | `almacen`  |

## Despliegue más allá de localhost (checklist)

La configuración por defecto (`WEB-INF/web.xml`) está pensada para
desarrollo local sobre HTTP plano. Antes de desplegar este proyecto en
cualquier entorno accesible fuera de `localhost` (staging, producción, demo
pública), completar lo siguiente:

- [ ] **Servir la aplicación detrás de TLS** (certificado válido en el
      balanceador/reverse proxy o directamente en el conector de Tomcat).
      Sin un listener HTTPS activo, los pasos siguientes rompen la
      aplicación en lugar de protegerla.
- [ ] **Habilitar la cookie `Secure`**: en `WEB-INF/web.xml`, cambiar
      `<cookie-config><secure>false</secure></cookie-config>` a `true`. Una
      cookie `Secure` es descartada silenciosamente por el navegador sobre
      HTTP plano, así que este cambio solo es seguro **después** del paso
      anterior.
- [ ] **Descomentar el bloque `<security-constraint>`** (con
      `<transport-guarantee>CONFIDENTIAL</transport-guarantee>`) al final
      de `WEB-INF/web.xml`. Esto fuerza a Tomcat a redirigir todo tráfico
      HTTP a HTTPS; también depende del paso de TLS.
- [ ] **Confirmar las variables de entorno de base de datos**
      (`DB_URL`/`DB_USER`/`DB_PASSWORD`) apuntan al entorno correcto y no a
      `localhost` — `config.StartupConfigListener` falla el despliegue si
      faltan, pero no valida que apunten al host correcto.
- [ ] **Revisar `session-timeout`** (`web.xml`, actualmente 30 minutos)
      según la política de seguridad del entorno de destino.
- [ ] **No reutilizar los usuarios semilla** (`admin`/`admin`,
      `almacen`/`almacen`) fuera de un entorno de desarrollo/demo — rotar
      contraseñas antes de exponer la aplicación.
