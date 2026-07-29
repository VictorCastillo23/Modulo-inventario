CREATE DATABASE inventario_roles;
USE inventario_roles;

CREATE TABLE Roles (
    idRol INT(2) AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(25) NOT NULL
);

CREATE TABLE Permisos (
    idPermiso INT(2) AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(100)
);

CREATE TABLE Roles_Permisos (
    idRol_Permiso INT(3) AUTO_INCREMENT PRIMARY KEY,
    idRol INT(2) NOT NULL,
    idPermisos INT(2) NOT NULL,

    FOREIGN KEY (idRol) REFERENCES Roles(idRol),
    FOREIGN KEY (idPermisos) REFERENCES Permisos(idPermiso)
);

CREATE TABLE Usuarios (
    idUsuario INT(6) AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(50) NOT NULL,
    contraseña VARCHAR(60) NOT NULL,
    idRol INT(2) NOT NULL,
    estatus INT(1) NOT NULL,

    FOREIGN KEY (idRol) REFERENCES Roles(idRol)
);

CREATE TABLE Productos (
    idProducto INT(6) AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cantidad INT(6) NOT NULL DEFAULT 0,
    estatus INT(1) NOT NULL
);

CREATE TABLE Historico (
    idHistorico INT(6) AUTO_INCREMENT PRIMARY KEY,
    idUsuario INT(6) NOT NULL,
    idProducto INT(6) NOT NULL,
    movimiento VARCHAR(10) NOT NULL,
    cantidad INT(6) NOT NULL,
    fecha DATETIME NOT NULL,
    FOREIGN KEY (idUsuario) REFERENCES Usuarios(idUsuario),
    FOREIGN KEY (idProducto) REFERENCES Productos(idProducto)
);

INSERT INTO Roles (nombre) VALUES
('Administrador'),('Almacenista');

-- Permission names MUST exactly match seguridad.Permisos and the EL literals
-- read directly by Productos/index.jsp, Productos/modificar.jsp, and
-- Productos/historial.jsp (see seguridad/Permisos.java Javadoc).
INSERT INTO Permisos (nombre, descripcion) VALUES
('ver_inventario', 'Permite ver el módulo de inventario'),
('agregar_productos', 'Permite agregar nuevos productos'),
('aumentar_inventario', 'Permite registrar entradas de inventario'),
('baja_reactivar_producto', 'Permite dar de baja o reactivar productos'),
('ver_salida', 'Permite ver el módulo de salida de productos'),
('sacar_inventario', 'Permite registrar salidas de inventario'),
('ver_historico', 'Permite ver el historial de movimientos');

INSERT INTO Roles_Permisos (idRol, idPermisos) VALUES
(1, 1),(1, 2),(1, 3),(1, 4),(1, 7);

INSERT INTO Roles_Permisos (idRol, idPermisos) VALUES
(2, 1),(2, 5),(2, 6);

-- Passwords below are BCrypt hashes (cost 10) of the demo plaintext
-- passwords 'admin' and 'almacen', generated with seguridad.PasswordHasher.
-- The plaintext demo credentials themselves are unchanged (see README.md).
INSERT INTO Usuarios (nombre, correo, contraseña, idRol, estatus) VALUES
('Victor', 'admin', '$2a$10$1i90KtcPfXIlpRyYdEsj9e8A4HAIar7kJ8e9TreX/MYZHmsZrBK02', 1, 1),
('JAVIER', 'almacen', '$2a$10$wY1e94oBVkJC9gALVeZvfeSfLCSdxGgxffkBmKidll7y9XnjGP/aa', 2, 1);

INSERT INTO Productos (nombre, cantidad, estatus) VALUES
('Producto 01', 0, 1),('Producto 02', 0, 1),('Producto 03', 0, 1),('Producto 04', 0, 1),
('Producto 05', 0, 1),('Producto 06', 0, 1),('Producto 07', 0, 1),('Producto 08', 0, 1),
('Producto 09', 0, 1),('Producto 10', 0, 1),('Producto 11', 0, 1),('Producto 12', 0, 1),
('Producto 13', 0, 1),('Producto 14', 0, 1),('Producto 15', 0, 1),('Producto 16', 0, 1),
('Producto 17', 0, 1),('Producto 18', 0, 1),('Producto 19', 0, 1),('Producto 20', 0, 1),
('Producto 21', 0, 1),('Producto 22', 0, 1),('Producto 23', 0, 1),('Producto 24', 0, 1),
('Producto 25', 0, 0),('Producto 26', 0, 0),('Producto 27', 0, 0),('Producto 28', 0, 0),
('Producto 29', 0, 0),('Producto 30', 0, 0),('Producto 31', 0, 0),('Producto 32', 0, 0),
('Producto 33', 0, 0),('Producto 34', 0, 0),('Producto 35', 0, 0),('Producto 36', 0, 0),
('Producto 37', 0, 0),('Producto 38', 0, 0),('Producto 39', 0, 0),('Producto 40', 0, 0);
