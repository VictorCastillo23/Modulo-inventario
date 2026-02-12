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

En la clase de conexión:

  Connection conexion = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/inventario_roles?serverTimezone=UTC", "root", "admin"
  );

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
