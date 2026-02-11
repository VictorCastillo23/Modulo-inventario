-- Ejecutar en la base de datos del proyecto.
-- Crea tablas Roles, Permisos, Roles_Permisos e inserta datos según la matriz de permisos.
-- Asegúrate de que la tabla de usuarios tenga la columna idRol (INT) y FK a Roles(idRol).

-- Tabla de roles
CREATE TABLE IF NOT EXISTS Roles (
    idRol INT(2) AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(25) NOT NULL
);

-- Tabla de permisos
CREATE TABLE IF NOT EXISTS Permisos (
    idPermiso INT(2) AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(100)
);

-- Tabla relación roles-permisos (idPermiso referencia a Permisos.idPermiso)
CREATE TABLE IF NOT EXISTS Roles_Permisos (
    idRol_Permiso INT(3) AUTO_INCREMENT PRIMARY KEY,
    idRol INT(2) NOT NULL,
    idPermiso INT(2) NOT NULL,
    UNIQUE KEY uk_rol_permiso (idRol, idPermiso),
    FOREIGN KEY (idRol) REFERENCES Roles(idRol),
    FOREIGN KEY (idPermiso) REFERENCES Permisos(idPermiso)
);

-- Insertar roles (1=Administrador, 2=Almacenista)
INSERT INTO Roles (idRol, nombre) VALUES (1, 'Administrador'), (2, 'Almacenista')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Insertar permisos con nombre usado en la aplicación
INSERT INTO Permisos (idPermiso, nombre, descripcion) VALUES
(1, 'ver_inventario', 'Ver módulo inventario'),
(2, 'agregar_productos', 'Agregar nuevos productos'),
(3, 'aumentar_inventario', 'Aumentar inventario'),
(4, 'baja_reactivar_producto', 'Dar de baja/reactivar un producto'),
(5, 'ver_salida', 'Ver módulo Salida de productos'),
(6, 'sacar_inventario', 'Sacar inventario del almacén'),
(7, 'ver_historico', 'Ver módulo del histórico')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);

-- Administrador (idRol=1): ver_inventario, agregar_productos, aumentar_inventario, baja_reactivar_producto, ver_historico
INSERT IGNORE INTO Roles_Permisos (idRol, idPermiso) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 7);

-- Almacenista (idRol=2): ver_inventario, ver_salida, sacar_inventario
INSERT IGNORE INTO Roles_Permisos (idRol, idPermiso) VALUES
(2, 1), (2, 5), (2, 6);

-- Opcional: si tu tabla de usuarios se llama 'usuarios' y no tiene idRol, descomenta y adapta:
-- ALTER TABLE usuarios ADD COLUMN idRol INT(2) NULL;
-- ALTER TABLE usuarios ADD CONSTRAINT fk_usuarios_rol FOREIGN KEY (idRol) REFERENCES Roles(idRol);
-- UPDATE usuarios SET idRol = 1 WHERE usuario = 'admin';
