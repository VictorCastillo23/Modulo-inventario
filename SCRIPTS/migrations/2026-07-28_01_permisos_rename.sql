-- Migration: 2026-07-28_01_permisos_rename
-- SEC-07: rename Permisos.nombre from UPPER_SNAKE (never matched by the
-- lowercase names checked in ProductosController/JSPs) to the exact lowercase
-- names defined in seguridad.Permisos.
--
-- Idempotent: matches on either the old or the new name, so re-running this
-- script after it has already applied updates zero rows.
--
-- Usage: mysql -u <user> -p inventario_roles < 2026-07-28_01_permisos_rename.sql

USE inventario_roles;

UPDATE Permisos SET nombre = 'ver_inventario'
    WHERE nombre IN ('VER_INVENTARIO', 'ver_inventario');

UPDATE Permisos SET nombre = 'agregar_productos'
    WHERE nombre IN ('AGREGAR_PRODUCTO', 'agregar_productos');

UPDATE Permisos SET nombre = 'aumentar_inventario'
    WHERE nombre IN ('AUMENTAR_INVENTARIO', 'aumentar_inventario');

UPDATE Permisos SET nombre = 'baja_reactivar_producto'
    WHERE nombre IN ('BAJA_REACTIVAR_PRODUCTO', 'baja_reactivar_producto');

UPDATE Permisos SET nombre = 'ver_salida'
    WHERE nombre IN ('VER_SALIDA_PRODUCTOS', 'ver_salida');

UPDATE Permisos SET nombre = 'sacar_inventario'
    WHERE nombre IN ('SACAR_INVENTARIO', 'sacar_inventario');

UPDATE Permisos SET nombre = 'ver_historico'
    WHERE nombre IN ('VER_HISTORIAL', 'ver_historico');
