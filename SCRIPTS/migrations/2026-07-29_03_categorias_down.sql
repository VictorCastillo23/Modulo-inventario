-- Down-migration for 2026-07-29_03_categorias.
-- Usage: mysql -u <user> -p inventario_roles < 2026-07-29_03_categorias_down.sql

USE inventario_roles;

DELETE rp FROM Roles_Permisos rp
    JOIN Permisos p ON rp.idPermisos = p.idPermiso
    WHERE p.nombre IN ('ver_categorias', 'gestionar_categorias');

DELETE FROM Permisos WHERE nombre IN ('ver_categorias', 'gestionar_categorias');

SET @fk_exists := (
    SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'Productos' AND CONSTRAINT_NAME = 'fk_productos_categoria'
);
SET @sql := IF(@fk_exists > 0,
    'ALTER TABLE Productos DROP FOREIGN KEY fk_productos_categoria',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'Productos' AND COLUMN_NAME = 'idCategoria'
);
SET @sql := IF(@col_exists > 0,
    'ALTER TABLE Productos DROP COLUMN idCategoria',
    'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

DROP TABLE IF EXISTS Categorias;
