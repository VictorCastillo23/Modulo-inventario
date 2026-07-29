-- Migration: 2026-07-28_02_password_hash
-- SEC-02: widen Usuarios.contraseña to fit a 60-char BCrypt hash and
-- re-seed the two demo accounts' hashes (generated with
-- seguridad.PasswordHasher, cost 10). Any other existing accounts are left
-- untouched — this migration only knows about the demo seed rows.
--
-- Idempotent: MODIFY COLUMN is repeatable; the UPDATE statements set the
-- same fixed hash value every run, so a second run reports zero changed
-- rows once applied.
--
-- Usage: mysql -u <user> -p inventario_roles < 2026-07-28_02_password_hash.sql

USE inventario_roles;

ALTER TABLE Usuarios MODIFY contraseña VARCHAR(60) NOT NULL;

UPDATE Usuarios SET contraseña = '$2a$10$1i90KtcPfXIlpRyYdEsj9e8A4HAIar7kJ8e9TreX/MYZHmsZrBK02'
    WHERE correo = 'admin';

UPDATE Usuarios SET contraseña = '$2a$10$wY1e94oBVkJC9gALVeZvfeSfLCSdxGgxffkBmKidll7y9XnjGP/aa'
    WHERE correo = 'almacen';
