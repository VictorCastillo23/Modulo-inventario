__IDE Utilizado

Apache NetBeans 28

Lenguaje y Plataforma

* JDK 25
* Jakarta EE
* JSTL

Servidor de Aplicaciones

* Apache Tomcat 10.1.52

Base de Datos

* MySQL Server 8.1

JDBC

* MySQL Connector/J com.mysql.cj.jdbc.Driver


Arquitectura del Sistema 

* MVC

__Configuración de la Base de Datos

  Ejecutar el Script 'inventario_roles' que esta en la carpeta SCRIPTS en este mismo repositorio
  esto creara la BD con las tablas necesarias y algunos registros de prueba 

__Configuración del Proyecto

Configuración del Servidor

1. Abrir NetBeans.
2. Ir a:

   Tools → Servers

3. Agregar:

   Apache Tomcat 10.1.52

4. Configuración de la Conexión a Base de Datos

La clase de conexión (`config.Conexion`) ya NO contiene credenciales
hardcodeadas. Debe configurar las siguientes variables de entorno (o
propiedades de sistema, como fallback, ej. `-DDB_URL=...` en la
configuración de ejecución del servidor en NetBeans) antes de desplegar:

| Variable      | Ejemplo                                                        |
| ------------- | --------------------------------------------------------------- |
| `DB_URL`      | `jdbc:mysql://localhost:3306/inventario_roles?serverTimezone=UTC` |
| `DB_USER`     | `root`                                                           |
| `DB_PASSWORD` | `admin`                                                          |

Si falta cualquiera de las tres, el despliegue falla inmediatamente
(`config.StartupConfigListener` lo valida al iniciar el contexto) en lugar
de fallar silenciosamente en la primera consulta a la base de datos.

5. Verificar que el driver esté agregado en:

Project Properties → Libraries → Add JAR/Folder


__Pasos para Ejecutar la Aplicación

1. Clonar o importar el proyecto en NetBeans.
2. Configurar el servidor Apache Tomcat 10.1.52.
3. Crear la base de datos en MySQL 8.1.
4. Configurar credenciales en la clase de conexión.
5. Limpiar y construir el proyecto.
6. Ejecutar el proyecto (Run).

La aplicación estará disponible en:

http://localhost:8080/NombreDelProyecto


__Pasos para iniciar la aplicacion 

El usuario con permisos de administrador 

usuario     : admin 
contraseña  : admin 

El usuario con permisos de almacenista 

usuario     : almacen 
contraseña  : almacen

__Despliegue más allá de localhost (checklist)

La configuración por defecto (`WEB-INF/web.xml`) está pensada para
desarrollo local sobre HTTP plano. Antes de desplegar este proyecto en
cualquier entorno accesible fuera de `localhost` (staging, producción,
demo pública), completar lo siguiente:

1. **Servir la aplicación detrás de TLS** (certificado válido en el
   balanceador/reverse proxy o directamente en el conector de Tomcat).
   Sin un listener HTTPS activo, los pasos 2 y 3 rompen la aplicación en
   lugar de protegerla.
2. **Habilitar la cookie `Secure`**: en `WEB-INF/web.xml`, cambiar
   `<cookie-config><secure>false</secure></cookie-config>` a `true`. Una
   cookie `Secure` es descartada silenciosamente por el navegador sobre
   HTTP plano, así que este cambio solo es seguro **después** del paso 1.
3. **Descomentar el bloque `<security-constraint>`** (con
   `<transport-guarantee>CONFIDENTIAL</transport-guarantee>`) al final de
   `WEB-INF/web.xml`. Esto fuerza a Tomcat a redirigir todo tráfico HTTP a
   HTTPS; también depende del paso 1.
4. **Confirmar las variables de entorno de base de datos**
   (`DB_URL`/`DB_USER`/`DB_PASSWORD`) apuntan al entorno correcto y no a
   `localhost` — `config.StartupConfigListener` falla el despliegue si
   faltan, pero no valida que apunten al host correcto.
5. **Revisar `session-timeout`** (`web.xml`, actualmente 30 minutos) según
   la política de seguridad del entorno de destino.
6. **No reutilizar los usuarios semilla** (`admin`/`admin`,
   `almacen`/`almacen`) fuera de un entorno de desarrollo/demo — rotar
   contraseñas antes de exponer la aplicación.
