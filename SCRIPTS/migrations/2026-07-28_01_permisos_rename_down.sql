-- Down-migration for 2026-07-28_01_permisos_rename
-- Restores the original UPPER_SNAKE permission names.
--
-- Idempotent: matches on either name, so re-running after it has already
-- applied updates zero rows.
--
-- Usage: mysql -u <user> -p inventario_roles < 2026-07-28_01_permisos_rename_down.sql

USE inventario_roles;

UPDATE Permisos SET nombre = 'VER_INVENTARIO'
    WHERE nombre IN ('ver_inventario', 'VER_INVENTARIO');

UPDATE Permisos SET nombre = 'AGREGAR_PRODUCTO'
    WHERE nombre IN ('agregar_productos', 'AGREGAR_PRODUCTO');

UPDATE Permisos SET nombre = 'AUMENTAR_INVENTARIO'
    WHERE nombre IN ('aumentar_inventario', 'AUMENTAR_INVENTARIO');

UPDATE Permisos SET nombre = 'BAJA_REACTIVAR_PRODUCTO'
    WHERE nombre IN ('baja_reactivar_producto', 'BAJA_REACTIVAR_PRODUCTO');

UPDATE Permisos SET nombre = 'VER_SALIDA_PRODUCTOS'
    WHERE nombre IN ('ver_salida', 'VER_SALIDA_PRODUCTOS');

UPDATE Permisos SET nombre = 'SACAR_INVENTARIO'
    WHERE nombre IN ('sacar_inventario', 'SACAR_INVENTARIO');

UPDATE Permisos SET nombre = 'VER_HISTORIAL'
    WHERE nombre IN ('ver_historico', 'VER_HISTORIAL');
