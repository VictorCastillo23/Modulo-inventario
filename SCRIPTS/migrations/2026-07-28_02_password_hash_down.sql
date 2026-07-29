-- Down-migration for 2026-07-28_02_password_hash
-- Restores the demo accounts' plaintext passwords and shrinks the column
-- back to VARCHAR(25).
--
-- Idempotent: same fixed values every run; MODIFY COLUMN is repeatable.
--
-- Usage: mysql -u <user> -p inventario_roles < 2026-07-28_02_password_hash_down.sql

USE inventario_roles;

UPDATE Usuarios SET contraseña = 'admin'
    WHERE correo = 'admin';

UPDATE Usuarios SET contraseña = 'almacen'
    WHERE correo = 'almacen';

ALTER TABLE Usuarios MODIFY contraseña VARCHAR(25) NOT NULL;
