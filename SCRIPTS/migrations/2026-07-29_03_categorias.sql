-- Migration: 2026-07-29_03_categorias
-- Adds the Categorias module: new Categorias table, a nullable FK column
-- (idCategoria) on Productos, and two new permissions (ver_categorias,
-- gestionar_categorias) granted to Administrador (both) and Almacenista
-- (ver_categorias only) — matches the grant pattern used for
-- ver_inventario.
--
-- Idempotent: table creation uses IF NOT EXISTS; the ALTER TABLE/FK are
-- guarded via information_schema so re-running after they've already
-- applied is a no-op; permission/grant/seed inserts are guarded by
-- NOT EXISTS checks so each inserts at most once.
--
-- Usage: mysql -u <user> -p inventario_roles < 2026-07-29_03_categorias.sql

USE inventario_roles;

CREATE TABLE IF NOT EXISTS Categorias (
    idCategoria INT(4) AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(150),
    estatus INT(1) NOT NULL DEFAULT 1
);

SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'Productos' AND COLUMN_NAME = 'idCategoria'
);
SET @sql := IF(@col_exists = 0,
    'ALTER TABLE Productos ADD COLUMN idCategoria INT(4) NULL AFTER estatus',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists := (
    SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'Productos' AND CONSTRAINT_NAME = 'fk_productos_categoria'
);
SET @sql := IF(@fk_exists = 0,
    'ALTER TABLE Productos ADD CONSTRAINT fk_productos_categoria FOREIGN KEY (idCategoria) REFERENCES Categorias(idCategoria)',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

INSERT INTO Permisos (nombre, descripcion)
SELECT 'ver_categorias', 'Permite ver y filtrar por categorías de producto'
WHERE NOT EXISTS (SELECT 1 FROM Permisos WHERE nombre = 'ver_categorias');

INSERT INTO Permisos (nombre, descripcion)
SELECT 'gestionar_categorias', 'Permite crear, editar y dar de baja categorías'
WHERE NOT EXISTS (SELECT 1 FROM Permisos WHERE nombre = 'gestionar_categorias');

INSERT INTO Roles_Permisos (idRol, idPermisos)
SELECT 1, (SELECT idPermiso FROM Permisos WHERE nombre = 'ver_categorias')
WHERE NOT EXISTS (
    SELECT 1 FROM Roles_Permisos
    WHERE idRol = 1 AND idPermisos = (SELECT idPermiso FROM Permisos WHERE nombre = 'ver_categorias')
);

INSERT INTO Roles_Permisos (idRol, idPermisos)
SELECT 1, (SELECT idPermiso FROM Permisos WHERE nombre = 'gestionar_categorias')
WHERE NOT EXISTS (
    SELECT 1 FROM Roles_Permisos
    WHERE idRol = 1 AND idPermisos = (SELECT idPermiso FROM Permisos WHERE nombre = 'gestionar_categorias')
);

INSERT INTO Roles_Permisos (idRol, idPermisos)
SELECT 2, (SELECT idPermiso FROM Permisos WHERE nombre = 'ver_categorias')
WHERE NOT EXISTS (
    SELECT 1 FROM Roles_Permisos
    WHERE idRol = 2 AND idPermisos = (SELECT idPermiso FROM Permisos WHERE nombre = 'ver_categorias')
);

INSERT INTO Categorias (nombre, descripcion, estatus)
SELECT 'Electrónica', 'Dispositivos y componentes electrónicos', 1
WHERE NOT EXISTS (SELECT 1 FROM Categorias WHERE nombre = 'Electrónica');

INSERT INTO Categorias (nombre, descripcion, estatus)
SELECT 'Papelería', 'Insumos de oficina y papelería', 1
WHERE NOT EXISTS (SELECT 1 FROM Categorias WHERE nombre = 'Papelería');

INSERT INTO Categorias (nombre, descripcion, estatus)
SELECT 'Limpieza', 'Productos de limpieza e higiene', 1
WHERE NOT EXISTS (SELECT 1 FROM Categorias WHERE nombre = 'Limpieza');

INSERT INTO Categorias (nombre, descripcion, estatus)
SELECT 'Herramientas', 'Herramientas manuales y eléctricas', 1
WHERE NOT EXISTS (SELECT 1 FROM Categorias WHERE nombre = 'Herramientas');
